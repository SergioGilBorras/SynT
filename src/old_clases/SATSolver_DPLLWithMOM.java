/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package old_clases;

import java.util.*;

/* 
Ejemplo extendido del algoritmo DPLL con la integración de una heurística 
avanzada: MOM's Heuristic (Maximum Occurrence in clauses of Minimum Size). Esta 
heurística selecciona literales que aparecen frecuentemente en cláusulas pequeñas, 
lo que mejora el rendimiento en fórmulas complejas. 
 */
public class SATSolver_DPLLWithMOM {

    // Método principal para resolver SAT usando DPLL con MOM's Heuristic
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
            assignment.add(unitLiteral);
            List<Set<Integer>> reducedClauses = eliminateClauses(clauses, unitLiteral);
            return solveDPLL(reducedClauses, assignment);
        }

        // MOM's Heuristic para seleccionar el mejor literal
        Integer literal = chooseLiteralWithMOM(clauses);

        // Intentar asignar el literal a verdadero
        assignment.add(literal);
        List<Set<Integer>> reducedClauses = eliminateClauses(clauses, literal);
        if (solveDPLL(reducedClauses, assignment)) {
            return true;
        }
        assignment.remove(literal);

        // Intentar asignar el literal a falso
        assignment.add(-literal);
        reducedClauses = eliminateClauses(clauses, -literal);
        if (solveDPLL(reducedClauses, assignment)) {
            return true;
        }
        assignment.remove(-literal);

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

    // MOM's Heuristic para seleccionar el mejor literal
    private static Integer chooseLiteralWithMOM(List<Set<Integer>> clauses) {
        // Encuentra el tamaño mínimo de cláusulas no vacías
        int minSize = Integer.MAX_VALUE;
        for (Set<Integer> clause : clauses) {
            if (!clause.isEmpty()) {
                minSize = Math.min(minSize, clause.size());
            }
        }

        // Filtra las cláusulas con tamaño mínimo
        Map<Integer, Integer> literalFrequency = new HashMap<>();
        for (Set<Integer> clause : clauses) {
            if (clause.size() == minSize) {
                for (Integer literal : clause) {
                    literalFrequency.put(literal, literalFrequency.getOrDefault(literal, 0) + 1);
                }
            }
        }

        // Encuentra el literal más frecuente
        int maxFrequency = -1;
        Integer bestLiteral = null;
        for (Map.Entry<Integer, Integer> entry : literalFrequency.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                bestLiteral = entry.getKey();
            }
        }

        return bestLiteral;
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
