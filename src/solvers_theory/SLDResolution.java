/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package solvers_theory;

import java.util.*;

class SLDResolution {

    static class Clause {

        String head;
        List<String> body;

        Clause(String head, List<String> body) {
            this.head = head;
            this.body = body;
        }

        @Override
        public String toString() {
            return head + " :- " + body;
        }
    }

    // Método de unificación simple
    public static Map<String, String> unify(String goal, String ruleHead) {
        Map<String, String> substitution = new HashMap<>();
        String[] goalParts = goal.split("\\(");
        String[] ruleParts = ruleHead.split("\\(");

        if (goalParts[0].equals(ruleParts[0])) {
            String goalArgs = goalParts.length > 1 ? goalParts[1].replace(")", "") : "";
            String ruleArgs = ruleParts.length > 1 ? ruleParts[1].replace(")", "") : "";

            String[] goalArgsArray = goalArgs.split(",");
            String[] ruleArgsArray = ruleArgs.split(",");

            if (goalArgsArray.length == ruleArgsArray.length) {
                for (int i = 0; i < goalArgsArray.length; i++) {
                    if (!goalArgsArray[i].equals(ruleArgsArray[i]) && !goalArgsArray[i].matches("[A-Za-z]+")) {
                        return null; // No unificación posible
                    }
                    if (goalArgsArray[i].matches("[A-Za-z]+") && !goalArgsArray[i].equals(ruleArgsArray[i])) {
                        substitution.put(goalArgsArray[i], ruleArgsArray[i]);
                    }
                }
                return substitution;
            }
        }
        return null; // No hay coincidencia
    }

    // Resolver utilizando SLD
    public static boolean resolve(List<Clause> program, List<String> goals) {
        if (goals.isEmpty()) {
            return true; // Consulta resuelta
        }
        String goal = goals.get(0); // Tomamos el primer literal
        for (Clause clause : program) {
            Map<String, String> substitution = unify(goal, clause.head);
            if (substitution != null) {
                List<String> newGoals = new ArrayList<>(goals);
                newGoals.remove(0); // Eliminar el primer literal
                // Aplicamos sustitución a los cuerpos de la cláusula
                List<String> newBody = new ArrayList<>();
                for (String bodyLiteral : clause.body) {
                    newBody.add(applySubstitution(bodyLiteral, substitution));
                }
                newGoals.addAll(newBody);
                if (resolve(program, newGoals)) {
                    return true;
                }
            }
        }
        return false; // No se pudo resolver
    }

    // Aplicar la sustitución a un literal
    public static String applySubstitution(String literal, Map<String, String> substitution) {
        for (Map.Entry<String, String> entry : substitution.entrySet()) {
            literal = literal.replace(entry.getKey(), entry.getValue());
        }
        return literal;
    }

    public static void main(String[] args) {
        List<Clause> program = new ArrayList<>();
        // Definir las cláusulas
        program.add(new Clause("p(X)", Arrays.asList("q(X)")));
        program.add(new Clause("q(Y)", Arrays.asList("r(Y)")));
        program.add(new Clause("r(Z)", Arrays.asList("s(Z)")));
        program.add(new Clause("s(a)", Collections.emptyList())); // Caso base

        // Definir la consulta
        List<String> goals = new ArrayList<>(Arrays.asList("p(a)"));

        // Resolver la consulta
        boolean result = resolve(program, goals);
        System.out.println("Se resolvio la consulta? " + result);
    }
}
