package graph;

import java.util.ArrayList;
import java.util.HashMap;

public class Graph extends ArrayList<Node> {
    /**
     * Checks if the graph contains any cycles.
     *
     * @return {@code true} if the graph contains at least one cycle, {@code false} otherwise.
     */
    public boolean hasCycles() {
        for (Node node : this) {
            if (node.hasCycles()) return true;
        }
        return false;
    }

    /**
     * Creates a new graph from topics with the correct connections between nodes.
     * The graph is reset before creating the new one.
     */
    public void createFromTopics() {
        // reset the graph
        this.removeRange(0, this.size());
        // store added nodes in Map to connect properly
        HashMap<Topic, Node> topicMap = new HashMap<>();
        // we use <Agent, Node> to allow multiple use of same names
        //  For example: we can use plus several times in different places on graph
        HashMap<Agent, Node> agentMap = new HashMap<>();

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();

        for (Topic topic : TopicManagerSingleton.get().getTopics()) {
            Node topicNode = createTopicNode(topic, topicMap);
            for (Agent a : topic.subs) {
                Node agentNode = createAgentNode(a, agentMap);
                topicNode.addEdge(agentNode);
            }
            for (Agent a : topic.pubs) {
                Node agentNode = createAgentNode(a, agentMap);
                agentNode.addEdge(topicNode);
            }
        }
    }

    /**
     * Creates a new node for a given topic and adds it to the graph.
     * If the topic node already exists in the graph, it returns the existing node.
     *
     * @param topic    The topic for which the node needs to be created.
     * @param topicMap A map to store the existing topic nodes.
     * @return The node corresponding to the given topic.
     */
    private Node createTopicNode(Topic topic, HashMap<Topic, Node> topicMap) {
        if (topicMap.containsKey(topic))
            return topicMap.get(topic);
        String topicName = "T" + topic.name;
        Node topicNode = new Node(topicName);
        this.add(topicNode);
        topicMap.put(topic, topicNode);
        return topicNode;
    }

    /**
     * Creates a new node for a given agent and adds it to the graph.
     * If the agent node already exists in the graph, it returns the existing node.
     *
     * @param a       The agent for which the node needs to be created.
     * @param nodeMap A map to store the existing agent nodes.
     * @return The node corresponding to the given agent.
     */
    private Node createAgentNode(Agent a, HashMap<Agent, Node> nodeMap) {
        if (nodeMap.containsKey(a))
            return nodeMap.get(a);
        String agentName = "A" + a.getName();
        Node agentNode = new Node(agentName);
        this.add(agentNode);
        nodeMap.put(a, agentNode);
        return agentNode;
    }
}
