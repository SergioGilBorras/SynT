/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

/*
algoritmo de Floyd-Warshall 
*/
public class TransitiveClosure {
    
    // Método para calcular el cierre transitivo usando el algoritmo de Floyd-Warshall
    public static int[][] computeTransitiveClosure(int[][] graph) {
        int n = graph.length; // Número de nodos en el grafo

        // Usamos el mismo grafo como base para el cierre transitivo
        int[][] closure = new int[n][n];
        
        // Copiamos los valores del grafo original en el cierre transitivo
        for (int i = 0; i < n; i++) {
            System.arraycopy(graph[i], 0, closure[i], 0, n);
        }

        // Algoritmo de Floyd-Warshall para encontrar el cierre transitivo
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // Si hay un camino de i a k y de k a j, entonces hay un camino de i a j
                    closure[i][j] = closure[i][j] | (closure[i][k] & closure[k][j]);
                }
            }
        }

        return closure;
    }
    
    private static void printMatrix(int[][] graph ){
        for (int[] graph1 : graph) {
            for (int j = 0; j < graph1.length; j++) {
                System.out.print(graph1[j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        // Grafo de ejemplo representado por una matriz de adyacencia
        // 1 representa una relación directa, 0 representa no hay relación directa
        int[][] graph = {
            {0, 1, 0, 0},  // 0 -> 1
            {0, 0, 1, 0},  // 1 -> 2
            {0, 0, 0, 1},  // 2 -> 3
            {0, 0, 0, 0}   // 3 -> no tiene relaciones
        };

        // Llamamos a la función para calcular el cierre transitivo
        int[][] closure = computeTransitiveClosure(graph);
        
        // Imprimir la matriz de cierre transitivo
        System.out.println("Transitive Closure:");
        printMatrix(closure);
    }
}
