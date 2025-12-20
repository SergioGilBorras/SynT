/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

import java.util.*;

/*
Algoritmo de Tarjan para encontrar los componentes fuertemente conexos (SCCs) de 
Grafos dirijidos.

Complejidad O(V+E)
 */
class TarjanCondensedGraph {

    private final int V;
    private final List<List<Integer>> adjList;
    private int index = 0;
    private final ArrayList<Set<Integer>> listSetNodes = new ArrayList<>();
    private Set<Integer> nodos;

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

    public TarjanCondensedGraph(int v) {
        V = v;
        adjList = new ArrayList<>();
        for (int i = 0; i < v; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    public final void addEdge(int u, int v) {
        adjList.get(u).add(v);
    }

    // Tarjan's DFS
    private void tarjanDFS(int v, int[] indices, int[] lowLink, Stack<Integer> stack, boolean[] inStack) {
        indices[v] = lowLink[v] = index++;
        stack.push(v);
        inStack[v] = true;

        // Explorar los vecinos
        for (int neighbor : adjList.get(v)) {
            if (indices[neighbor] == -1) {
                tarjanDFS(neighbor, indices, lowLink, stack, inStack);
                lowLink[v] = Math.min(lowLink[v], lowLink[neighbor]);
            } else if (inStack[neighbor]) {
                lowLink[v] = Math.min(lowLink[v], indices[neighbor]);
            }
        }

        // Identificar SCCs
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

    // Función principal para encontrar los SCCs con Tarjan
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
