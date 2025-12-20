/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

public class TransitiveReduction {

    // Método para realizar la reducción transitiva sobre la matriz de adyacencia
    public static int[][] computeTransitiveReduction(int[][] closure) {
        int n = closure.length; // Número de nodos en el grafo

        // Comenzamos con el grafo reducido igual a la matriz de cierre transitivo
        int[][] reducedGraph = new int[n][n];

        // Copiamos el cierre transitivo al grafo reducido inicialmente
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                reducedGraph[i][j] = closure[i][j];
            }
        }

        //Algoritmo Floyd-Warshall Inverso
        // Realizamos la reducción transitiva \cite{Floyd1962} \cite{GRIES1989} \cite{Aho1972}
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Verificamos si existe un nodo intermedio k tal que i -> k -> j
                if (reducedGraph[i][j] > 0) { //CHANGE:: CAMBIE == 1 por > 0
                    for (int k = 0; k < n; k++) {
                        // Si hay un camino i -> k y k -> j, y i -> j, lo eliminamos
                        if (i != k && j != k && reducedGraph[i][k] > 0 && reducedGraph[k][j] > 0) { //CHANGE:: CAMBIE == 1 por > 0
                            reducedGraph[i][j] = 0;
                        }
                    }
                }
            }
        }

        return reducedGraph;

    }

    private static void printMatrix(int[][] graph) {
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
            {0, 1, 1, 1}, // 0 -> 1 ; 0 -> 2 ; 0 -> 3 ;
            {0, 0, 1, 1}, // 1 -> 2 ; 1 -> 3; 
            {0, 0, 0, 1}, // 2 -> 3
            {0, 0, 0, 0} // 3 -> no tiene relaciones
        };

        // Llamamos a la función para calcular el cierre transitivo
        System.out.println("Original Graph (Transitive Closure):");
        printMatrix(graph);

        // Calculamos y mostramos la reducción transitiva
        computeTransitiveReduction(graph);

        // Imprimir la matriz de reducción transitiva
        System.out.println("Transitive Reduction:");
        printMatrix(graph);
    }
}
