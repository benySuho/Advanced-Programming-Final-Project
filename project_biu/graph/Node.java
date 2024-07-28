package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;
    public String getName() {
        return name;
    }

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public List<Node> getEdges() {
        return edges;
    }

    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    public void addEdge(Node node) {
        edges.add(node);
    }

    /**
     * This method checks if the current node and its descendants form a cycle.
     * It uses a depth-first search (DFS) algorithm to traverse the graph and detect cycles.
     *
     * @return {@code true} if a cycle is detected, {@code false} otherwise.
     */
    public boolean hasCycles() {
        Set<Node> visited = new HashSet<>();
        Set<Node> stack = new HashSet<>();
        // Search for cycles using dfs algorithm
        return dfs(this, visited, stack);
    }

    /**
     * Performs a depth-first search (DFS) to detect cycles in the graph starting from the given node.
     *
     * @param currentNode The node to start the DFS from.
     * @param visited A set to keep track of visited nodes.
     * @param stack A set to keep track of nodes currently in the DFS stack.
     * @return {@code true} if a cycle is detected, {@code false} otherwise.
     */
    private static boolean dfs(Node currentNode, Set<Node> visited, Set<Node> stack) {
        if (stack.contains(currentNode)) {
            return true; // Cycle detected
        }
        if (visited.contains(currentNode)) {
            return false; // Already visited node, no cycle here
        }

        visited.add(currentNode);
        stack.add(currentNode);

        for (Node neighbor : currentNode.getEdges()) {
            if (dfs(neighbor, visited, stack)) {
                return true;
            }
        }

        stack.remove(currentNode); // Remove from stack once all neighbors are processed
        return false;
    }
}