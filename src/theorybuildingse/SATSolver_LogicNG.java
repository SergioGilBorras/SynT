/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.solvers.MiniSat;
import org.logicng.datastructures.Tristate;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PseudoBooleanParser;
import org.logicng.transformations.simplification.AdvancedSimplifier;

public class SATSolver_LogicNG {

    String modelo = "";
    boolean isfactible = false;
    Formula formulaCNFSimplificada;
    FormulaFactory formulaFactory;
    public static boolean debug = true;

    public static void main(String[] args) {

        String expression = "(A & B) | C & (A => C)";

        SATSolver_LogicNG SATSolver = new SATSolver_LogicNG(expression);

        if (SATSolver.isfactible()) {
            System.out.println("La formula es factible.");

            // Obtener un modelo (una asignación que satisface la fórmula)
            System.out.println("Modelo encontrado: " + SATSolver.getModelo());
        } else {
            System.out.println("La formula no es factible.");
        }

    }

    public SATSolver_LogicNG(String expression) {

        // Paso 1: Crear una fábrica de fórmulas
        formulaFactory = new FormulaFactory();
        // Paso 2: Definir una fórmula lógica
        // Ejemplo: (A ∨ ¬B) ∧ (¬A ∨ C)
//        Formula formula = formulaFactory.and(
//                formulaFactory.or(formulaFactory.variable("A"), formulaFactory.not(formulaFactory.variable("B"))),
//                formulaFactory.or(formulaFactory.not(formulaFactory.variable("A")), formulaFactory.variable("C"))
//        );
        if (debug) {
            System.out.println("Formula inicial: " + expression);
        }

        PseudoBooleanParser parser = new PseudoBooleanParser(formulaFactory);
        try {
            // Parsear la fórmula
            Formula formula = parser.parse(expression);

            Formula cnfFormula_1 = formula.cnf();
            if (debug) {
                System.out.println("Formula en CNF: " + cnfFormula_1);
            }
            AdvancedSimplifier AVSimplifier = new AdvancedSimplifier();
            Formula simplifiedFormula = AVSimplifier.apply(formula, false);
            if (debug) {
                System.out.println("Formula simplificada: " + simplifiedFormula);
            }
            // Paso 3: Convertir a CNF (opcional, el SATSolver puede manejarlo internamente)
            Formula cnfFormula = simplifiedFormula.cnf();
            if (debug) {
                System.out.println("Formula simplificada en CNF: " + cnfFormula);
            }
            // Paso 4: Crear el SATSolver (MiniSat es un solver integrado en LogicNG)
            MiniSat solver = MiniSat.miniSat(formulaFactory);
            // Paso 5: Agregar la fórmula al SATSolver
            solver.add(cnfFormula);

            formulaCNFSimplificada = cnfFormula;

            // Paso 6: Comprobar la satisfacibilidad
            isfactible = (solver.sat() == Tristate.TRUE);
            if (isfactible) {

                // Obtener un modelo (una asignación que satisface la fórmula)
                modelo = solver.model().toString();
            }
        } catch (ParserException e) {
            System.out.println("Excepcion: " + e.getMessage());

        }

    }

    public String getModelo() {
        return modelo;
    }

    public boolean isfactible() {
        return isfactible;
    }

    public Formula getFormulaCNFSimplificada() {
        return formulaCNFSimplificada;
    }

    public FormulaFactory getFormulaFactory() {
        return formulaFactory;
    }
    
    
}
