/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

import java.util.*;

/**
 * Tarjan's algorithm implementation to compute strongly connected components
 * (SCCs) of a directed graph.
 * <p>
 * This class can be built either from an adjacency matrix ({@code int[][]}) or
 * by specifying a vertex count and adding edges.
 * </p>
 * <p>
 * Time complexity: O(V + E).
 * </p>
 */
public class TarjanCondensedGraph {

    /** Number of vertices. */
    private final int V;

    /** Adjacency list representation. */
    private final List<List<Integer>> adjList;

    /** Monotonic index counter used by Tarjan. */
    private int index = 0;

    /** Collected SCCs as sets of vertex indices. */
    private final ArrayList<Set<Integer>> listSetNodes = new ArrayList<>();

    /** Internal set instance used while extracting a single SCC. */
    private Set<Integer> nodos;

    /**
     * Builds a graph from an adjacency matrix.
     * <p>
     * Any entry {@code != 0} is treated as an edge.
     * </p>
     *
     * @param matrizAdj adjacency matrix (must be square)
     */
    public TarjanCondensedGraph(int[][] matrizAdj) {
        V = matrizAdj.length;
        adjList = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adjList.add(new ArrayList<>());
            for (int j = 0; j < V; j++) {
                if (matrizAdj[i][j] != 0) {
                    //adjList.get(i).add(j);
                    addEdge(i, j);
                }
            }
        }
    }

    /**
     * Builds an empty graph with {@code v} vertices.
     *
     * @param v vertex count
     */
    public TarjanCondensedGraph(int v) {
        V = v;
        adjList = new ArrayList<>();
        for (int i = 0; i < v; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    /**
     * Adds a directed edge {@code u -> v}.
     *
     * @param u source vertex
     * @param v target vertex
     */
    public final void addEdge(int u, int v) {
        adjList.get(u).add(v);
    }

    /**
     * Tarjan DFS routine.
     *
     * @param v current vertex
     * @param indices discovery indices (Tarjan)
     * @param lowLink low-link values (Tarjan)
     * @param stack DFS stack
     * @param inStack membership flag for {@code stack}
     */
    private void tarjanDFS(int v, int[] indices, int[] lowLink, Stack<Integer> stack, boolean[] inStack) {
        indices[v] = lowLink[v] = index++;
        stack.push(v);
        inStack[v] = true;

        // Explore neighbors
        for (int neighbor : adjList.get(v)) {
            if (indices[neighbor] == -1) {
                tarjanDFS(neighbor, indices, lowLink, stack, inStack);
                lowLink[v] = Math.min(lowLink[v], lowLink[neighbor]);
            } else if (inStack[neighbor]) {
                lowLink[v] = Math.min(lowLink[v], indices[neighbor]);
            }
        }

        // Root of an SCC
        if (indices[v] == lowLink[v]) {
            nodos = new LinkedHashSet<>();
            //System.out.print("SCC: ");
            int node;
            do {
                node = stack.pop();
                nodos.add(node);
                inStack[node] = false;
                //System.out.print(node + " ");
            } while (node != v);
            //System.out.println();
            listSetNodes.add(nodos);
        }
    }

    /**
     * Computes all SCCs of the current graph.
     * <p>
     * Note: the returned list is backed by internal state; repeated calls will
     * append SCCs again in the current implementation.
     * </p>
     *
     * @return list of SCCs, each SCC as a set of vertex indices
     */
    public ArrayList<Set<Integer>> findSCCs() {
        int[] indices = new int[V];
        int[] lowLink = new int[V];
        Arrays.fill(indices, -1);
        Stack<Integer> stack = new Stack<>();
        boolean[] inStack = new boolean[V];

        for (int i = 0; i < V; i++) {
            if (indices[i] == -1) {
                tarjanDFS(i, indices, lowLink, stack, inStack);
            }
        }

        return listSetNodes;
    }

    /**
     * Prints the currently collected SCCs to stdout.
     * <p>
     * Debug helper.
     * </p>
     */
    public void printListSetNodes() {
        System.out.println("-- Print SCC nodes -- ");
        for (Set<Integer> listSetNode : listSetNodes) {
            System.out.print("SCC: ");
            for (Integer node : listSetNode) {
                System.out.print(node + " ");
            }
            System.out.println();
        }
    }

    /**
     * Simple manual demo.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        TarjanCondensedGraph g = new TarjanCondensedGraph(5);
        g.addEdge(0, 2);
        g.addEdge(2, 1);
        g.addEdge(1, 0);
        g.addEdge(1, 3);
        g.addEdge(3, 4);

        g.findSCCs();  // Imprimir los componentes fuertemente conexos
        g.printListSetNodes();
    }
}
