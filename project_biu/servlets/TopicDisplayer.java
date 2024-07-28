package servlets;

import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import server.RequestParser.RequestInfo;
import views.HtmlReader;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Displays Topic values in table
 */
public class TopicDisplayer implements Servlet {
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        sendMessage(ri.getParameters().get("topic"), ri.getParameters().get("message"));

        String content = HtmlReader.readHtmlFile("html_files/values.html");

        String tableContent = createTable();
        String valuesContent = createValuesMap();
        content = content != null ? content.replace("<!--PLACE_TABLE-->", tableContent) : null;
        content = content != null ? content.replace("\"PLACE_VALUES\":\"VALUES\"", valuesContent) : null;
        sendContent(content.split("\n"), toClient);
    }

    @Override
    public void close() throws IOException {

    }

    /**
     * Sends a message to a specific topic in the graph.
     *
     * @param topic The name of the topic to which the message will be sent.
     * @param msg   The content of the message to be sent.
     *              <p>
     *              If no matching topic is found, no action is taken.
     */
    private void sendMessage(String topic, String msg) {
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        for (Topic t : tm.getTopics()) {
            if (t.name.equals(topic)) {
                t.publish(new Message(msg));
                break;
            }
        }
    }

    /**
     * This function generates a string representation of the topics and their latest messages,
     * formatted as an HTML table.
     *
     * @return A string containing the HTML representation of the topics and their latest messages.
     * Each row in the table represents a topic, with the topic name and the latest message displayed.
     */
    private String createTable() {
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        StringBuilder tableContent = new StringBuilder();
        for (Topic topic : tm.getTopics()) {
            tableContent.append("<tr><td>");
            tableContent.append(topic.name);
            tableContent.append("</td><td>");
            Message msg;
            if ((msg = topic.getLastMsg()) != null) {
                if (Double.isNaN(msg.asDouble))
                    tableContent.append(msg.asText);
                else
                    tableContent.append(String.format("%.02f", msg.asDouble));
            }
            tableContent.append("</td></tr>");
        }
        return tableContent.toString();
    }

    /**
     * This function generates a string representation of the latest messages for each topic,
     * formatted as a JSON object.
     *
     * @return A string containing the latest messages for each topic, formatted as a JSON object.
     * The keys are the topic IDs, and the values are the corresponding latest messages.
     */
    private String createValuesMap() {
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        StringBuilder valuesContent = new StringBuilder();
        for (Topic topic : tm.getTopics()) {
            if (topic.getLastMsg() == null)
                continue;

            if (Double.isNaN(topic.getLastMsg().asDouble))
                valuesContent.append(String.format("\"%s\": %s,\n", topic.getId(), topic.getLastMsg().asText));
            else {
                valuesContent.append(String.format("\"%s\": %.02f,\n", topic.getId(), topic.getLastMsg().asDouble));
            }
        }
        return valuesContent.toString();
    }

    /**
     * Sends the content to the client using the provided output stream.
     * The content is formatted as an HTTP response with a 200 OK status code and a text/html content type.
     *
     * @param content  The content to be sent to the client. Each string in the array represents a line of content.
     * @param toClient The output stream to which the content will be written.
     * @throws IOException If an I/O error occurs while writing to the output stream.
     */
    private void sendContent(String[] content, OutputStream toClient) throws IOException {
        toClient.write(("HTTP/1.1 200 OK\r\n").getBytes());
        toClient.write(("Content-Type: text/html\r\n").getBytes());
        toClient.write(("\r\n").getBytes());
        for (String line : content)
            toClient.write(line.getBytes());
    }
}
