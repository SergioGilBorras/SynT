/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package old_clases;

import java.util.*;

class KosarajuCondensedGraph {
    static class Graph {
        int V;
        List<List<Integer>> adjList;

        Graph(int v) {
            V = v;
            adjList = new ArrayList<>();
            for (int i = 0; i < v; i++) {
                adjList.add(new ArrayList<>());
            }
        }

        void addEdge(int u, int v) {
            adjList.get(u).add(v);
        }

        // DFS en el grafo original
        void dfs1(int v, boolean[] visited, Stack<Integer> stack) {
            visited[v] = true;
            for (int neighbor : adjList.get(v)) {
                if (!visited[neighbor]) {
                    dfs1(neighbor, visited, stack);
                }
            }
            stack.push(v);  // Agregar a la pila cuando se termina
        }

        // DFS en el grafo invertido
        void dfs2(int v, boolean[] visited) {
            visited[v] = true;
            System.out.print(v + " ");
            for (int neighbor : adjList.get(v)) {
                if (!visited[neighbor]) {
                    dfs2(neighbor, visited);
                }
            }
        }

        // Invertir el grafo
        Graph invert() {
            Graph invertedGraph = new Graph(V);
            for (int u = 0; u < V; u++) {
                for (int v : adjList.get(u)) {
                    invertedGraph.addEdge(v, u);  // Invertir la arista
                }
            }
            return invertedGraph;
        }

        // Función para encontrar los SCCs usando Kosaraju
        void findSCCs() {
            Stack<Integer> stack = new Stack<>();
            boolean[] visited = new boolean[V];

            // Paso 1: Realizar DFS en el grafo original y llenar la pila con el orden de terminación
            for (int i = 0; i < V; i++) {
                if (!visited[i]) {
                    dfs1(i, visited, stack);
                }
            }

            // Paso 2: Invertir el grafo
            Graph invertedGraph = invert();

            // Paso 3: Realizar DFS en el grafo invertido en el orden de la pila
            Arrays.fill(visited, false);
            while (!stack.isEmpty()) {
                int v = stack.pop();
                if (!visited[v]) {
                    System.out.print("SCC: ");
                    invertedGraph.dfs2(v, visited);
                    System.out.println();
                }
            }
        }
    }

    public static void main(String[] args) {
        Graph g = new Graph(5);
        g.addEdge(0, 2);
        g.addEdge(2, 1);
        g.addEdge(1, 0);
        g.addEdge(1, 3);
        g.addEdge(3, 4);

        g.findSCCs();  // Imprimir los componentes fuertemente conexos
    }
}
