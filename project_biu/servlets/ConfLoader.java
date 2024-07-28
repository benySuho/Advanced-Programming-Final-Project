package servlets;

import configs.GenericConfig;
import graph.Graph;
import graph.TopicManagerSingleton;
import server.RequestParser.RequestInfo;
import views.HtmlGraphWriter;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ConfLoader implements Servlet {
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        if (!ri.getParameters().containsKey("filename"))
            return;
        String filename = ri.getParameters().get("filename");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("config_files/"+filename))){
            writer.write(new String(ri.getContent(), 0));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        tm.clear();
        GenericConfig config = new GenericConfig();
        config.setConfFile("config_files/"+filename);
        config.create();
        Graph g = new Graph();
        g.createFromTopics();
        String[] content = HtmlGraphWriter.getGraphHTML(g);

        sendContent(content, toClient);

    }

    @Override
    public void close() throws IOException {

    }


    private void sendContent(String[] content, OutputStream toClient) throws IOException {
        toClient.write(("HTTP/1.1 200 OK\r\n").getBytes());
        toClient.write(("Content-Type: text/html\r\n").getBytes());
//        toClient.write(("Content-Length: " + content.length + "\r\n").getBytes());
        toClient.write(("\r\n").getBytes());
        for (String line : content)
            toClient.write(line.getBytes());
    }

}
