/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package old_clases;

import theorybuildingse.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility to remove cycles from a directed graph represented as an adjacency
 * matrix by contracting (merging) strongly connected components (SCCs) into a
 * single node, and later being able to reconstruct (restore) those cycles.
 * <p>
 * Internally it works in "levels" (or "iterations"): each SCC merge produces a
 * new matrix and a new list of node names. The level history is stored in
 * {@link #lMatrizAdj} and {@link #lOriginalNodes}.
 * </p>
 * <p>
 * Naming convention: when an SCC is merged, the combined node is represented as
 * the concatenation of the original node names separated by {@code "/"} (e.g.
 * {@code "A/B/C"}). This convention is later used to map indices during the
 * restore step.
 * </p>
 *
 * Note: the merge aggregates (sums) edges to/from the combined node. If your
 * matrix is binary (0/1), this sum can produce values &gt; 1 when multiple edges
 * collapse into the same entry.
 *
 * @author Sergio
 */
public class ArregloCiclosSinMapedado {

    /**
     * Level history of node-name lists.
     * <ul>
     * <li>Index 0: original list provided to the constructor.</li>
     * <li>Last index: node list of the graph after merging cycles.</li>
     * </ul>
     * Each merge appends a new list where the combined node is added as the last
     * element using the {@code "n1/n2/..."} format.
     */
    private final ArrayList<List<String>> lOriginalNodes = new ArrayList<>();

    /**
     * Level history of adjacency matrices.
     * <ul>
     * <li>Index 0: original matrix provided to the constructor.</li>
     * <li>Last index: resulting matrix without cycles (after SCC merges).</li>
     * </ul>
     */
    private final ArrayList<int[][]> lMatrizAdj = new ArrayList<>();

    /**
     * Index of the level currently being processed.
     * <p>
     * During the merge phase it advances from 0 up to the last created level.
     * During the restore phase it is set to traverse levels in reverse order.
     * </p>
     */
    private int vueltaArreglos = 0;

    /**
     * Creates an instance and executes the cycle-merging process.
     * <p>
     * While the graph still contains cycles, SCCs (Tarjan) are computed on the
     * current matrix, and the first SCC found with size &gt; 1 is merged. The result
     * of each merge is stored as a new level in {@link #lMatrizAdj} and
     * {@link #lOriginalNodes}.
     * </p>
     *
     * @param matrizAdj square adjacency matrix (N x N).
     * @param nodes list of node names; its size must be N and it must follow the
     *              same order as the indices of {@code matrizAdj}.
     */
    public ArregloCiclosSinMapedado(int[][] matrizAdj, List<String> nodes) {
        this.lOriginalNodes.add(nodes);
        this.lMatrizAdj.add(matrizAdj);
        while (CycleDetectionDFSIterative.hasCycle(lMatrizAdj.get(vueltaArreglos))) {
//            System.out.println("-- VUELTA ciclo -- ");
            TarjanCondensedGraph g = new TarjanCondensedGraph(lMatrizAdj.get(vueltaArreglos));
            for (Set<Integer> SCC : g.findSCCs()) {
                List<Integer> lSCC = new ArrayList<>(SCC);
                Collections.sort(lSCC);
                if (SCC.size() > 1) {
//                    System.out.println("Encuentra ciclo: " + SCC);
                    lMatrizAdj.add(fusionarNodosCiclo(lMatrizAdj.get(vueltaArreglos), lSCC));

                    vueltaArreglos++;
                    break;
                }
            }
        }
    }

    /**
     * Returns the node list of the last level (graph with SCCs merged).
     *
     * @return list of node names aligned with {@link #getMatrizFusionCiclos()}.
     */
    public List<String> getNodes() {
        return lOriginalNodes.get(lOriginalNodes.size() - 1);
    }

    /**
     * Returns the final matrix obtained after merging cycles.
     * <p>
     * Important: this returns the internal reference stored in the history. If you
     * need immutability, make a copy before modifying it.
     * </p>
     *
     * @return adjacency matrix of the last level (acyclic).
     */
    public int[][] getMatrizFusionCiclos() {
        return lMatrizAdj.get(lMatrizAdj.size() - 1);
    }

    /**
     * Restores (expands) the cycles previously merged into the history.
     * <p>
     * This method traverses the levels created during the merge phase from the
     * last one back to the first one, and replaces each combined node with its
     * sub-nodes, reconstructing the associated cycle.
     * </p>
     *
     * @param matrizAdj matrix to restore. Typically this is the matrix returned by
     *                  {@link #getMatrizFusionCiclos()} (or a transformation of it),
     *                  so it must correspond to the last level.
     * @return a new matrix obtained by successive expansions until reaching the
     *         matrix with the original nodes.
     * @throws Exception if the name-based mapping between levels cannot be resolved
     *                   (see {@link #relacionNodos(int, int)}).
     */
    public int[][] restaurarCiclos(int[][] matrizAdj) throws Exception {
        for (int i = lOriginalNodes.size() - 1; i > 0; i--) {
            vueltaArreglos = i;

            List<String> lNodes = lOriginalNodes.get(i);
            int n = lNodes.size() - 1;
//            System.out.println("-- VUELTA ciclo Restaura -- :: " + vueltaArreglos);
//            for (String lNode : lNodes) {
//                System.out.println("Nodo: " + lNode);
//            }
            matrizAdj = agregarNodosCiclo(matrizAdj, n, lNodes.get(n).split("/").length - 1);
            //vueltaArreglos--;
        }
        vueltaArreglos = 0;
        return matrizAdj;
    }

    /**
     * Merges (contracts) a set of nodes that form a cycle/SCC into a single node.
     * <p>
     * The selected nodes are replaced by a new node placed at the last index of
     * the resulting matrix. Edges entering or leaving the set are aggregated into
     * the new node by combining (summing) weights.
     * </p>
     * <p>
     * It also creates the next level in {@link #lOriginalNodes} by adding a
     * combined name using the {@code "n1/n2/..."} format.
     * </p>
     *
     * @param matriz adjacency matrix of the current level.
     * @param nodosSeleccionados indices (in the current level) to be contracted.
     * @return adjacency matrix with the cycle merged into a single node.
     */
    private int[][] fusionarNodosCiclo(int[][] matriz, List<Integer> nodosSeleccionados) {
        int n = matriz.length;
        int nuevoTamano = n - nodosSeleccionados.size() + 1; // Tamaño de la nueva matriz
        int nuevoNodoIndex = nuevoTamano - 1;

//        System.out.println("n:: " + n);
//        System.out.println("nuevoTamano:: " + nuevoTamano);
//        System.out.println("nuevoNodoIndex:: " + nuevoNodoIndex);
        // Crear una nueva matriz de adyacencia
        int[][] nuevaMatriz = new int[nuevoTamano][nuevoTamano];

        // Mapeo de índices antiguos a nuevos
        Map<Integer, Integer> mapeo = new HashMap<>();
        int nuevoIndice = 0;
        lOriginalNodes.add(new ArrayList<>());
        for (int i = 0; i < n; i++) {
            if (!nodosSeleccionados.contains(i)) {
                mapeo.put(i, nuevoIndice++);
                // Añado los nombre de los nodos no seleccionados
                lOriginalNodes.get(vueltaArreglos + 1).add(lOriginalNodes.get(vueltaArreglos).get(i));
            }
        }
        // Añado el nombre del nodo comun
        String nodosSelectName = "";
        for (Integer nodoSeleccionado : nodosSeleccionados) {
//            System.out.println("lOriginalNodes.size:: " + lOriginalNodes.size());
//            System.out.println("vueltaArreglos:: " + vueltaArreglos);
//            System.out.println("lOriginalNodes.get(vueltaArreglos):: " + lOriginalNodes.get(vueltaArreglos).size());
//            System.out.println("nodoSeleccionado:: " + nodoSeleccionado);
            nodosSelectName += lOriginalNodes.get(vueltaArreglos).get(nodoSeleccionado) + "/";
        }
        lOriginalNodes.get(vueltaArreglos + 1).add(nodosSelectName.substring(0, nodosSelectName.length() - 1));

        mapeo.put(-1, nuevoNodoIndex); // -1 representa el nuevo nodo combinado

        // Llenar la nueva matriz
        for (int i = 0; i < n; i++) {
            if (nodosSeleccionados.contains(i)) {
                continue;
            }

            for (int j = 0; j < n; j++) {
                if (nodosSeleccionados.contains(j)) {
                    // Conexión hacia el nuevo nodo
                    nuevaMatriz[mapeo.get(i)][nuevoNodoIndex] += matriz[i][j];
                } else {
                    // Conexión entre nodos no seleccionados
                    nuevaMatriz[mapeo.get(i)][mapeo.get(j)] = matriz[i][j];
                }
            }
        }

        // Completar las conexiones del nuevo nodo combinado
        for (int seleccionado : nodosSeleccionados) {
            for (int i = 0; i < n; i++) {
                if (!nodosSeleccionados.contains(i)) {
                    nuevaMatriz[nuevoNodoIndex][mapeo.get(i)] += matriz[seleccionado][i];
                }
            }
        }

        return nuevaMatriz;
    }

    /**
     * Maps an index (and a position within a combined name) from level
     * {@link #vueltaArreglos} to the corresponding index in the previous level.
     * <p>
     * The mapping is name-based: if the current node is a combined node (e.g.
     * {@code "A/B/C"}), {@code posicion} indicates which sub-node should be used.
     * </p>
     *
     * @param nuevoNodo node index in the current (or restored) level. If it exceeds
     *                  the size of the level list, it is clamped to the last index.
     * @param posicion sub-node position within the combined name (result of
     *                 {@code split("/")}).
     * @return equivalent node index in the previous level.
     * @throws Exception if the node/sub-node cannot be found in the previous level.
     */
    private int relacionNodos(int nuevoNodo, int posicion) throws Exception {
        List<String> nodosIniciales = lOriginalNodes.get(vueltaArreglos - 1);

//        System.out.println("lOriginalNodes size:: " + lOriginalNodes.size());
//        System.out.println("vueltaArreglos:: " + vueltaArreglos);
//        System.out.println("lOriginalNodes.get(vueltaArreglos) size:: " + lOriginalNodes.get(vueltaArreglos).size());
//        System.out.println("nuevoNodo:: " + nuevoNodo);
//        System.out.println("posicion:: " + posicion);
        if (lOriginalNodes.get(vueltaArreglos).size() <= nuevoNodo) {
            nuevoNodo = lOriginalNodes.get(vueltaArreglos).size() - 1;
        }
//        System.out.println("nuevoNodo String:: " + lOriginalNodes.get(vueltaArreglos).get(nuevoNodo));
//        System.out.println("split length:: " + lOriginalNodes.get(vueltaArreglos).get(nuevoNodo).split("/").length);

        String nodoBuscado = lOriginalNodes.get(vueltaArreglos).get(nuevoNodo).split("/")[posicion];

//        System.out.println("nodoBuscado:: " + nodoBuscado);
        for (int i = 0; i < nodosIniciales.size(); i++) {
            // System.out.println("nodosIniciales:: " + nodosIniciales.get(i));
            String[] subNodos = nodosIniciales.get(i).split("/");
            if (nodoBuscado.equals(nodosIniciales.get(i))) {
                return i;
            } else if (subNodos.length > 1) {
                for (String subNodo : subNodos) {
                    if (nodoBuscado.equals(subNodo)) {
                        return i;
                    }
                }
            }
        }
        throw new Exception("Error [ArregloCiclosSinMapedado.realacionNodos()] :: Nodo no encontrado.");
    }

    /**
     * Expands a combined node by reconstructing the cycle of its sub-nodes.
     * <p>
     * Starting from a matrix at level {@link #vueltaArreglos}, this method creates a
     * larger matrix that reintroduces {@code NumNuevosNodos} additional nodes (in
     * addition to the existing node representing the cycle). Then it rebuilds the
     * cycle edges and re-maps existing edges to the previous level.
     * </p>
     *
     * @param matriz adjacency matrix of the current level (with the combined node).
     * @param nodoSeleccionado index of the combined node in the node list of the
     *                         current level (typically the last node).
     * @param NumNuevosNodos number of extra nodes to add to expand the combined node.
     *                      Usually {@code combinado.split("/").length - 1}.
     * @return new matrix with the cycle nodes expanded.
     * @throws Exception if index mapping between levels fails.
     */
    private int[][] agregarNodosCiclo(int[][] matriz, int nodoSeleccionado, int NumNuevosNodos) throws Exception {
        int n = matriz.length; // Tamaño original de la matriz sin el ciclo expandido
        int nuevoTamano = n + NumNuevosNodos;

        int[][] nuevaMatriz = new int[nuevoTamano][nuevoTamano];

        // 1\) Copiar la matriz original a la nueva (re-mapeando índices al nivel anterior)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matriz[i][j] > 0) {
                    nuevaMatriz[relacionNodos(i, 0)][relacionNodos(j, 0)] = matriz[i][j];
                }
            }
        }

        // 2\) Índices de los nuevos nodos en la matriz restaurada
        int primerNuevoNodoMatriz = n;                 // índice del primer NUEVO nodo en la matriz
        int ultimoNuevoNodoMatriz = nuevoTamano - 1;   // índice del último NUEVO nodo en la matriz

        // 3\) Número de subnodos del nodo combinado en este nivel
        //    Ojo: aquí usamos *nodoSeleccionado* como índice en lOriginalNodes
        String combinado = lOriginalNodes.get(vueltaArreglos)
                .get(nodoSeleccionado);
        String[] partes = combinado.split("/");
        int len = partes.length - 1; // última posición de subnodo

        // 4\) Conexión del primer nodo del ciclo hacia el nodo seleccionado (en la matriz nueva)
        //    - `primerNuevoNodoMatriz` es índice de matriz
        //    - `1` es la posición del segundo subnodo dentro del combinado (el primero es el que ya estaba)
        nuevaMatriz[relacionNodos(primerNuevoNodoMatriz, 1)]
                [relacionNodos(nodoSeleccionado, 0)] = 1;

        // 5\) Conexión del nodo seleccionado hacia el último nodo del ciclo
        nuevaMatriz[relacionNodos(nodoSeleccionado, 0)]
                [relacionNodos(ultimoNuevoNodoMatriz, len)] = 1;

        // 6\) Conectar los nuevos nodos entre sí para formar el ciclo
        //    Recorremos las posiciones intermedias del nombre combinado
        for (int i = 0; i < NumNuevosNodos - 1; i++) {
            // Todos los subnodos están dentro del mismo combinado (`nodoSeleccionado`)
            int nodoActual = relacionNodos(nodoSeleccionado, i + 1);
            int nodoSiguiente = relacionNodos(nodoSeleccionado, i + 2);

            // Conexión del nodo actual al siguiente nodo en el ciclo
            nuevaMatriz[nodoSiguiente][nodoActual] = 1;
        }

        return nuevaMatriz;
    }

    /**
     * Manual example (demo): builds a matrix with a cycle, merges it, and then
     * tries to restore it.
     * <p>
     * Not part of the main API; used for debugging/visual validation.
     * </p>
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        int[][] matrix = new int[10][10];
        matrix[0][7] = 1;
        matrix[2][1] = 1;
        matrix[1][0] = 1;
        matrix[1][3] = 1;
        matrix[3][4] = 1;
        matrix[5][0] = 1;
        matrix[1][6] = 1;
        matrix[7][8] = 1;
        matrix[8][2] = 1;

        matrix[6][9] = 1;
//        TarjanCondensedGraph g1 = new TarjanCondensedGraph(matrix);
//        ArrayList<Set<Integer>> SCCs = g1.findSCCs();  // Obtengo los componentes fuertemente conexos
//
//        g1.printListSetNodes();
        List<String> nodes = new ArrayList<>();
        nodes.add("A");
        nodes.add("B");
        nodes.add("C");
        nodes.add("D");
        nodes.add("E");
        nodes.add("F");
        nodes.add("G");
        nodes.add("H");
        nodes.add("I");
        nodes.add("J");

        System.out.println("\n-- MATRIZ ORIGINAL --");

        TheoryToImplicationTheory.printMatrix(matrix, nodes);

        MatrixToLatex.toLatex(matrix);

        dibujaGrafos DG = new dibujaGrafos();
        DG.dibujar(matrix, nodes);

        ArregloCiclosSinMapedado AR = new ArregloCiclosSinMapedado(matrix, nodes);

        int[][] matrix1 = AR.getMatrizFusionCiclos();
        List<String> nodes1 = AR.getNodes();

        System.out.println("\n-- MATRIZ SIN CICLOS --");

        TheoryToImplicationTheory.printMatrix(matrix1, nodes1);

        MatrixToLatex.toLatex(matrix1);

        DG.redibujar(matrix1, nodes1);

        int[][] matrix2;
        try {

            System.out.println("\n-- MATRIZ RECOSTRUIDA --");
            matrix2 = AR.restaurarCiclos(matrix1);

            TheoryToImplicationTheory.printMatrix(matrix2, nodes);

            MatrixToLatex.toLatex(matrix2);

            DG.redibujar(matrix2, nodes);
        } catch (Exception ex) {
            System.out.println("Error:: " + ex.getMessage());
            ex.printStackTrace();
        }
        DG.close();
    }
}
