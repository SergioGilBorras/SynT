/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package solvers_theory;

/*
una cláusula Horn es una cláusula que tiene, como máximo, un literal positivo en 
su cuerpo (lado derecho). Esto significa que una Horn clause puede tener múltiples 
literales negativos, pero solo uno positivo.
*/

public class HornClauseChecker {

    // Método que verifica si una cláusula es una cláusula Horn
    public static boolean esHornClause(String clausula) {
        // Dividir la cláusula en literales (asumimos que los literales están separados por "OR" o por comas)
        String[] literales = clausula.split("\\|");

        int positivos = 0; // Contador de literales positivos

        // Iterar sobre los literales
        for (String literal : literales) {
            // Eliminar espacios en blanco
            literal = literal.trim();

            // Si es un literal positivo (sin negación)
            if (!literal.startsWith("!")) {
                positivos++;
            }

            // Si hay más de un literal positivo, no es una cláusula Horn
            if (positivos > 1) {
                return false;
            }
        }

        // Si hay 0 o 1 literales positivos, es una cláusula Horn
        return positivos <= 1;
    }

    // Método principal para probar el código
    public static void main(String[] args) {
        // Ejemplos de cláusulas
        String[] clausulas = {
            "!x1 | !x2 | x3",         // Cláusula Horn
            "x1",                     // Cláusula Horn
            "!x1 | x2",               // Cláusula Horn
            "x1 | x2 | x3",           // No es cláusula Horn
            "!x1 | !x2 | !x3"         // Cláusula Horn
        };

        // Verificar si cada cláusula es una cláusula Horn
        for (String clausula : clausulas) {
            System.out.println("Clausula: " + clausula + " -> Es Horn? " + esHornClause(clausula));
        }
    }
}