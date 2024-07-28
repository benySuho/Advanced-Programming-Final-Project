package servlets;

import configs.GenericConfig;
import graph.Graph;
import graph.TopicManagerSingleton;
import server.RequestParser.RequestInfo;
import views.HtmlGraphWriter;

import java.io.*;


public class ConfLoader implements Servlet {
    /**
     * This method handles incoming requests related to configuration loading.
     * It reads the content of a configuration file from the request, processes it,
     * and generates a graph based on the configuration. The generated graph is then
     * converted to HTML and sent as a response to the client.
     *
     * @param ri       The RequestInfo object containing information about the incoming request.
     * @param toClient The OutputStream to which the response will be written.
     * @throws IOException If an I/O error occurs while writing to the OutputStream.
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        // Check if the request contains a filename parameter
        if (!ri.getParameters().containsKey("filename"))
            return;

        // Get the filename from the request parameters
        String filename = ri.getParameters().get("filename");

        // Write the request content to the specified configuration file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("config_files/" + filename))) {
            writer.write(new String(ri.getContent(), 0));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        tm.clear();

        // Create a new GenericConfig object and set its configuration file
        GenericConfig config = new GenericConfig();
        config.setConfFile("config_files/" + filename);
        config.create();

        // Create a new graph based on the topics in the configuration
        Graph g = new Graph();
        g.createFromTopics();

        // Generate HTML content for the graph and send
        String[] content = HtmlGraphWriter.getGraphHTML(g, "html_files/graph.html");
        sendContent(content, toClient);
    }

    @Override
    public void close() throws IOException {

    }


    /**
     * This method sends the content to the client using the provided OutputStream.
     * The content is formatted as an HTTP response with a 200 OK status code and a text/html content type.
     *
     * @param content  The array of strings representing the content to be sent to the client.
     *                 Each string in the array represents a line of the content.
     * @param toClient The OutputStream to which the content will be written.
     * @throws IOException If an I/O error occurs while writing to the OutputStream.
     */
    private void sendContent(String[] content, OutputStream toClient) throws IOException {
        toClient.write(("HTTP/1.1 200 OK\r\n").getBytes());
        toClient.write(("Content-Type: text/html\r\n").getBytes());
        toClient.write(("\r\n").getBytes());
        for (String line : content)
            toClient.write(line.getBytes());
    }

}
