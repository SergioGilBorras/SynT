/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package old_clases;

import java.util.*;

/*
Algoritmo DPLL (Davis-Putnam-Logemann-Loveland) 
Es una mejora significativa del algoritmo original Davis-Putnam para resolver
problemas SAT (Satisfiability Problem).
*/

public class SATSolver_DPLL {

    // Método principal para resolver SAT usando DPLL
    public static boolean solveDPLL(List<Set<Integer>> clauses, Set<Integer> assignment) {
        // Caso base: Si no hay cláusulas, la fórmula es satisfacible
        if (clauses.isEmpty()) {
            return true;
        }

        // Caso base: Si hay una cláusula vacía, la fórmula es insatisfacible
        for (Set<Integer> clause : clauses) {
            if (clause.isEmpty()) {
                return false;
            }
        }

        // Propagación unitaria: busca y asigna literales unitarios
        Integer unitLiteral = findUnitLiteral(clauses);
        if (unitLiteral != null) {
            assignment.add(unitLiteral); // Agrega la asignación
            List<Set<Integer>> reducedClauses = eliminateClauses(clauses, unitLiteral);
            return solveDPLL(reducedClauses, assignment);
        }

        // Heurística de selección de literales: elige un literal arbitrario
        Integer literal = chooseLiteral(clauses);

        // Intentar asignar el literal a verdadero
        assignment.add(literal); // Agregar el literal a la asignación
        List<Set<Integer>> reducedClauses = eliminateClauses(clauses, literal);
        if (solveDPLL(reducedClauses, assignment)) {
            return true;
        }
        assignment.remove(literal); // Eliminar el literal si no funcionó

        // Intentar asignar el literal a falso
        assignment.add(-literal); // Agregar el literal opuesto a la asignación
        reducedClauses = eliminateClauses(clauses, -literal);
        if (solveDPLL(reducedClauses, assignment)) {
            return true;
        }
        assignment.remove(-literal); // Eliminar el literal opuesto si no funcionó

        return false;
    }

    // Encuentra un literal unitario (una cláusula con un solo literal)
    private static Integer findUnitLiteral(List<Set<Integer>> clauses) {
        for (Set<Integer> clause : clauses) {
            if (clause.size() == 1) {
                return clause.iterator().next();
            }
        }
        return null;
    }

    // Heurística para seleccionar un literal (en este caso, el primero encontrado)
    private static Integer chooseLiteral(List<Set<Integer>> clauses) {
        for (Set<Integer> clause : clauses) {
            if (!clause.isEmpty()) {
                return clause.iterator().next();
            }
        }
        return null; // Nunca debería llegar aquí si la fórmula no está vacía
    }

    // Elimina cláusulas satisfechas y simplifica las restantes eliminando literales opuestos
    private static List<Set<Integer>> eliminateClauses(List<Set<Integer>> clauses, Integer literal) {
        List<Set<Integer>> reducedClauses = new ArrayList<>();
        for (Set<Integer> clause : clauses) {
            if (clause.contains(literal)) {
                continue; // Cláusula satisfecha, no se incluye
            }
            Set<Integer> reducedClause = new HashSet<>(clause);
            reducedClause.remove(-literal); // Elimina el literal opuesto
            reducedClauses.add(reducedClause);
        }
        return reducedClauses;
    }

    // Método principal para probar la implementación
    public static void main(String[] args) {
        // Ejemplo de fórmula en CNF: (x1 ∨ x2) ∧ (¬x1 ∨ x3) ∧ (¬x2 ∨ ¬x3)
        List<Set<Integer>> clauses = new ArrayList<>();
        clauses.add(new HashSet<>(Arrays.asList(1, 2)));   // x1 ∨ x2
        clauses.add(new HashSet<>(Arrays.asList(-1, 3)));  // ¬x1 ∨ x3
        clauses.add(new HashSet<>(Arrays.asList(-2, -3))); // ¬x2 ∨ ¬x3

        Set<Integer> assignment = new HashSet<>();
        boolean isSatisfiable = solveDPLL(clauses, assignment);

        System.out.println("Es satisfacible? " + isSatisfiable);
        if (isSatisfiable) {
            System.out.println("Asignacion: " + assignment);
        }
    }
}
