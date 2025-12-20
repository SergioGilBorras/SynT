/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

    
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;

import java.util.ArrayList;
import java.util.List;
import org.logicng.formulas.FType;

public class CNFToImplications_LogicNG {

    public static void main(String[] args) {
        // Crear una fábrica de fórmulas
        FormulaFactory formulaFactory = new FormulaFactory();

        // Ejemplo de fórmula CNF: (A ∨ B) ∧ (¬A ∨ C) ∧ (B ∨ ¬C)
        Formula cnfFormula = formulaFactory.and(
            formulaFactory.or(formulaFactory.variable("A"), formulaFactory.variable("B")),
            formulaFactory.or(formulaFactory.not(formulaFactory.variable("A")), formulaFactory.variable("C")),
            formulaFactory.or(formulaFactory.variable("B"), formulaFactory.not(formulaFactory.variable("C")))
        );

        System.out.println("Formula en CNF: " + cnfFormula);

        // Convertir cada cláusula de la CNF en implicaciones
        List<Formula> implications = convertCNFToImplications(cnfFormula, formulaFactory);

        // Mostrar las implicaciones resultantes
        System.out.println("Implicaciones:");
        for (Formula implication : implications) {
            System.out.println(implication);
        }
    }

    /**
     * Convierte una fórmula CNF en una lista de implicaciones.
     * @param cnf
     * @param formulaFactory
     * @return 
     */
    public static List<Formula> convertCNFToImplications(Formula cnf, FormulaFactory formulaFactory) {
        List<Formula> implications = new ArrayList<>();
        
        // Asegurarse de que la fórmula es una conjunción
        if (cnf.type() == FType.AND) {
            for (Formula clause : cnf) {
                System.out.println("clause AND: "+clause);
                implications.add(convertClauseToImplication(clause, formulaFactory));
            }
        } else {
            // Si la fórmula no es una conjunción, tratarla como una sola cláusula
            implications.add(convertClauseToImplication(cnf, formulaFactory));
        }

        return implications;
    }

    /**
     * Convierte una cláusula (disyunción de literales) a una implicación.
     */
    private static Formula convertClauseToImplication(Formula clause, FormulaFactory formulaFactory) {
        // Convertir cada cláusula (A ∨ B ∨ ¬C) a una implicación lógica
        if (clause.type() == FType.OR) {
            // Tomar el primer literal como el antecedente
            List<Formula> literals = new ArrayList<>(clause.literals());//operands());
            Formula antecedent = formulaFactory.not(literals.remove(0)); // Negar el primer literal
            Formula consequent = formulaFactory.or(literals);

            return formulaFactory.implication(antecedent, consequent);
        } else if (clause instanceof Literal) {
            // Si la cláusula es un literal (A), convertir a TRUE → A
            return formulaFactory.implication(formulaFactory.verum(), clause);
        }

        throw new IllegalArgumentException("La cláusula no está en formato válido: " + clause);
    }
}
