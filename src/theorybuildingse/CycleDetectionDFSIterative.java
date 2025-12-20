/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

import java.util.Stack;
/*
Detección de ciclos mediante DFS (Depth First Search) Iterativo
Complejidad:

    Tiempo: O(V+E), ya que cada nodo y arista se procesan exactamente una vez.
    Espacio: O(3V), debido a la pila explícita y los arrays auxiliares visited e inStack
*/
public class CycleDetectionDFSIterative {

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

    public static boolean hasCycle(int[][] matrix) {
        int n = matrix.length;
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
                if (matrix[node][neighbor] == 1) { // Existe una conexión
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