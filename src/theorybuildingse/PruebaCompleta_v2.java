/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

import java.util.ArrayList;
import java.util.List;
import org.logicng.formulas.Formula;
import static theorybuildingse.CNFToImplications_LogicNG.convertCNFToImplications;

/**
 *
 * @author Sergio
 */
public class PruebaCompleta_v2 {

    public static void main(String[] args) {
        // Ejemplo de entrada: Teoría lógica representada como una lista de implicaciones
        List<String> theory = new ArrayList<>();

        String expression = "(A & B) | C & (A => C)";
        SATSolver_LogicNG.debug = true;

        SATSolver_LogicNG SATSolver = new SATSolver_LogicNG(expression);
        System.out.println("Teoria Simplificada formato CNF: " + SATSolver.getFormulaCNFSimplificada());

        if (SATSolver.isfactible()) {
            System.out.println("La formula es factible.");

            // Obtener un modelo (una asignación que satisface la fórmula)
            System.out.println("Modelo encontrado: " + SATSolver.getModelo());

            // Convertir cada cláusula de la CNF en implicaciones
            List<Formula> implications = convertCNFToImplications(SATSolver.getFormulaCNFSimplificada(), SATSolver.getFormulaFactory());

            // Mostrar las implicaciones resultantes
            System.out.println("Implicaciones:");
            for (Formula implication : implications) {
                System.out.println(implication);
                theory.add(implication.toString());
            }

            // Generar la matriz de adyacencia
            int[][] adjacencyMatrix = TheoryToImplicationTheory.getImplicationTheoryFromTheory(theory);

            List<String> nodes = TheoryToImplicationTheory.getNodesFromTheory(theory);

            // Imprimir resultados
            System.out.println("\nNodos: " + nodes);
            System.out.println("Matriz de adyacencia:");
            TheoryToImplicationTheory.printMatrix(adjacencyMatrix, nodes);

            boolean hasCycle = CycleDetectionDFSIterative.hasCycle(adjacencyMatrix);
            System.out.println("\nEl grafo tiene ciclos? " + hasCycle);

            if (hasCycle) {
                //TarjanCondensedGraph -> Eliminar los ciclos
            }
            
            
            int[][] TransitiveClosureMatrix = TransitiveClosure.computeTransitiveClosure(adjacencyMatrix);

            System.out.println("\nMatriz Transitive Closure:");
            TheoryToImplicationTheory.printMatrix(TransitiveClosureMatrix, nodes);

            int[][] TransitiveReductionMatrix = TransitiveReduction.computeTransitiveReduction(TransitiveClosureMatrix);

            System.out.println("\nMatriz Transitive Reduction:");
            TheoryToImplicationTheory.printMatrix(TransitiveReductionMatrix, nodes);

        } else {
            System.out.println("La Teoria no es factible.");
        }
    }
}
