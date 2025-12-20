/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package old_clases;
import java.util.*;

/*
Algoritmo Davis-Putnam 
Método de resolución de problemas de satisfacibilidad booleana (SAT).
*/

public class SATSolver_DavisPutnam {

    // Método principal para resolver SAT con el algoritmo Davis-Putnam
    public static boolean solveSAT(List<Set<Integer>> clauses, Set<Integer> assignment) {
        // Caso base: si no hay cláusulas, la fórmula es satisfacible
        if (clauses.isEmpty()) {
            return true;
        }

        // Caso base: si alguna cláusula está vacía, la fórmula es insatisfacible
        for (Set<Integer> clause : clauses) {
            if (clause.isEmpty()) {
                return false;
            }
        }

        // Busca variables puras y las elimina
        Integer pureLiteral = findPureLiteral(clauses);
        if (pureLiteral != null) {
            assignment.add(pureLiteral);
            clauses = eliminateClauses(clauses, pureLiteral);
            return solveSAT(clauses, assignment);
        }

        // Busca literales unitarios y los propaga
        Integer unitLiteral = findUnitLiteral(clauses);
        if (unitLiteral != null) {
            assignment.add(unitLiteral);
            clauses = eliminateClauses(clauses, unitLiteral);
            return solveSAT(clauses, assignment);
        }

        // Elegir una variable (heurística básica: la primera encontrada)
        Integer literal = clauses.iterator().next().iterator().next();

        // Intentar con literal positivo
        List<Set<Integer>> reducedClauses = eliminateClauses(clauses, literal);
        Set<Integer> newAssignment = new HashSet<>(assignment);
        newAssignment.add(literal);
        if (solveSAT(reducedClauses, newAssignment)) {
            return true;
        }

        // Intentar con literal negativo
        reducedClauses = eliminateClauses(clauses, -literal);
        newAssignment = new HashSet<>(assignment);
        newAssignment.add(-literal);
        return solveSAT(reducedClauses, newAssignment);
    }

    // Encuentra un literal puro en las cláusulas
    private static Integer findPureLiteral(List<Set<Integer>> clauses) {
        Map<Integer, Boolean> literalMap = new HashMap<>();
        for (Set<Integer> clause : clauses) {
            for (Integer literal : clause) {
                literalMap.put(literal, !literalMap.containsKey(-literal));
            }
        }
        for (Map.Entry<Integer, Boolean> entry : literalMap.entrySet()) {
            if (entry.getValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Encuentra un literal unitario en las cláusulas
    private static Integer findUnitLiteral(List<Set<Integer>> clauses) {
        for (Set<Integer> clause : clauses) {
            if (clause.size() == 1) {
                return clause.iterator().next();
            }
        }
        return null;
    }

    // Elimina las cláusulas que contienen un literal y simplifica las restantes
    private static List<Set<Integer>> eliminateClauses(List<Set<Integer>> clauses, Integer literal) {
        List<Set<Integer>> reducedClauses = new ArrayList<>();
        for (Set<Integer> clause : clauses) {
            if (clause.contains(literal)) {
                continue; // Esta cláusula está satisfecha
            }
            Set<Integer> reducedClause = new HashSet<>(clause);
            reducedClause.remove(-literal);
            reducedClauses.add(reducedClause);
        }
        return reducedClauses;
    }

    // Método principal para probar la implementación
    public static void main(String[] args) {
        // Ejemplo de fórmula: (x1 ∨ x2) ∧ (¬x1 ∨ x3) ∧ (¬x2 ∨ ¬x3)
        List<Set<Integer>> clauses = new ArrayList<>();
        clauses.add(new HashSet<>(Arrays.asList(1, 2)));   // x1 ∨ x2
        clauses.add(new HashSet<>(Arrays.asList(-1, 3)));  // ¬x1 ∨ x3
        clauses.add(new HashSet<>(Arrays.asList(-2, -3))); // ¬x2 ∨ ¬x3

        Set<Integer> assignment = new HashSet<>();
        boolean isSatisfiable = solveSAT(clauses, assignment);

        System.out.println("Es satisfacible? " + isSatisfiable);
        if (isSatisfiable) {
            System.out.println("Asignacion: " + assignment);
        }
    }
}