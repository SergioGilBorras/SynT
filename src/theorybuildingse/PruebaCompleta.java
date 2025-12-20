/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Sergio
 */
public class PruebaCompleta {

    public static void main(String[] args) {
        // Ejemplo de entrada: Teoría lógica representada como una lista de implicaciones
        List<String> theory = Arrays.asList("Q => R", "P => R", "S => Q", "~S => ~P");

        // Generar la matriz de adyacencia
        int[][] adjacencyMatrix = TheoryToImplicationTheory.getImplicationTheoryFromTheory(theory);

        List<String> nodes = TheoryToImplicationTheory.getNodesFromTheory(theory);

        // Imprimir resultados
        System.out.println("Nodos: " + nodes);
        System.out.println("Matriz de adyacencia:");
        TheoryToImplicationTheory.printMatrix(adjacencyMatrix, nodes);

        int[][] TransitiveClosureMatrix = TransitiveClosure.computeTransitiveClosure(adjacencyMatrix);

        System.out.println("Matriz Transitive Closure:");
        TheoryToImplicationTheory.printMatrix(TransitiveClosureMatrix, nodes);

        int[][] TransitiveReductionMatrix = TransitiveReduction.computeTransitiveReduction(TransitiveClosureMatrix);

        boolean hasCycle = CycleDetectionDFSIterative.hasCycle(TransitiveReductionMatrix);
        System.out.println("\nEl grafo tiene ciclos? " + hasCycle);
        
        if(hasCycle){
            //TarjanCondensedGraph -> Eliminar los ciclos
        }

        System.out.println("\nMatriz Transitive Reduction:");
        TheoryToImplicationTheory.printMatrix(TransitiveReductionMatrix, nodes);
    }
}
