/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package old_clases;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;
import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

public class SATSolver_MiniSAT {
    public static void main(String[] args) {
        // Crear un solver SAT
        ISolver solver = SolverFactory.newDefault();

        // Número de variables en la fórmula
        solver.newVar(3); // Variables: A (1), B (2)

        // Cláusulas en formato CNF
        IVecInt clause1 = new VecInt(new int[] {1, -2}); // A ∨ ¬B
        IVecInt clause2 = new VecInt(new int[] {-1, 2}); // ¬A ∨ B
        IVecInt clause3 = new VecInt(new int[] {-1, -2}); // ¬A ∨ ¬B

        try {
            // Añadir cláusulas al solver
            solver.addClause(clause1);
            solver.addClause(clause2);
            solver.addClause(clause3);

            // Intentar encontrar una solución
            if (solver.isSatisfiable()) {
                System.out.println("Satisfacible");
                int[] solution = solver.model();
                System.out.println("Modelo encontrado:");
                for (int i = 1; i <= 2; i++) {
                    System.out.println("Variable " + i + ": " + (solution[i - 1] > 0 ? "True" : "False"));
                }
            } else {
                System.out.println("No satisfacible");
            }
        } catch (TimeoutException e) {
            System.out.println("El solucionador SAT alcanzó el tiempo límite.");
        } catch (ContradictionException ex) {
            System.out.println("El solucionador SAT alcanzó una Contradiction Exception.");
        }
    }
}
