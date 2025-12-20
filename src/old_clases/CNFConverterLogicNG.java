/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package old_clases;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PseudoBooleanParser;
import org.logicng.transformations.cnf.CNFFactorization;

public class CNFConverterLogicNG {

    /*
    
Object          Factory Method          Syntax
True            f.verum()               $true
False           f.falsum()              $false
Variable	f.variable("A")         A
Literal         f.literal("A", false)	~A
Negation	f.not(f1)               ~f1
Implication	f.implication(f1, f2)	f1 => f2
Equivalence	f.equivalence(f1, f2)	f1 <=> f2
Conjunction	f.and(f1, f2, f3)	f1 & f2 & f3
Disjunction	f.or(f1, f2, f3)	f1 | f2 | f3
    
    */
    
    public static void main(String[] args) {
        // Crear una fábrica de fórmulas
        FormulaFactory f = new FormulaFactory();

        // Crear un parser para analizar la expresión lógica
        PseudoBooleanParser parser = new PseudoBooleanParser(f);

        // Expresión lógica en cadena
        String expression = "(A & B) | C & (A => C)";

        try {
            // Parsear la fórmula
            Formula formula = parser.parse(expression);
            Formula cnfFormula_v1 = formula.cnf();
            // Normalizar a CNF (Convertir a CNF)
            CNFFactorization cnf = new CNFFactorization();
            Formula cnfFormula = cnf.apply(formula, false);
            // Imprimir la fórmula CNF
            System.out.println("Expresion en CNF: " + cnfFormula);
            System.out.println("Expresion en CNF: " + cnfFormula_v1);
        } catch (ParserException e) {;
            System.out.println("Excepcion: " + e.getMessage());
           
        }
    }
}
