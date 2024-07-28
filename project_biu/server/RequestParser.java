package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    /**
     * Parses an HTTP request from a BufferedReader and extracts relevant information.
     *
     * @param reader A BufferedReader containing the HTTP request.
     * @return A RequestInfo object containing the parsed request information, or null if the request is invalid.
     * @throws IOException If an I/O error occurs while reading from the BufferedReader.
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        String httpCommand;
        String uri;
        String boundary = "";
        String[] uriSegments;
        Map<String, String> parameters = new HashMap<>();
        byte[] content;

        if (line == null || line.isEmpty())
            return null;

        String[] requestLineParts = line.split(" ");
        if (requestLineParts.length < 3)
            return null;

        httpCommand = requestLineParts[0];
        uri = requestLineParts[1];

        // Extract URI segments
        String path = uri.split("\\?")[0];
        uriSegments = Arrays.stream(path.split("/")).filter(segment -> !segment.isEmpty())
                .toArray(String[]::new);

        // Parse parameters from URI
        if (uri.contains("?")) {
            String queryString = uri.split("\\?")[1];
            String[] queryParams = queryString.split("&");
            for (String param : queryParams) {
                String[] paramParts = param.split("=");
                if (paramParts.length == 2) {
                    parameters.put(paramParts[0], paramParts[1]);
                }
            }
        }

        // read until empty line
        while (reader.ready()) {
            line = reader.readLine();
            if (line.isEmpty())
                break;
            if (line.contains("boundary=")) {
                boundary = line.split("boundary=")[1];
            }
        }

        // read until empty line
        while (reader.ready()) {
            line = reader.readLine();
            if (line.isEmpty())
                break;
            if (line.contains("filename=")){
                parameters.put("filename", line.split("filename=")[1].replace("\"",""));
                continue;
            }
            String[] paramParts = line.split("=");
            if (paramParts.length == 2) {
                parameters.put(paramParts[0], paramParts[1]);
            }
        }

        // Read content after the second empty line
        StringBuilder contentBuilder = new StringBuilder();
        while (reader.ready()) {
            line = reader.readLine();
            if (!boundary.isEmpty() && line.contains(boundary))
                break;

            contentBuilder.append(line).append("\n");
        }
        if(!contentBuilder.isEmpty())
            contentBuilder.deleteCharAt(contentBuilder.length() - 1);
        content = contentBuilder.toString().getBytes();

        return new RequestInfo(httpCommand, uri, uriSegments, parameters, content);
    }

    /**
     * A class representing parsed information from an HTTP request.
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        /**
         * Constructs a new RequestInfo object.
         *
         * @param httpCommand The HTTP command (e.g., GET, POST).
         * @param uri         The full URI of the request.
         * @param uriSegments The segments of the URI path.
         * @param parameters  The parameters extracted from the URI or request body.
         * @param content     The content of the request, if any.
         */
        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
