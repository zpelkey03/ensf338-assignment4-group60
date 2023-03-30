//graph class for ENSF 338 ex4.1
//Includes GraphNode and GraphEdge class at the bottom
//for convienience
import java.io.*;
import java.util.*;
import matplotlib4j.*;

public class Graph{
    public List<GraphNode> nodes;
    public List<GraphEdge> edges;

    public static void main(String[] args) {
        Graph graph = Graph.importFromFile("random.dot");
        if (graph == null) {
            System.out.println("Failed to import graph from file");
            return;
        }
        GraphNode startNode = graph.nodes.get(0);
        long slowTotalTime = 0;
        long fastTotalTime = 0;
        long slowMaxTime = Long.MIN_VALUE;
        long fastMaxTime = Long.MIN_VALUE;
        long slowMinTime = Long.MAX_VALUE;
        long fastMinTime = Long.MAX_VALUE;
        int nNodes = graph.nodes.size();

        for (GraphNode node : graph.nodes) {
            long startTime = System.nanoTime();
            graph.slowSP(node);
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            slowTotalTime += elapsedTime;
            slowMaxTime = Math.max(slowMaxTime, elapsedTime);
            slowMinTime = Math.min(slowMinTime, elapsedTime);

            startTime = System.nanoTime();
            graph.fastSP(node);
            endTime = System.nanoTime();
            elapsedTime = endTime - startTime;
            fastTotalTime += elapsedTime;
            fastMaxTime = Math.max(fastMaxTime, elapsedTime);
            fastMinTime = Math.min(fastMinTime, elapsedTime);
        }

        double slowAvgTime = (double) slowTotalTime / nNodes;
        double fastAvgTime = (double) fastTotalTime / nNodes;
        System.out.println("slowSP average time: " + slowAvgTime + " nanoseconds");
        System.out.println("slowSP max time: " + slowMaxTime + " nanoseconds");
        System.out.println("slowSP min time: " + slowMinTime + " nanoseconds");
        System.out.println("fastSP average time: " + fastAvgTime + " nanoseconds");
        System.out.println("fastSP max time: " + fastMaxTime + " nanoseconds");
        System.out.println("fastSP min time: " + fastMinTime + " nanoseconds");

        // Plot a histogram of execution times using matplotlib4j
        Map<String, Object> histArgs = new HashMap<>();
        histArgs.put("bins", 20);
        histArgs.put("alpha", 0.5);
        histArgs.put("label", new String[] {"slowSP", "fastSP"});
        histArgs.put("color", new String[] {"blue", "red"});

        double[] slowTimes = new double[nNodes];
        double[] fastTimes = new double[nNodes];

        for (int i = 0; i < nNodes; i++) {
            long startTime = System.nanoTime();
            graph.slowSP(graph.nodes.get(i));
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            slowTimes[i] = (double) elapsedTime;

            startTime = System.nanoTime();
            graph.fastSP(graph.nodes.get(i));
            endTime = System.nanoTime();
            elapsedTime = endTime - startTime;
            fastTimes[i] = (double) elapsedTime;
        }

        Hist.hist(slowTimes, histArgs);
        Hist.hist(fastTimes, histArgs);
        try {
            Hist.show();
        } catch (PythonExecutionException e) {
            e.printStackTrace();
        }
        
    }

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
                if (line.isEmpty() || line.startsWith("//") || line.startsWith("}")) {
                    continue; // Skip empty lines and comments
                }
                if (line.endsWith(";")) {
                    line = line.substring(0, line.length() - 1).trim(); // Remove the trailing semicolon
                }
                String[] tokens = line.split("\\s*--\\s*");
                if (tokens.length != 2) {
                    System.out.println("Invalid edge definition: " + line);
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
                        System.out.println(weightStr);
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

    public Map<GraphNode, Integer> slowSP(GraphNode startNode) {
        Map<GraphNode, Integer> distances = new HashMap<>(); // Map of nodes to their distances from startNode
        Set<GraphNode> unvisitedNodes = new HashSet<>(nodes); // Set of nodes that have not been visited yet
        distances.put(startNode, 0); // Start node has a distance of 0
        unvisitedNodes.remove(startNode); // Remove start node from unvisited nodes set
    
        while (!unvisitedNodes.isEmpty()) {
            // Find the unvisited node with the smallest distance
            GraphNode closestNode = null;
            int closestDistance = Integer.MAX_VALUE;
            for (GraphNode node : unvisitedNodes) {
                Integer distance = distances.get(node);
                if (distance != null && distance < closestDistance) {
                    closestNode = node;
                    closestDistance = distance;
                }
            }
    
            // If all remaining unvisited nodes are unreachable from startNode, exit the loop
            if (closestNode == null) {
                break;
            }
    
            // Update distances to neighboring nodes
            for (GraphEdge edge : closestNode.edges) {
                GraphNode neighbor = (edge.node1 == closestNode) ? edge.node2 : edge.node1;
                if (unvisitedNodes.contains(neighbor)) {
                    int newDistance = closestDistance + edge.weight;
                    Integer currentDistance = distances.get(neighbor);
                    if (currentDistance == null || newDistance < currentDistance) {
                        distances.put(neighbor, newDistance);
                    }
                }
            }
    
            unvisitedNodes.remove(closestNode); // Mark closestNode as visited
        }
    
        return distances;
    }
    
    // Finds the shortest path from the input node to all other nodes using a fast implementation of Dijkstra's algorithm.
    public Map<GraphNode, Integer> fastSP(GraphNode startNode) {
        Map<GraphNode, Integer> distances = new HashMap<>(); // Map of nodes to their distances from startNode
        PriorityQueue<GraphNode> unvisitedNodes = new PriorityQueue<>(Comparator.comparingInt(distances::get)); // Priority queue of unvisited nodes, ordered by distance
        Set<GraphNode> visitedNodes = new HashSet<>(); // Set of visited nodes
        distances.put(startNode, 0); // Start node has a distance of 0
        unvisitedNodes.add(startNode); // Add start node to priority queue
    
        while (!unvisitedNodes.isEmpty()) {
            GraphNode closestNode = unvisitedNodes.poll(); // Node with smallest distance
            int closestDistance = distances.get(closestNode);
    
            if (visitedNodes.contains(closestNode)) {
                continue; // Skip already visited nodes
            }
            visitedNodes.add(closestNode);
    
            // Update distances to neighboring nodes
            for (GraphEdge edge : closestNode.edges) {
                GraphNode neighbor = (edge.node1 == closestNode) ? edge.node2 : edge.node1;
                int newDistance = closestDistance + edge.weight;
                Integer currentDistance = distances.get(neighbor);
                if (currentDistance == null || newDistance < currentDistance) {
                    distances.put(neighbor, newDistance);
                    if (unvisitedNodes.contains(neighbor)) {
                        unvisitedNodes.remove(neighbor);
                    }
                    unvisitedNodes.add(neighbor);
                }
            }
        }
    
        return distances;
    }
    
    
}


//Just a simple GraphNode class
class GraphNode {
    public String data;
    public List<GraphEdge> edges;

    public GraphNode(String data) {
        this.data = data;
        this.edges = new ArrayList<>();
    }
    
}

//Just a an example of a graph edge class
class GraphEdge {
    public GraphNode node1;
    public GraphNode node2;
    public int weight;

    public GraphEdge(GraphNode node1, GraphNode node2, int weight) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }
}