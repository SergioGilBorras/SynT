/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

import java.util.Stack;

/**
 * Detects directed cycles in a graph represented as an adjacency matrix using an
 * iterative depth-first search (DFS).
 * <p>
 * Any entry {@code > 0} in the matrix is treated as an edge.
 * </p>
 * <p>
 * Time complexity: O(V + E) (each node/edge is processed once).<br>
 * Space complexity: O(V) (explicit stack + visited/inStack bookkeeping).
 * </p>
 */
public class CycleDetectionDFSIterative {

    /**
     * Simple manual demo.
     * <p>
     * Not used by the application at runtime.
     * </p>
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        // Ejemplo de matriz de adyacencia
        int[][] adjacencyMatrix = {
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1},
                {1, 0, 0, 0} // Este enlace crea un ciclo
        };

        boolean hasCycle = hasCycle(adjacencyMatrix);
        System.out.println("¿El grafo tiene ciclos? " + hasCycle);
    }

    /**
     * Returns whether the directed graph described by {@code matrix} contains at
     * least one directed cycle.
     *
     * @param matrix adjacency matrix (must be square); an entry {@code > 0}
     *               represents an edge
     * @return {@code true} if a directed cycle exists; {@code false} otherwise
     * @throws IllegalArgumentException if {@code matrix} is {@code null} or not
     *                                  square
     */
    public static boolean hasCycle(int[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("matrix cannot be null");
        }
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            if (matrix[i] == null || matrix[i].length != n) {
                throw new IllegalArgumentException("matrix must be square");
            }
        }

        boolean[] visited = new boolean[n];
        boolean[] inStack = new boolean[n];

        // Revisar todos los nodos del grafo
        for (int startNode = 0; startNode < n; startNode++) {
            if (!visited[startNode]) {
                if (dfsIterative(matrix, startNode, visited, inStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Iterative DFS starting at {@code startNode}.
     *
     * @param matrix adjacency matrix
     * @param startNode start node
     * @param visited global visited array
     * @param inStack global recursion-stack emulation array
     * @return {@code true} if a back-edge is found (cycle); {@code false} otherwise
     */
    private static boolean dfsIterative(int[][] matrix, int startNode, boolean[] visited, boolean[] inStack) {
        Stack<Integer> stack = new Stack<>();
        stack.push(startNode);

        while (!stack.isEmpty()) {
            int node = stack.peek();

            // Si el nodo no ha sido visitado, márcalo como visitado y agrégalo al stack
            if (!visited[node]) {
                visited[node] = true;
                inStack[node] = true;
            }

            boolean hasUnvisitedNeighbor = false;

            // Revisar todos los vecinos del nodo
            for (int neighbor = 0; neighbor < matrix[node].length; neighbor++) {
                if (matrix[node][neighbor] > 0) { // Existe una conexión
                    if (!visited[neighbor]) {
                        // Si hay un vecino no visitado, agrégalo al stack
                        stack.push(neighbor);
                        hasUnvisitedNeighbor = true;
                        break; // Salimos para procesar este vecino antes de continuar con otros
                    } else if (inStack[neighbor]) {
                        // Si un vecino ya está en la pila, hay un ciclo
                        return true;
                    }
                }
            }

            // Si no tiene más vecinos sin visitar, sácalo del stack
            if (!hasUnvisitedNeighbor) {
                stack.pop();
                inStack[node] = false; // Ya no está en la pila de recorrido
            }
        }

        return false;
    }
}