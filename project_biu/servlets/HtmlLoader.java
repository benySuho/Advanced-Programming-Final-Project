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


    private void sendNotFound(OutputStream toClient) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n";
        toClient.write(response.getBytes());
        toClient.flush();
    }

    @Override
    public void close() throws IOException {

    }
}
