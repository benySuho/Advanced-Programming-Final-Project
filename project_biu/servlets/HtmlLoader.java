package servlets;

import server.RequestParser.RequestInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


public class HtmlLoader implements Servlet {
    String htmlFolder;

    public HtmlLoader(String htmlFolder) {
        this.htmlFolder = htmlFolder;
    }

    /**
     * Handles HTTP requests by loading HTML files from a specified folder.
     *
     * @param ri       The parsed request information.
     * @param toClient The OutputStream to write the HTTP response and file content to.
     * @throws IOException If an I/O error occurs while reading the file or writing to the client.
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        String[] uriSegments = ri.getUriSegments();
        if (uriSegments.length > 1) {
            uriSegments = Arrays.copyOfRange(uriSegments, 1, uriSegments.length);
        } else {
            uriSegments = new String[]{"index.html"};
        }

        Path requestedPath = Paths.get(htmlFolder, uriSegments);

        if (Files.exists(requestedPath)) {
            sendFile(requestedPath, toClient);
        } else if (Files.isRegularFile(requestedPath)) {
            sendFile(requestedPath, toClient);
        } else {
            sendNotFound(toClient);
        }
    }

    /**
     * Sends a file to the client using HTTP 200 OK response.
     *
     * @param filePath The path to the file to be sent.
     * @param toClient The OutputStream to write the HTTP response and file content to.
     * @throws IOException If an I/O error occurs while reading the file or writing to the client.
     */
    private void sendFile(Path filePath, OutputStream toClient) throws IOException {
        byte[] content = Files.readAllBytes(filePath);
        toClient.write(("HTTP/1.1 200 OK\r\n").getBytes());
        toClient.write(("Content-Type: text/").getBytes());
        toClient.write(filePath.getFileName().toString().split("\\.")[1].toLowerCase().getBytes()); // hml, css, ...
        toClient.write(("\r\n").getBytes());
        toClient.write(("Content-Length: " + content.length + "\r\n").getBytes());
        toClient.write(("\r\n").getBytes());
        toClient.write(content);
    }


    /**
     * Sends a 404 Not Found HTTP response to the client.
     *
     * @param toClient The OutputStream to write the response to.
     * @throws IOException If an I/O error occurs while writing to the client.
     */
    private void sendNotFound(OutputStream toClient) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n";
        toClient.write(response.getBytes());
        toClient.flush();
    }

    @Override
    public void close() throws IOException {

    }
}
