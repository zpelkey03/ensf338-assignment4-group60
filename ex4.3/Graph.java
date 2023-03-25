/*graph class for ENSF 338 ex4.3
Uses the UNION-FIND (Q2) cycle detection algorithim
to implement the Krusal method (Q3). Doesn't contain
all of the potential graph functions as they were not
specified
 */

import java.util.*;
public class Graph {

    //Edges for the Graph class
    private class Edge {
        int node1;
        int node2;
        int weight;

        Edge(int node1, int node2, int weight) {
            this.node1 = node1;
            this.node2 = node2;
            this.weight = weight;
        }
    }

        // Internal class to represent a node in the graph
        public class GraphNode {
            String data;
            List<GraphNode> neighbors;

            public GraphNode(String data) {
                this.data = data;
                neighbors = new ArrayList<>();
            }
        }
        //lists of the graphs and nodes
        private List<GraphNode> nodes;
        private List<Edge> edges;

        //Simple contructor for graph`
        public Graph() {
            nodes = new ArrayList<>();
            edges = new ArrayList<>();
        }

    //find function for the union-find algorithm (Q2)
    private GraphNode find(GraphNode node) {
        while (node.getParent() != null) {
            node = node.getParent();
        }
        return node;
    }

    //union function for the union-find algorithm (Q2)
    private void union(GraphNode n1, GraphNode n2) {
        GraphNode root1 = find(n1);
        GraphNode root2 = find(n2);
        if (root1 == root2) {
            return;
        }
        if (root1.getRank() < root2.getRank()) {
            root1.setParent(root2);
        } else if (root1.getRank() > root2.getRank()) {
            root2.setParent(root1);
        } else {
            root2.setParent(root1);
            root1.setRank(root1.getRank() + 1);
        }
    }

    //implementation of the krusal algorithm using the union find methods(Q3)
    public Graph mst() {
        Graph mst = new Graph();
        ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
        // add all edges to edges list
        for (GraphNode n : nodes) {
            for (GraphEdge e : n.getEdges()) {
                edges.add(e);
            }
        }
        // sort edges list by weight for lateR
        edges.sort(new Comparator<GraphEdge>() {
            public int compare(GraphEdge e1, GraphEdge e2) {
                return e1.getWeight() - e2.getWeight();
            }
        });
        // create union-find set for each node
        for (GraphNode n : nodes) {
            n.setParent(null);
            n.setRank(0);
        }
        // iterate over edges, adding to MST if no cycle is formed
        for (GraphEdge e : edges) {
            GraphNode n1 = e.getN1();
            GraphNode n2 = e.getN2();
            if (find(n1) != find(n2)) {
                mst.addNode(n1.getData());
                mst.addNode(n2.getData());
                mst.addEdge(n1, n2, e.getWeight());
                union(n1, n2);
            }
        }
        return mst;
    }


}
