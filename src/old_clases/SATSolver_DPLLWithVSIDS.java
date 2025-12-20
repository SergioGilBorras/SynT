/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package old_clases;

import java.util.*;

/*
El algoritmo DPLL con VSIDS (Variable State Independent Decaying Sum), una heurística 
muy popular en los solucionadores SAT modernos. VSIDS prioriza variables con una alta 
relevancia basada en su participación en conflictos recientes, ajustándose dinámicamente 
durante la ejecución. Esto permite enfocarse en variables más críticas, mejorando el 
rendimiento en problemas complejos.
 */
public class SATSolver_DPLLWithVSIDS {

    private static final double DECAY_FACTOR = 0.95;
    private static final int CONFLICT_INCREMENT = 1;

    // Mapa para almacenar el puntaje de actividad de cada literal
    private static final Map<Integer, Double> activityMap = new HashMap<>();

    // Método principal para resolver SAT usando DPLL con VSIDS
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

        // VSIDS Heuristic para seleccionar el mejor literal
        Integer literal = chooseLiteralWithVSIDS(clauses);

        // Intentar asignar el literal a verdadero
        assignment.add(literal);
        List<Set<Integer>> reducedClauses = eliminateClauses(clauses, literal);
        if (solveDPLL(reducedClauses, assignment)) {
            return true;
        }
        assignment.remove(literal);

        // Incrementar actividad por conflicto
        incrementActivity(literal);

        // Intentar asignar el literal a falso
        assignment.add(-literal);
        reducedClauses = eliminateClauses(clauses, -literal);
        if (solveDPLL(reducedClauses, assignment)) {
            return true;
        }
        assignment.remove(-literal);

        // Incrementar actividad por conflicto
        incrementActivity(-literal);

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

    // VSIDS Heuristic para seleccionar el mejor literal
    private static Integer chooseLiteralWithVSIDS(List<Set<Integer>> clauses) {
        double maxActivity = Double.NEGATIVE_INFINITY;
        Integer bestLiteral = null;

        for (Set<Integer> clause : clauses) {
            for (Integer literal : clause) {
                double activity = activityMap.getOrDefault(literal, 0.0);
                if (activity > maxActivity) {
                    maxActivity = activity;
                    bestLiteral = literal;
                }
            }
        }

        return bestLiteral != null ? bestLiteral : chooseLiteralFallback(clauses);
    }

    // Incrementa la actividad de un literal tras un conflicto
    private static void incrementActivity(Integer literal) {
        activityMap.put(literal, activityMap.getOrDefault(literal, 0.0) + CONFLICT_INCREMENT);
        decayActivities();
    }

    // Decae todas las actividades dinámicamente
    private static void decayActivities() {
        for (Map.Entry<Integer, Double> entry : activityMap.entrySet()) {
            activityMap.put(entry.getKey(), entry.getValue() * DECAY_FACTOR);
        }
    }

    // Fallback en caso de que no haya actividad inicial (selección arbitraria)
    private static Integer chooseLiteralFallback(List<Set<Integer>> clauses) {
        for (Set<Integer> clause : clauses) {
            if (!clause.isEmpty()) {
                return clause.iterator().next();
            }
        }
        return null;
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
        if (!activityMap.isEmpty()) {
            // Imprimir las actividades de los literales
            System.out.println("Actividades de los literales: " + activityMap);
        }
    }
}
