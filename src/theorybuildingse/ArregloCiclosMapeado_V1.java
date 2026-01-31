/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

import java.util.*;

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
public class ArregloCiclosMapeado_V1 extends ArregloCiclos {

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
        throw new Exception("Error [ArregloCiclosSinMapedado_V1.realacionNodos()] :: Nodo no encontrado.");
    }


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
    public ArregloCiclosMapeado_V1(int[][] matrizAdj, List<String> nodes) {
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
     * Restores (expands) the cycles previously merged into the history, and places
     * each restored edge on the original node it belonged to according to the
     * provided {@code matrizOriginal}.
     * <p>
     * Compared to the older behaviour (where all hanging edges were attached to a
     * single representative node), this version tries to attach each incoming/outgoing
     * edge of a combined node to the most appropriate sub-node, by inspecting the
     * original adjacency matrix.
     * </p>
     * <p>
     * Rules:
     * <ul>
     *   <li>If multiple original sub-nodes match, the one with the smallest index is chosen (deterministic).</li>
     *   <li>If no direct original sub-node matches, it tries to match against a compound node name (e.g. "A/B/C").</li>
     *   <li>If still no match is possible, an exception is thrown with a detailed message.</li>
     *   <li>No self-loop is allowed in the restored matrix (u == v is skipped/avoided).</li>
     *   <li>If {@code matrizOriginal} is weighted, restored edges are always written with weight 1.</li>
     * </ul>
     * </p>
     *
     * @param matrizOriginal original adjacency matrix (reference of truth for where edges should be attached).
     * @param matrizAExpandir matrix to expand/restore; typically the matrix returned by {@link #getMatrizFusionCiclos()}.
     * @param nodosOriginal list of original node names aligned with {@code matrizOriginal}.
     * @return restored matrix with original node indices.
     * @throws Exception if the mapping cannot be resolved for some edge.
     */
    public int[][] restaurarCiclos(int[][] matrizOriginal, int[][] matrizAExpandir, List<String> nodosOriginal) throws Exception {
        if (matrizOriginal == null || matrizAExpandir == null || nodosOriginal == null) {
            throw new IllegalArgumentException("restaurarCiclos: matrizOriginal, matrizAExpandir and nodosOriginal must be non-null.");
        }
        if (matrizOriginal.length != matrizOriginal[0].length) {
            throw new IllegalArgumentException("restaurarCiclos: matrizOriginal must be square.");
        }
        if (matrizOriginal.length != nodosOriginal.size()) {
            throw new IllegalArgumentException("restaurarCiclos: nodosOriginal size must match matrizOriginal dimension.");
        }

        int[][] matrizAdj = matrizAExpandir;

        for (int i = lOriginalNodes.size() - 1; i > 0; i--) {
            vueltaArreglos = i;
            List<String> lNodesNivel = lOriginalNodes.get(i);
            int idxCombinadoEnNivel = lNodesNivel.size() - 1;

            // Expand the (last) combined node of this level using a "faithful" restore.
            matrizAdj = expandirNodoCombinadoFiel(
                    matrizOriginal,
                    matrizAdj,
                    nodosOriginal,
                    idxCombinadoEnNivel,
                    lNodesNivel.get(idxCombinadoEnNivel));
        }

        vueltaArreglos = 0;
        return matrizAdj;
    }

    /**
     * Expands one combined node for the current {@link #vueltaArreglos} level and
     * reattaches incoming/outgoing edges to the appropriate original sub-node by
     * consulting {@code matrizOriginal}.
     */
    private int[][] expandirNodoCombinadoFiel(
            int[][] matrizOriginal,
            int[][] matrizNivel,
            List<String> nodosOriginal,
            int idxNodoCombinadoNivel,
            String nombreCombinadoNivel) throws Exception {

        // Determine sub-nodes (names) of the combined node in this level.
        String[] partes = nombreCombinadoNivel.split("/");
        if (partes.length < 2) {
            // Nothing to expand.
            return matrizNivel;
        }

        // New matrix size must match the previous level (before this combined node existed).
        List<String> nombresPrevio = lOriginalNodes.get(vueltaArreglos - 1);
        int nuevoTamano = nombresPrevio.size();
        int n = matrizNivel.length;
        int[][] nueva = new int[nuevoTamano][nuevoTamano];

        // Precompute: map each sub-node name to its original index (best effort).
        int[] subNodoOrigIdx = new int[partes.length];
        Arrays.fill(subNodoOrigIdx, -1);
        for (int p = 0; p < partes.length; p++) {
            subNodoOrigIdx[p] = encontrarIndiceNodoOriginal(nodosOriginal, partes[p]);
        }

        // Helper: resolve a node name (possibly compound) to its index in the previous level.
        // Uses exact match or any sub-name contained.
        var idxPrev = (java.util.function.Function<String, Integer>) nombre -> {
            for (int k = 0; k < nombresPrevio.size(); k++) {
                String nom = nombresPrevio.get(k);
                if (nom.equals(nombre)) {
                    return k;
                }
                for (String piece : nom.split("/")) {
                    if (piece.equals(nombre)) {
                        return k;
                    }
                }
            }
            return -1;
        };

        // 1) Copy existing edges between non-expanded nodes to their positions in previous level.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrizNivel[i][j] <= 0) {
                    continue;
                }
                if (i == idxNodoCombinadoNivel || j == idxNodoCombinadoNivel) {
                    continue; // handled below
                }

                int uPrev = idxPrev.apply(lOriginalNodes.get(vueltaArreglos).get(i));
                int vPrev = idxPrev.apply(lOriginalNodes.get(vueltaArreglos).get(j));

                if (uPrev < 0 || vPrev < 0 || uPrev == vPrev) {
                    continue; // skip unresolved or self-loop
                }
                nueva[uPrev][vPrev] = 1;
            }
        }

        // 2) Reattach incoming edges X -> COMBINED
        for (int i = 0; i < n; i++) {
            if (i == idxNodoCombinadoNivel) {
                continue;
            }
            if (matrizNivel[i][idxNodoCombinadoNivel] <= 0) {
                continue;
            }

            String nombreX = lOriginalNodes.get(vueltaArreglos).get(i);
            int xOrig = resolverIndiceOriginalDesdeNombre(nodosOriginal, nombreX);
            int xPrev = idxPrev.apply(nombreX);
            if (xPrev < 0) {
                throw new Exception("restaurarCiclos: cannot map node '" + nombreX + "' to previous level.");
            }

            int sub = elegirSubNodoParaAristaEntrante(matrizOriginal, xOrig, partes, subNodoOrigIdx, nodosOriginal);
            int yPrev = idxPrev.apply(partes[sub]);
            if (yPrev < 0) {
                throw new Exception("restaurarCiclos: cannot map sub-node '" + partes[sub] + "' to previous level.");
            }

            if (xPrev == yPrev) {
                Integer alt = elegirSubNodoAlternativoEntrante(matrizOriginal, xOrig, partes, subNodoOrigIdx, nodosOriginal, resolverIndiceOriginalDesdeNombre(nodosOriginal, partes[sub]));
                if (alt == null) {
                    throw new Exception("restaurarCiclos: self-loop detected while restoring incoming edge from '" + nombreX + "' to combined node '" + nombreCombinadoNivel + "'.");
                }
                yPrev = idxPrev.apply(partes[alt]);
            }

            if (xPrev != yPrev) {
                nueva[xPrev][yPrev] = 1;
            }
        }

        // 3) Reattach outgoing edges COMBINED -> Y
        for (int j = 0; j < n; j++) {
            if (j == idxNodoCombinadoNivel) {
                continue;
            }
            if (matrizNivel[idxNodoCombinadoNivel][j] <= 0) {
                continue;
            }

            String nombreY = lOriginalNodes.get(vueltaArreglos).get(j);
            int yOrig = resolverIndiceOriginalDesdeNombre(nodosOriginal, nombreY);
            int yPrev = idxPrev.apply(nombreY);
            if (yPrev < 0) {
                throw new Exception("restaurarCiclos: cannot map node '" + nombreY + "' to previous level.");
            }

            int sub = elegirSubNodoParaAristaSaliente(matrizOriginal, yOrig, partes, subNodoOrigIdx, nodosOriginal);
            int xPrev = idxPrev.apply(partes[sub]);
            if (xPrev < 0) {
                throw new Exception("restaurarCiclos: cannot map sub-node '" + partes[sub] + "' to previous level.");
            }

            if (xPrev == yPrev) {
                Integer alt = elegirSubNodoAlternativoSaliente(matrizOriginal, yOrig, partes, subNodoOrigIdx, nodosOriginal, resolverIndiceOriginalDesdeNombre(nodosOriginal, partes[sub]));
                if (alt == null) {
                    throw new Exception("restaurarCiclos: self-loop detected while restoring outgoing edge from combined node '" + nombreCombinadoNivel + "' to '" + nombreY + "'.");
                }
                xPrev = idxPrev.apply(partes[alt]);
            }

            if (xPrev != yPrev) {
                nueva[xPrev][yPrev] = 1;
            }
        }

        // 4) Restore cycle internal edges among the subnodes (weight 1, no self-loops).
        int prev1 = idxPrev.apply(partes[1]);
        int prev0 = idxPrev.apply(partes[0]);
        if (prev1 >= 0 && prev0 >= 0 && prev1 != prev0) {
            nueva[prev1][prev0] = 1;
        }

        int prevLast = idxPrev.apply(partes[partes.length - 1]);
        if (prev0 >= 0 && prevLast >= 0 && prev0 != prevLast) {
            nueva[prev0][prevLast] = 1;
        }

        for (int p = 1; p < partes.length - 1; p++) {
            int nodoActual = idxPrev.apply(partes[p]);
            int nodoSiguiente = idxPrev.apply(partes[p + 1]);
            if (nodoActual >= 0 && nodoSiguiente >= 0 && nodoSiguiente != nodoActual) {
                nueva[nodoSiguiente][nodoActual] = 1;
            }
        }

        return nueva;
    }

    /** Finds exact match of a node name in the original node list; returns -1 if not found. */
    private int encontrarIndiceNodoOriginal(List<String> nodosOriginal, String nombre) {
        for (int i = 0; i < nodosOriginal.size(); i++) {
            if (nombre.equals(nodosOriginal.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Resolves a node name (which may be compound "A/B/C") to an original index.
     * If it's a compound name, it resolves to the first sub-node that exists in nodosOriginal.
     */
    private int resolverIndiceOriginalDesdeNombre(List<String> nodosOriginal, String nombre) throws Exception {
        int idx = encontrarIndiceNodoOriginal(nodosOriginal, nombre);
        if (idx >= 0) {
            return idx;
        }
        String[] partes = nombre.split("/");
        for (String p : partes) {
            idx = encontrarIndiceNodoOriginal(nodosOriginal, p);
            if (idx >= 0) {
                return idx;
            }
        }
        throw new Exception("restaurarCiclos: cannot resolve node name '" + nombre + "' into nodosOriginal.");
    }

    /**
     * Picks the best sub-node index (position within 'partes') for an incoming edge X -> COMBINED.
     * The best is the smallest original index that has matrizOriginal[xOrig][subOrig] > 0.
     */
    private int elegirSubNodoParaAristaEntrante(
            int[][] matrizOriginal,
            int xOrig,
            String[] partes,
            int[] subNodoOrigIdx,
            List<String> nodosOriginal) throws Exception {

        Integer bestPos = null;
        Integer bestOrigIdx = null;

        for (int p = 0; p < partes.length; p++) {
            int subOrig = subNodoOrigIdx[p];
            if (subOrig < 0) {
                continue;
            }
            if (matrizOriginal[xOrig][subOrig] > 0) {
                if (bestOrigIdx == null || subOrig < bestOrigIdx) {
                    bestOrigIdx = subOrig;
                    bestPos = p;
                }
            }
        }
        if (bestPos != null) {
            return bestPos;
        }

        // Fallback: if we can't match directly, try matching against compound nodes in nodosOriginal.
        // If some compound contains any of the subparts, we accept the subpart with the smallest index.
        Integer posFallback = elegirSubNodoPorNodoCompuesto(partes, nodosOriginal);
        if (posFallback != null) {
            return posFallback;
        }

        throw new Exception("restaurarCiclos: no candidate sub-node found for incoming edge from original node index "
                + xOrig + " to combined node '" + String.join("/", partes) + "'.");
    }

    private Integer elegirSubNodoAlternativoEntrante(
            int[][] matrizOriginal,
            int xOrig,
            String[] partes,
            int[] subNodoOrigIdx,
            List<String> nodosOriginal,
            int forbiddenOrig) throws Exception {

        Integer bestPos = null;
        Integer bestOrigIdx = null;
        for (int p = 0; p < partes.length; p++) {
            int subOrig = subNodoOrigIdx[p];
            if (subOrig < 0 || subOrig == forbiddenOrig) {
                continue;
            }
            if (matrizOriginal[xOrig][subOrig] > 0) {
                if (bestOrigIdx == null || subOrig < bestOrigIdx) {
                    bestOrigIdx = subOrig;
                    bestPos = p;
                }
            }
        }
        if (bestPos != null) {
            return bestPos;
        }
        // As an extreme fallback, pick any other subnode (smallest original index) different from forbidden.
        for (int p = 0; p < partes.length; p++) {
            int subOrig = subNodoOrigIdx[p];
            if (subOrig >= 0 && subOrig != forbiddenOrig) {
                return p;
            }
        }
        return null;
    }

    /**
     * Picks the best sub-node index (position within 'partes') for an outgoing edge COMBINED -> Y.
     * The best is the smallest original index that has matrizOriginal[subOrig][yOrig] > 0.
     */
    private int elegirSubNodoParaAristaSaliente(
            int[][] matrizOriginal,
            int yOrig,
            String[] partes,
            int[] subNodoOrigIdx,
            List<String> nodosOriginal) throws Exception {

        Integer bestPos = null;
        Integer bestOrigIdx = null;

        for (int p = 0; p < partes.length; p++) {
            int subOrig = subNodoOrigIdx[p];
            if (subOrig < 0) {
                continue;
            }
            if (matrizOriginal[subOrig][yOrig] > 0) {
                if (bestOrigIdx == null || subOrig < bestOrigIdx) {
                    bestOrigIdx = subOrig;
                    bestPos = p;
                }
            }
        }
        if (bestPos != null) {
            return bestPos;
        }

        Integer posFallback = elegirSubNodoPorNodoCompuesto(partes, nodosOriginal);
        if (posFallback != null) {
            return posFallback;
        }

        throw new Exception("restaurarCiclos: no candidate sub-node found for outgoing edge from combined node '"
                + String.join("/", partes) + "' to original node index " + yOrig + ".");
    }

    private Integer elegirSubNodoAlternativoSaliente(
            int[][] matrizOriginal,
            int yOrig,
            String[] partes,
            int[] subNodoOrigIdx,
            List<String> nodosOriginal,
            int forbiddenOrig) throws Exception {

        Integer bestPos = null;
        Integer bestOrigIdx = null;
        for (int p = 0; p < partes.length; p++) {
            int subOrig = subNodoOrigIdx[p];
            if (subOrig < 0 || subOrig == forbiddenOrig) {
                continue;
            }
            if (matrizOriginal[subOrig][yOrig] > 0) {
                if (bestOrigIdx == null || subOrig < bestOrigIdx) {
                    bestOrigIdx = subOrig;
                    bestPos = p;
                }
            }
        }
        if (bestPos != null) {
            return bestPos;
        }
        for (int p = 0; p < partes.length; p++) {
            int subOrig = subNodoOrigIdx[p];
            if (subOrig >= 0 && subOrig != forbiddenOrig) {
                return p;
            }
        }
        return null;
    }

    /**
     * Fallback: tries to find whether the original node list contains compound nodes that include
     * any of the provided subparts. If so, returns the position of the subpart whose original index
     * (when resolved) is minimal.
     */
    private Integer elegirSubNodoPorNodoCompuesto(String[] subPartes, List<String> nodosOriginal) {
        Integer bestPos = null;
        Integer bestOrigIdx = null;
        for (int p = 0; p < subPartes.length; p++) {
            String sub = subPartes[p];
            for (int i = 0; i < nodosOriginal.size(); i++) {
                String nombre = nodosOriginal.get(i);
                if (!nombre.contains("/")) {
                    continue;
                }
                for (String piece : nombre.split("/")) {
                    if (sub.equals(piece)) {
                        if (bestOrigIdx == null || i < bestOrigIdx) {
                            bestOrigIdx = i;
                            bestPos = p;
                        }
                    }
                }
            }
        }
        return bestPos;
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

        ArregloCiclosMapeado_V1 AR = new ArregloCiclosMapeado_V1(matrix, nodes);

        int[][] matrix1 = AR.getMatrizFusionCiclos();
        List<String> nodes1 = AR.getNodes();

        System.out.println("\n-- MATRIZ SIN CICLOS --");

        TheoryToImplicationTheory.printMatrix(matrix1, nodes1);

        MatrixToLatex.toLatex(matrix1);

        DG.redibujar(matrix1, nodes1);

        int[][] matrix2;
        try {

            System.out.println("\n-- MATRIZ RECOSTRUIDA --");
            // New signature: provide original matrix + original node names
            matrix2 = AR.restaurarCiclos(matrix, matrix1, nodes);

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
