package views;

import graph.Graph;
import graph.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class HtmlGraphWriter {

    public static String[] getGraphHTML(Graph graph) {
        String htmlTemplate = readHtmlFile("html_files/graph.html");
        if (htmlTemplate == null) {
            return new String[]{""};
        }


        String graphJson = GraphToJson(graph);
        String modifiedHtml = htmlTemplate.replace("<!--GRAPH_DATA-->", graphJson);
        return modifiedHtml.split("\n");

    }

    private static String readHtmlFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                contentBuilder.append(currentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return contentBuilder.toString();
    }

    static String GraphToJson(Graph graph) {
        StringBuilder json = new StringBuilder("[");
        for (Node node : graph) {
            json.append("{");
            json.append("\"id\":\"").append(node.toString().split("@")[1]).append("\",\n");
            String type = "";
            String name = node.getName();
            if (name.charAt(0) == 'T') {
                type = "Topic";
                name = name.substring(1);
            } else if (name.charAt(0) == 'A') {
                type = "Agent";
                name = name.split("\\.")[1];
            }
            json.append("\"type\":\"").append(type).append("\",\n");
            json.append("\"name\":\"").append(name).append("\",\n");
            json.append("\"edges\":[");
            boolean first = true;
            for (Node edge : node.getEdges()) {
                if (!first) {
                    json.append(",");
                }
                json.append("\"").append(edge.toString().split("@")[1]).append("\"");
                first = false;
            }

            json.append("]");
            json.append("},\n");
        }
        json.append("]");
        return json.toString();
    }
}
