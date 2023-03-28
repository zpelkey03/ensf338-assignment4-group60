import java.io.*;
import java.util.*;

public class Graph{
    public List<GraphNode> nodes;
    public List<GraphEdge> edges;

    //Simple contructor for graph
    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    //Creates a new graph node and internally stores it.
    public GraphNode addNode(String data) {
        GraphNode node = new GraphNode(data);
        nodes.add(node);
        return node;
    }

    //Removes a node from the list thats internally stored
    public void removeNode(GraphNode node) {
        // Remove the node from the list of nodes
        nodes.remove(node);

        // Remove any edges that connect to the node
        Iterator<GraphEdge> iterator = edges.iterator();
        while (iterator.hasNext()) {
            GraphEdge edge = iterator.next();
            if (edge.node1 == node || edge.node2 == node) {
                iterator.remove();
            }
        }
    }


    //Creates the edge between 2 input nodes
    public void addEdge(GraphNode node1, GraphNode node2, int weight) {
        GraphEdge edge = new GraphEdge(node1, node2, weight);
        edges.add(edge);
        node1.edges.add(edge);
        node2.edges.add(edge);
    }

    //removes the edge between 2 input nodes using a
    //iterator
    public void removeEdge(GraphNode node1, GraphNode node2) {
        Iterator<GraphEdge> iterator = edges.iterator();
        while (iterator.hasNext()) {
            GraphEdge edge = iterator.next();
            if ((edge.node1 == node1 && edge.node2 == node2) ||
                    (edge.node1 == node2 && edge.node2 == node1)) {
                iterator.remove();
                node1.edges.remove(edge);
                node2.edges.remove(edge);
                break;
            }
        }
    }



    //imports a undefined graph object from a file given the
    //GraphViz format. Also sets them as items
    public static Graph importFromFile(String fileName) {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            // Find the line that starts with "strict graph" and extract the graph name
            String line = scanner.nextLine().trim();
            if (!line.startsWith("strict graph")) {
                return null; // The file does not contain an undirected graph
            }
            String graphName = line.substring("strict graph".length()).trim();

            // Create a new Graph object
            Graph graph = new Graph();

            // Parse the edge definitions
            while (scanner.hasNextLine()) {
                line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue; // Skip empty lines and comments
                }
                if (line.endsWith(";")) {
                    line = line.substring(0, line.length() - 1).trim(); // Remove the trailing semicolon
                }
                String[] tokens = line.split("\\s*--\\s*");
                if (tokens.length != 2) {
                    return null; // The line does not contain a valid edge definition
                }
                GraphNode node1 = graph.addNode(tokens[0]);
                GraphNode node2 = graph.addNode(tokens[1]);
                int weight = 1; // Default weight is 1
                int weightIndex = line.indexOf("weight=");
                if (weightIndex != -1) {
                    int startIndex = weightIndex + "weight=".length();
                    int endIndex = line.indexOf(',', startIndex);
                    if (endIndex == -1) {
                        endIndex = line.length() - 1;
                    }
                    String weightStr = line.substring(startIndex, endIndex).trim();
                    try {
                        weight = Integer.parseInt(weightStr);
                    } catch (NumberFormatException e) {
                        return null; // The weight is not a valid integer
                    }
                }
                graph.addEdge(node1, node2, weight);
            }

            return graph;
        } catch (IOException e) {
            return null; // Failed to read the file
        }
    }

    public static void slowSP(GraphNode start) {
        Set<GraphNode> visited = new HashSet<>();
        Map<GraphNode, Integer> distance = new HashMap<>();

        // Initialize the distance to all nodes to be infinity, except for the starting node
        for (GraphNode node : start.graph.nodes) {
            if (node == start) {
                distance.put(node, 0);
            } else {
                distance.put(node, Integer.MAX_VALUE);
            }
        }

        // Repeat until all nodes have been visited
        while (visited.size() < start.graph.nodes.size()) {
            // Find the unvisited node with the smallest tentative distance
            GraphNode current = null;
            int smallestDistance = Integer.MAX_VALUE;
            for (GraphNode node : start.graph.nodes) {
                if (!visited.contains(node) && distance.get(node) < smallestDistance) {
                    current = node;
                    smallestDistance = distance.get(node);
                }
            }

            // Mark the current node as visited
            visited.add(current);

            // Update the distances of all neighbors of the current node
            for (GraphEdge edge : current.edges) {
                GraphNode neighbor = edge.getOtherNode(current);
                int newDistance = distance.get(current) + edge.weight;
                if (newDistance < distance.get(neighbor)) {
                    distance.put(neighbor, newDistance);
                }
            }
        }

}



}


//Just a simple GraphNode class
public class GraphNode {
    public String data;
    public List<GraphEdge> edges;

    public GraphNode(String data) {
        this.data = data;
        this.edges = new ArrayList<>();
    }
}

//Just a an example of a graph edge class
public class GraphEdge {
    public GraphNode node1;
    public GraphNode node2;
    public int weight;

    public GraphEdge(GraphNode node1, GraphNode node2, int weight) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }
}

