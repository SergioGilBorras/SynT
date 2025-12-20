/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.swing.DefaultListModel;
import models.ColElements;
import models.Construct;
import models.Implication;
import models.Universe;
import models.Variable;
import theorybuildingse.ArregloCiclos;
import theorybuildingse.CycleDetectionDFSIterative;
import theorybuildingse.TransitiveClosure;
import theorybuildingse.TransitiveReduction;

/**
 * Utility class for building and analysing the implication model matrices
 * used by SynT.
 * <p>
 * This class keeps static state for the current node lists and matrices and
 * provides helpers to generate adjacency matrices, compute transitive
 * closures/reductions, serialize the model to JSON/CSV, and check graph
 * connectivity.
 * </p>
 */
public class utils {

    /**
     * List of all nodes (literals, possibly negated) in the current model.
     */
    private static List<String> nodos;
    /**
     * List of nodes after cycle fusion (reduced model).
     */
    private static List<String> nodosReducidos;
    /**
     * List of adjacency matrices representing the different model stages
     * (initial, cycle-reduced, transitive closure, transitive reduction,
     * cycle-restored, etc.).
     */
    private static List<int[][]> matrices;
    /**
     * Last error message produced during matrix generation, or {@code null}
     * if no error occurred.
     */
    private static String error;
    /**
     * Flag indicating whether the current model contains cycles.
     */
    private static boolean tieneCiclos;

    /**
     * Returns the list of all nodes of the current model.
     *
     * @return the list of node labels, or {@code null} if no model has been
     *         generated yet
     */
    public static List<String> getNodos() {
        return nodos;
    }

    /**
     * Returns the list of reduced nodes after cycle fusion.
     *
     * @return the list of reduced node labels, or {@code null} if cycles were
     *         not detected or the model has not been generated
     */
    public static List<String> getNodosReducidos() {
        return nodosReducidos;
    }

    /**
     * Returns the list of generated matrices for the current model.
     *
     * @return an ordered list of adjacency matrices, or {@code null} if none
     *         have been generated
     */
    public static List<int[][]> getListMatrices() {
        return matrices;
    }

    /**
     * Returns the last error message that occurred during matrix generation.
     *
     * @return the error message, or {@code null} if there was no error
     */
    public static String getError() {
        return error;
    }

    /**
     * Indicates whether the current model contains cycles.
     *
     * @return {@code true} if cycles were detected; {@code false} otherwise
     */
    public static boolean tieneCiclos() {
        return tieneCiclos;
    }

    /**
     * Clears all cached model data (nodes, reduced nodes, matrices and error
     * message).
     */
    public static void emptyMatriz() {
        if (nodos != null) {
            nodos.clear();
            nodos = null;
        }
        if (nodosReducidos != null) {
            nodosReducidos.clear();
            nodosReducidos = null;
        }
        if (matrices != null) {
            matrices.clear();
            matrices = null;
        }
        error = null;
    }

    /**
     * Counts the number of directed edges in the given adjacency matrix.
     * <p>
     * Any entry greater than zero is considered an edge.
     * </p>
     *
     * @param matrizL the adjacency matrix
     * @return the number of edges
     */
    public static int getNumeroAristas(int[][] matrizL) {
        int aristas = 0;
        for (int[] row : matrizL) {
            for (int val : row) {
                if (val > 0) {
                    aristas++;
                }
            }
        }
        return aristas;
    }

    /**
     * Generates all model matrices from the implication lists.
     * <p>
     * This method builds the initial adjacency matrix from the literals and
     * their negation flags, detects cycles and then delegates to either
     * {@link #generadorConCiclos(List)} or {@link #generadorSinCiclos(List)}
     * to compute the subsequent matrices.
     * </p>
     *
     * @param listModelLiterales1       list of literal 1 strings
     * @param listModelLiterales2       list of literal 2 strings
     * @param listModelImplicacionesNot1 list of negation flags for literal 1
     * @param listModelImplicacionesNot2 list of negation flags for literal 2
     * @return the list of generated matrices, or {@code null} if there is not
     *         enough content to build a model
     */
    public static List<int[][]> generarMatrices(DefaultListModel<String> listModelLiterales1,
            DefaultListModel<String> listModelLiterales2, DefaultListModel<String> listModelImplicacionesNot1,
            DefaultListModel<String> listModelImplicacionesNot2) {
        if (matrices == null) {
            List<String> nodosL = generarListaNodos(listModelLiterales1, listModelLiterales2, listModelImplicacionesNot1, listModelImplicacionesNot2);
            if (nodosL.size() > 1) {
                matrices = new ArrayList<>();
                int[][] matriz = new int[nodosL.size()][nodosL.size()];
                for (int i = 0; i < listModelLiterales1.size(); i++) {
                    String literal1a = (listModelImplicacionesNot1.get(i).equals("true") ? "¬ " : "") + listModelLiterales1.get(i);
                    String literal2a = (listModelImplicacionesNot2.get(i).equals("true") ? "¬ " : "") + listModelLiterales2.get(i);
                    int l1a = nodosL.indexOf(literal1a);
                    int l2a = nodosL.indexOf(literal2a);
                    matriz[l1a][l2a] = 1;
                    String literal1b = (listModelImplicacionesNot1.get(i).equals("false") ? "¬ " : "") + listModelLiterales1.get(i);
                    String literal2b = (listModelImplicacionesNot2.get(i).equals("false") ? "¬ " : "") + listModelLiterales2.get(i);
                    int l1b = nodosL.indexOf(literal2b);
                    int l2b = nodosL.indexOf(literal1b);
                    matriz[l1b][l2b] = 1;
                }
                matrices.add(matriz);

                tieneCiclos = CycleDetectionDFSIterative.hasCycle(matriz);

                if (tieneCiclos) {
                    generadorConCiclos(matrices);
                } else {
                    generadorSinCiclos(matrices);
                }
            }
        }
        return matrices;
    }

    /**
     * Generates matrices for models that contain cycles.
     * <p>
     * The sequence is: initial matrix, cycle-fused matrix, transitive
     * closure, transitive reduction on the reduced graph and finally a
     * matrix with cycles restored.
     * </p>
     *
     * @param matrices the list where the new matrices will be appended
     */
    private static void generadorConCiclos(List<int[][]> matrices) {
        ArregloCiclos AR;

        AR = new ArregloCiclos(matrices.get(0), utils.nodos);
        matrices.add(AR.getMatrizFusionCiclos());
        utils.nodosReducidos = AR.getNodes();

        matrices.add(TransitiveClosure.computeTransitiveClosure(matrices.get(1)));
        matrices.add(TransitiveReduction.computeTransitiveReduction(matrices.get(2)));

        try {
            matrices.add(AR.restaurarCiclos(matrices.get(3)));
        } catch (Exception ex) {
            error = "An error occurred while generating the matrices: " + ex.getMessage();
            ex.printStackTrace();
        }

    }

    /**
     * Generates matrices for models without cycles.
     * <p>
     * The sequence is: initial matrix, transitive closure and transitive
     * reduction.
     * </p>
     *
     * @param matrices the list where the new matrices will be appended
     */
    private static void generadorSinCiclos(List<int[][]> matrices) {
        matrices.add(TransitiveClosure.computeTransitiveClosure(matrices.get(0)));
        matrices.add(TransitiveReduction.computeTransitiveReduction(matrices.get(1)));
    }

    /**
     * Builds the list of all nodes (literals with optional negation) present
     * in the implication lists.
     * <p>
     * For each literal, both its negated and non-negated forms are added to
     * the list if they were not present already. The resulting list is then
     * sorted lexicographically.
     * </p>
     *
     * @param listModelLiterales1       list of literal 1 strings
     * @param listModelLiterales2       list of literal 2 strings
     * @param listModelImplicacionesNot1 list of negation flags for literal 1
     * @param listModelImplicacionesNot2 list of negation flags for literal 2
     * @return a sorted list of all node labels
     */
    public static List<String> generarListaNodos(DefaultListModel<String> listModelLiterales1,
            DefaultListModel<String> listModelLiterales2, DefaultListModel<String> listModelImplicacionesNot1,
            DefaultListModel<String> listModelImplicacionesNot2) {
        if (nodos == null) {
            nodos = new ArrayList<>();
            for (int i = 0; i < listModelLiterales1.size(); i++) {
                String literal1 = (listModelImplicacionesNot1.get(i).equals("true") ? "¬ " : "") + listModelLiterales1.get(i);
                if (!nodos.contains(literal1)) {
                    String literal2 = (listModelImplicacionesNot1.get(i).equals("false") ? "¬ " : "") + listModelLiterales1.get(i);

                    nodos.add(literal1);
                    nodos.add(literal2);
                }
            }
            for (int i = 0; i < listModelLiterales2.size(); i++) {
                String literal1 = (listModelImplicacionesNot2.get(i).equals("true") ? "¬ " : "") + listModelLiterales2.get(i);
                if (!nodos.contains(literal1)) {
                    String literal2 = (listModelImplicacionesNot2.get(i).equals("false") ? "¬ " : "") + listModelLiterales2.get(i);

                    nodos.add(literal1);
                    nodos.add(literal2);
                }
            }
            Collections.sort(nodos);
        }
        return nodos;
    }

    /**
     * Serializes the current model (constructs, universes, variables and
     * implications) to a JSON string.
     *
     * @param colConstructs            collection of constructs
     * @param colUniverses             collection of universes
     * @param colVariables             collection of variables
     * @param colImplications          collection of implications
     * @return a JSON document representing the full SynT model
     */
    public static String generarJSON(
            ColElements<Construct> colConstructs, ColElements<Universe> colUniverses, 
            ColElements<Variable> colVariables,   ColElements<Implication> colImplications
    ) {
        StringBuilder JSON = new StringBuilder("""
                {
                  "Constructs": [
                """);
        for (int i = 0; i < colConstructs.size(); i++) {
            JSON.append("    {\n" + "      \"Name\": \"").append(colConstructs.getModelElement().get(i).getName()).append("\",\n").append("      \"From\": \"").append(colConstructs.getModelElement().get(i).getFrom()).append("\",\n").append("      \"Scope\": \"").append(colConstructs.getModelElement().get(i).getScope()).append("\"\n");
            if (i != colConstructs.size() - 1) {
                JSON.append("    },\n");
            } else {
                JSON.append("    }\n");
            }
        }
        /* //////////////////// Universes //////////////////////////////////////////////// */
        JSON.append("""
                   ],
                   "Universes": [
                """);
        for (int i = 0; i < colUniverses.size(); i++) {
            JSON.append("    {\n" + "      \"Name\": \"").append(colUniverses.getModelElement().get(i).getName()).append("\",\n").append("      \"Type\": \"").append(colUniverses.getModelElement().get(i).getType()).append("\",\n").append("      \"ValueEnum\": \"").append(colUniverses.getModelElement().get(i).getValueEnum()).append("\",\n").append("      \"ValueMin\": \"").append(colUniverses.getModelElement().get(i).getValueMin()).append("\",\n").append("      \"ValueMax\": \"").append(colUniverses.getModelElement().get(i).getValueMax()).append("\",\n").append("      \"Function\": \"").append(colUniverses.getModelElement().get(i).getFunction()).append("\",\n").append("      \"Aridad\": \"").append(colUniverses.getModelElement().get(i).getAridad()).append("\",\n").append("      \"Equal\": \"").append(colUniverses.getModelElement().get(i).isEqual()).append("\",\n").append("      \"Greater\": \"").append(colUniverses.getModelElement().get(i).isGreater()).append("\",\n").append("      \"Greater_equal\": \"").append(colUniverses.getModelElement().get(i).isGreater_equal()).append("\",\n").append("      \"Not_equal\": \"").append(colUniverses.getModelElement().get(i).isNot_equal()).append("\",\n").append("      \"Less\": \"").append(colUniverses.getModelElement().get(i).isLess()).append("\",\n").append("      \"Less_equal\": \"").append(colUniverses.getModelElement().get(i).isLess_equal()).append("\"\n");
            if (i != colUniverses.size() - 1) {
                JSON.append("    },\n");
            } else {
                JSON.append("    }\n");
            }
        }
        /* /////////////////////// Variables ///////////////////////////////////////////// */
        JSON.append("""
                   ],
                   "Variables": [
                """);
        for (int i = 0; i < colVariables.size(); i++) {
            JSON.append("    {\n" + "      \"Name\": \"").append(colVariables.getModelElement().get(i).getName()).append("\",\n").append("      \"Nickname\": \"").append(colVariables.getModelElement().get(i).getNickname()).append("\",\n").append("      \"ConstructName\": \"").append(colVariables.getModelElement().get(i).getConstruct().getName()).append("\",\n").append("      \"UniverseName\": \"").append(colVariables.getModelElement().get(i).getUniverse().getName()).append("\"\n");
            if (i != colVariables.size() - 1) {
                JSON.append("    },\n");
            } else {
                JSON.append("    }\n");
            }
        }
        /* ////////////////////////// Implicaciones ////////////////////////////////////////// */
        JSON.append("""
                  ],
                  "Implications": [
                """);
        
        for (int i = 0; i < colImplications.size(); i++) {
            JSON.append("    {\n" + "      \"Variable1\": \"").append(colImplications.getModelElement().get(i).getVariable1().getNickname()).append("\",\n");
            JSON.append("      \"Relation1\": \"").append(colImplications.getModelElement().get(i).getRelation1()).append("\",\n");
            JSON.append("      \"Value1\": \"").append(colImplications.getModelElement().get(i).getValue1()).append("\",\n");
            JSON.append("      \"Negated1\": \"").append(colImplications.getModelElement().get(i).isNegated1()).append("\",\n");
            JSON.append("      \"Variable2\": \"").append(colImplications.getModelElement().get(i).getVariable2().getNickname()).append("\",\n");
            JSON.append("      \"Relation2\": \"").append(colImplications.getModelElement().get(i).getRelation2()).append("\",\n");
            JSON.append("      \"Value2\": \"").append(colImplications.getModelElement().get(i).getValue2()).append("\",\n");
            JSON.append("      \"Negated2\": \"").append(colImplications.getModelElement().get(i).isNegated2()).append("\",\n");
            if (i != colImplications.size() - 1) {
                JSON.append("    },\n");
            } else {
                JSON.append("    }\n");
            }
        }
        JSON.append("  ]\n" + "}");
        return JSON.toString();
    }

    /**
     * Builds a CSV representation of the given adjacency matrix.
     * <p>
     * The first row contains the node labels as headers, the first column of
     * each subsequent row contains the row node label, and the remaining
     * columns contain the matrix values.
     * </p>
     *
     * @param matrizL the adjacency matrix
     * @param nodosL  the list of node labels corresponding to rows/columns
     * @return the CSV representation as a string
     */
    public static String generarTablaCSV(int[][] matrizL, List<String> nodosL) {
        StringBuilder CSV = new StringBuilder(";");
        for (String s : nodosL) {
            CSV.append(s).append(";");
        }
        CSV.append("\n");
        for (int i = 0; i < matrizL.length; i++) {
            CSV.append(nodosL.get(i)).append(";");
            for (int j = 0; j < matrizL[i].length; j++) {
                CSV.append(matrizL[i][j]).append(";");
            }
            CSV.append("\n");
        }
        return CSV.toString();
    }

    /**
     * Checks whether two nodes are connected in an (undirected) sense in the
     * given matrix.
     * <p>
     * The search is performed with a breadth-first search (BFS) over the
     * implicit graph defined by the matrix, where an edge is considered
     * present if either {@code matriz[a][b] > 0} or {@code matriz[b][a] > 0}.
     * </p>
     *
     * @param matriz the adjacency matrix
     * @param nodo1  the index of the source node
     * @param nodo2  the index of the target node
     * @return {@code true} if there is a path between {@code nodo1} and
     *         {@code nodo2}; {@code false} otherwise
     */
    public static boolean estanConectadosNodos(int[][] matriz, int nodo1, int nodo2) {
        if (nodo1 == nodo2) {
            return true;
        }

        int n = matriz.length;
        boolean[] visitado = new boolean[n];
        Queue<Integer> cola = new LinkedList<>();

        cola.add(nodo1);
        visitado[nodo1] = true;

        while (!cola.isEmpty()) {
            int actual = cola.poll();
            if (actual == nodo2) {
                return true;
            }

            for (int i = 0; i < n; i++) {
                if ((matriz[actual][i] > 0 || matriz[i][actual] > 0) && !visitado[i]) {
                    cola.add(i);
                    visitado[i] = true;
                }
            }
        }

        return false;
    }
}
