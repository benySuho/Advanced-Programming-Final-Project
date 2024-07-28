package views;

import graph.Graph;
import graph.Node;

public class HtmlGraphWriter {

    /**
     * This function generates an HTML representation of a given graph.
     *
     * @param graph The graph to be represented in HTML.
     * @return An array of strings, where each string represents a line of the HTML code.
     * If the HTML template file cannot be read, an empty array is returned.
     */
    public static String[] getGraphHTML(Graph graph, String filePath) {
        String htmlTemplate = HtmlReader.readHtmlFile(filePath);
        if (htmlTemplate == null) {
            return new String[]{""};
        }

        String graphJson = GraphToJson(graph);
        String modifiedHtml = htmlTemplate.replace("\"GRAPH_DATA\"", graphJson);
        return modifiedHtml.split("\n");
    }

    /**
     * Converts a given graph into a JSON string representation.
     *
     * @param graph The graph to be converted.
     * @return A JSON string representing the graph. Each node in the graph is represented as a JSON object with the following properties:
     * - "id": The unique identifier of the node.
     * - "type": The type of the node (either "Topic" or "Agent").
     * - "name": The name of the node.
     * - "edges": An array of unique identifiers of the nodes connected to the current node.
     */
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
