/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package solvers_theory;

import java.util.*;

// Clase que representa el proceso de resolución lineal con función de selección
public class LinearResolutionWithSelectionFunction {

    // Método para realizar la unificación de dos literales
    public static boolean unificar(String literal1, String literal2) {
        // Si son iguales, la unificación tiene éxito
        if (literal1.equals(literal2)) {
            return true;
        }
        
        // Si un literal es la negación del otro, la unificación tiene éxito
        if (literal1.equals("!" + literal2) || literal2.equals("!" + literal1)) {
            return true;
        }
        
        return false;
    }

    // Método para realizar la resolución de cláusulas
    public static Set<String> resolver(Set<String> clausulas) {
        Set<String> resueltas = new HashSet<>();

        // Convertir el conjunto de cláusulas en una lista para iterar
        List<String> clausulasList = new ArrayList<>(clausulas);
        
        // Iterar a través de las cláusulas y resolver par a par
        for (int i = 0; i < clausulasList.size(); i++) {
            for (int j = i + 1; j < clausulasList.size(); j++) {
                String clausula1 = clausulasList.get(i);
                String clausula2 = clausulasList.get(j);

                // Si las cláusulas no tienen literales complementarios, las saltamos
                if (!tieneComplementarios(clausula1, clausula2)) {
                    continue;
                }
                
                // Intentamos resolver las cláusulas
                String resolvente = resolverClausulas(clausula1, clausula2);
                if (resolvente != null) {
                    resueltas.add(resolvente);
                }
            }
        }
        
        return resueltas;
    }

    // Método para verificar si dos cláusulas tienen literales complementarios
    private static boolean tieneComplementarios(String clausula1, String clausula2) {
        for (String literal1 : clausula1.split("\\|")) {
            for (String literal2 : clausula2.split("\\|")) {
                if (unificar(literal1.trim(), literal2.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Método para resolver dos cláusulas y devolver la resolvente
    private static String resolverClausulas(String clausula1, String clausula2) {
        // Intentamos encontrar literales complementarios
        String[] literals1 = clausula1.split("\\|");
        String[] literals2 = clausula2.split("\\|");

        StringBuilder resolvente = new StringBuilder();

        for (String literal1 : literals1) {
            literal1 = literal1.trim();
            boolean encontrado = false;
            for (String literal2 : literals2) {
                literal2 = literal2.trim();
                // Si encontramos literales complementarios
                if (unificar(literal1, literal2)) {
                    encontrado = true;
                    break;
                }
            }
            // Si no encontramos complementarios, agregamos el literal a la resolvente
            if (!encontrado) {
                if (resolvente.length() > 0) {
                    resolvente.append(" | ");
                }
                resolvente.append(literal1);
            }
        }

        // Si hemos resuelto y la resolvente no es vacía, la devolvemos
        if (resolvente.length() > 0) {
            return resolvente.toString();
        }

        // Si la resolvente está vacía, hemos encontrado una contradicción (cláusula vacía)
        return null;
    }

    // Método principal para ejecutar la resolución y probar el algoritmo
    public static void main(String[] args) {
        // Definimos un conjunto de cláusulas
        Set<String> clausulas = new HashSet<>();
        clausulas.add("P(x) | Q(x)");
        clausulas.add("!P(a) | R(a)");
        clausulas.add("!Q(x) | !R(x)");
        clausulas.add("!P(a)");

        // Resolver las cláusulas
        Set<String> resueltas = resolver(clausulas);

        // Mostrar el resultado de la resolución
        if (resueltas.isEmpty()) {
            System.out.println("No se puede resolver más. No se encontró contradicción.");
        } else {
            System.out.println("Resolventes generadas:");
            for (String resolvente : resueltas) {
                System.out.println(resolvente);
            }
        }
    }
}