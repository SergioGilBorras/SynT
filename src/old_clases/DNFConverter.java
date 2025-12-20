/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package old_clases;

/*
Diferencias entre CNF y DNF:
-- Aspecto	CNF --
Forma principal	Conjunción de disyunciones
Enfoque	Más útil para SAT y resolución lógica
Ejemplo	(p∨¬q)∧(¬p∨r)
-- Aspecto	DNF --
Forma Disyunción de conjunciones
Enfoque	Más útil para enumerar casos positivos
Ejemplo	((p∧¬q)∨(r∧s)
*/

public class DNFConverter {

    // Método principal que convierte una expresión a DNF
    public static String convertToDNF(String expression) {
        // Paso 1: Eliminar las implicaciones
        expression = removeImplications(expression);

        // Paso 2: Aplicar las leyes de De Morgan y simplificar las expresiones.
        expression = applyDeMorgans(expression);

        // Paso 3: Distribuir AND sobre OR para obtener la DNF
        expression = distributeANDOverOR(expression);

        // Paso 4: Simplificar la expresión final.
        expression = simplifyExpression(expression);

        // Paso 5: Eliminar redundancias y términos contradictorios
        expression = removeContradictions(expression);
        
        // Paso 6: Manejar los paréntesis anidados y aplicar simplificación adicional
        expression = handleNestedParentheses(expression);
        

        return expression;
    }

    // Método para eliminar las implicaciones (A -> B -> !A | B)
    private static String removeImplications(String expression) {
        // Reemplazar las implicaciones de la forma A -> B por !A | B
        expression = expression.replaceAll("(\\w+)\\s*->\\s*(\\w+)", "!$1 | $2");
        // También cubrir el caso de implicación dentro de paréntesis
        expression = expression.replaceAll("\\((.*?)\\s*->\\s*(.*?)\\)", "(!$1) | $2");
        return expression;
    }

    // Método para aplicar las leyes de De Morgan
    private static String applyDeMorgans(String expression) {
        // Ley de De Morgan: !(A & B) -> !A | !B
        expression = expression.replaceAll("!(\\w+)\\s*&\\s!(\\w+)", "!$1 | !$2");

        // Ley de De Morgan: !(A | B) -> !A & !B
        expression = expression.replaceAll("!(\\w+)\\s*\\|\\s!(\\w+)", "!$1 & !$2");

        return expression;
    }

    // Método para distribuir AND sobre OR
    private static String distributeANDOverOR(String expression) {
        System.out.println(expression);
        
        // Distribución de AND sobre OR: (A | (B & C)) -> (A | B) & (A | C)
        // Expresión más compleja con paréntesis y múltiples elementos
        
        // Primero, buscamos expresiones de la forma A & (B | C)
        while (expression.contains("&") && expression.contains("|")) {
            // Distribuir AND sobre OR: (A & (B | C)) -> (A & B) | (A & C)
            expression = expression.replaceAll("(\\(.*?\\))\\s*&\\s*\\((.*?\\|.*?)\\)", "($1 & $2)");
            expression = expression.replaceAll("(\\w+)\\s*&\\s*\\((\\w+)\\s*\\|\\s*(\\w+)\\)", "($1 & $2) | ($1 & $3)");
        }
        return expression;
    }
    
    // Método para simplificar la expresión eliminando redundancias
    private static String simplifyExpression(String expression) {
        // Eliminamos redundancias como (A & A) -> A
        expression = expression.replaceAll("(\\w+)\\s*&\\s*\\1", "$1");

        // Eliminamos términos como A | A -> A
        expression = expression.replaceAll("(\\w+)\\s*\\|\\s*\\1", "$1");

        // Eliminamos expresiones como A & !A, o A | !A (contradicciones)
        expression = expression.replaceAll("(\\w+)\\s*&\\s*!\\1", "False");
        expression = expression.replaceAll("(\\w+)\\s*\\|\\s*!\\1", "True");

        return expression;
    }

    // Método para eliminar contradicciones (como A & !A o A | !A)
    private static String removeContradictions(String expression) {
        // Expresión como A & !A siempre es falsa
        expression = expression.replaceAll("(\\w+)\\s*&\\s*!\\1", "False");

        // Expresión como A | !A siempre es verdadera
        expression = expression.replaceAll("(\\w+)\\s*\\|\\s*!\\1", "True");

        return expression;
    }
    
    // Método para manejar paréntesis anidados
    private static String handleNestedParentheses(String expression) {
        System.out.println(expression);
        while (expression.contains("(")) {
            // Buscamos expresiones con paréntesis y las resolvemos
            expression = expression.replaceAll("\\(([^\\(\\)]+)\\)", "$1");
        }
        return expression;
    }

    public static void main(String[] args) {
        // Ejemplo de expresión booleana
        String expression = "(A & (B | C)) -> D";

        // Convertimos la expresión a DNF
        String dnfExpression = convertToDNF(expression);

        System.out.println("Expresión en DNF: " + dnfExpression);
    }
}
