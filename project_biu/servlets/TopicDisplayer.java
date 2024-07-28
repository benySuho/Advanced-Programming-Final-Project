package servlets;

import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import server.RequestParser.RequestInfo;

import java.io.IOException;
import java.io.OutputStream;


public class TopicDisplayer implements Servlet {
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        sendMessage(ri.getParameters().get("topic"), ri.getParameters().get("message"));
        toClient.write(("HTTP/1.1 200 OK\r\n").getBytes());
        toClient.write(("Content-Type: text/html\r\n").getBytes());
        toClient.write(("\r\n").getBytes());
        toClient.write("<!DOCTYPE html>".getBytes());
        toClient.write("<html lang=\"en\">".getBytes());
        toClient.write("<head>".getBytes());
        toClient.write("<meta charset=\"UTF-8\">".getBytes());
        toClient.write("<title>Graph</title>".getBytes());
        toClient.write("<style>".getBytes());
        toClient.write("body { font-family: Arial, sans-serif; }".getBytes());
        toClient.write("</style>".getBytes());
        toClient.write("</head>".getBytes());
        toClient.write("<body>".getBytes());
        toClient.write("<table>".getBytes());
        for (Topic topic : tm.getTopics()) {
            toClient.write("<tr><td>".getBytes());
            toClient.write(topic.name.getBytes());
            toClient.write("</td><td>".getBytes());
            if (topic.getLastMsg() != null)
                toClient.write(topic.getLastMsg().getBytes());
            toClient.write("</td></tr>".getBytes());
        }
        toClient.write("</table>".getBytes());
        toClient.write("</body>".getBytes());
        toClient.write("</html>".getBytes());
        toClient.flush();
    }

    @Override
    public void close() throws IOException {

    }

    private void sendMessage(String topic, String msg) {
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        for(Topic t :tm.getTopics()) {
            if(t.name.equals(topic)) {
                t.publish(new Message(msg));
                break;
            }

        }
    }
}
