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
public class ArregloCiclosMapeado_V2 extends ArregloCiclos {

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

    // Divide un nombre combinado por '/' solo a nivel superior (fuera de corchetes).
    private String[] dividirNombreCompuesto(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return new String[]{nombre};
        }
        List<String> partes = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < nombre.length(); i++) {
            char c = nombre.charAt(i);
            if (c == '[') {
                depth++;
            } else if (c == ']') {
                depth = Math.max(0, depth - 1);
            }
            if (c == '/' && depth == 0) {
                partes.add(actual.toString());
                actual.setLength(0);
                continue;
            }
            actual.append(c);
        }
        partes.add(actual.toString());
        if (partes.size() == 1) {
            return new String[]{nombre};
        }
        return partes.toArray(new String[0]);
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
        String[] partesNodo = dividirNombreCompuesto(lOriginalNodes.get(vueltaArreglos).get(nuevoNodo));
        String nodoBuscado = partesNodo[posicion];

//        System.out.println("nodoBuscado:: " + nodoBuscado);
        for (int i = 0; i < nodosIniciales.size(); i++) {
            // System.out.println("nodosIniciales:: " + nodosIniciales.get(i));
            String[] subNodos = dividirNombreCompuesto(nodosIniciales.get(i));
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
        throw new Exception("Error [ArregloCiclosSinMapedado_V2.realacionNodos()] :: Nodo no encontrado.");
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
    public ArregloCiclosMapeado_V2(int[][] matrizAdj, List<String> nodes) {
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
            int idxCombinadoEnNivel = Math.min(lNodesNivel.size() - 1, matrizAdj.length - 1);

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
        String[] partes = dividirNombreCompuesto(nombreCombinadoNivel);
        if (partes.length < 2) {
            // Nothing to expand.
            return matrizNivel;
        }

        // New matrix size must match the previous level (before this combined node existed).
        List<String> nombresPrevio = lOriginalNodes.get(vueltaArreglos - 1);
        int nuevoTamano = nombresPrevio.size();
        int n = matrizNivel.length;
        int[][] nueva = new int[nuevoTamano][nuevoTamano];

        // Lazily resolved mapping of each sub-node name to its original index.
        int[] subNodoOrigIdx = new int[partes.length];
        Arrays.fill(subNodoOrigIdx, -1);

        // 1) Copy existing edges between non-expanded nodes to their positions in previous level.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrizNivel[i][j] <= 0) {
                    continue;
                }
                if (i == idxNodoCombinadoNivel || j == idxNodoCombinadoNivel) {
                    continue; // handled below
                }

                int uPrev = resolverIndiceNivelAnterior(nombresPrevio, lOriginalNodes.get(vueltaArreglos).get(i));
                int vPrev = resolverIndiceNivelAnterior(nombresPrevio, lOriginalNodes.get(vueltaArreglos).get(j));
                if (uPrev == vPrev) {
                    continue; // avoid self-loop
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
            int xPrev = resolverIndiceNivelAnterior(nombresPrevio, nombreX);

            int sub = elegirSubNodoParaAristaEntrante(matrizOriginal, xOrig, partes, subNodoOrigIdx, nodosOriginal);
            int yPrev = resolverIndiceNivelAnterior(nombresPrevio, partes[sub]);

            if (xPrev == yPrev) {
                int forbiddenOrig = resolverIndiceOriginalSubNodo(partes[sub], subNodoOrigIdx, sub, nodosOriginal);
                Integer alt = elegirSubNodoAlternativoEntrante(matrizOriginal, xOrig, partes, subNodoOrigIdx, nodosOriginal, forbiddenOrig);
                if (alt == null) {
                    throw new Exception("restaurarCiclos: self-loop detected while restoring incoming edge from '" + nombreX + "' to combined node '" + nombreCombinadoNivel + "'.");
                }
                yPrev = resolverIndiceNivelAnterior(nombresPrevio, partes[alt]);
                if (xPrev == yPrev) {
                    throw new Exception("restaurarCiclos: unable to avoid self-loop for incoming edge from '" + nombreX + "' to '" + partes[alt] + "'.");
                }
            }

            nueva[xPrev][yPrev] = 1;
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
            int yPrev = resolverIndiceNivelAnterior(nombresPrevio, nombreY);

            int sub = elegirSubNodoParaAristaSaliente(matrizOriginal, yOrig, partes, subNodoOrigIdx, nodosOriginal);
            int xPrev = resolverIndiceNivelAnterior(nombresPrevio, partes[sub]);

            if (xPrev == yPrev) {
                int forbiddenOrig = resolverIndiceOriginalSubNodo(partes[sub], subNodoOrigIdx, sub, nodosOriginal);
                Integer alt = elegirSubNodoAlternativoSaliente(matrizOriginal, yOrig, partes, subNodoOrigIdx, nodosOriginal, forbiddenOrig);
                if (alt == null) {
                    throw new Exception("restaurarCiclos: self-loop detected while restoring outgoing edge from combined node '" + nombreCombinadoNivel + "' to '" + nombreY + "'.");
                }
                xPrev = resolverIndiceNivelAnterior(nombresPrevio, partes[alt]);
                if (xPrev == yPrev) {
                    throw new Exception("restaurarCiclos: unable to avoid self-loop for outgoing edge to '" + nombreY + "' using sub-node '" + partes[alt] + "'.");
                }
            }

            nueva[xPrev][yPrev] = 1;
        }

        // 4) Restore internal edges among the sub-nodes according to the original matrix (weight 1, no self-loops).
        restaurarAristasInternas(matrizOriginal, partes, subNodoOrigIdx, nodosOriginal, nombresPrevio, nueva);

        return nueva;
    }

    private int resolverIndiceNivelAnterior(List<String> nombresPrevio, String nombre) throws Exception {
        for (int k = 0; k < nombresPrevio.size(); k++) {
            String nom = nombresPrevio.get(k);
            if (nom.equals(nombre)) {
                return k;
            }
            for (String piece : dividirNombreCompuesto(nom)) {
                if (piece.equals(nombre)) {
                    return k;
                }
            }
        }
        throw new Exception("restaurarCiclos: cannot map node '" + nombre + "' to previous level.");
    }

    private void restaurarAristasInternas(
            int[][] matrizOriginal,
            String[] partes,
            int[] subNodoOrigIdx,
            List<String> nodosOriginal,
            List<String> nombresPrevio,
            int[][] nueva) throws Exception {

        int m = partes.length;
        int[] origIdx = new int[m];
        int[] prevIdx = new int[m];
        for (int p = 0; p < m; p++) {
            origIdx[p] = resolverIndiceOriginalSubNodo(partes[p], subNodoOrigIdx, p, nodosOriginal);
            prevIdx[p] = resolverIndiceNivelAnterior(nombresPrevio, partes[p]);
        }

        boolean[][] subAdj = new boolean[m][m];
        List<int[]> edges = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if (i == j) {
                    continue; // sin autoaristas
                }
                if (matrizOriginal[origIdx[i]][origIdx[j]] > 0) {
                    subAdj[i][j] = true;
                    edges.add(new int[]{i, j});
                }
            }
        }

        edges.sort(Comparator.<int[]>comparingInt(e -> e[0]).thenComparingInt(e -> e[1]));
        for (int[] e : edges) {
            int u = e[0];
            int v = e[1];
            if (!subAdj[u][v]) {
                continue;
            }
            subAdj[u][v] = false; // intento quitar la arista
            boolean sccOk = esFuertementeConexo(subAdj);
            //boolean hamOk = existeCicloHamiltoniano(subAdj);
            //System.out.println("ARISTA: " + partes[u] + " --> " + partes[v]
            //        + " | SCC=" + sccOk + " | Hamiltoniano=" + hamOk);
            if (!sccOk){// || !hamOk) {
                subAdj[u][v] = true; // necesaria para mantener SCC
            }
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if (!subAdj[i][j] || prevIdx[i] == prevIdx[j]) {
                    continue;
                }
                nueva[prevIdx[i]][prevIdx[j]] = 1;
            }
        }
    }

    // Comprueba si existe un ciclo hamiltoniano (pasa por todos los nodos y vuelve al inicio)
    private boolean existeCicloHamiltoniano(boolean[][] adj) {
        int n = adj.length;
        if (n == 0) {
            return true;
        }
        for (int start = 0; start < n; start++) {
            boolean[] visit = new boolean[n];
            visit[start] = true;
            if (dfsHamiltoniano(start, start, adj, visit, 1)) {
                return true;
            }
        }
        return false;
    }

    private boolean dfsHamiltoniano(int actual, int inicio, boolean[][] adj, boolean[] visit, int usados) {
        int n = adj.length;
        if (usados == n) {
            return adj[actual][inicio];
        }
        for (int sig = 0; sig < n; sig++) {
            if (visit[sig]) {
                continue;
            }
            if (!adj[actual][sig]) {
                continue;
            }
            visit[sig] = true;
            if (dfsHamiltoniano(sig, inicio, adj, visit, usados + 1)) {
                return true;
            }
            visit[sig] = false;
        }
        return false;
    }

    // --- Helpers de mapeo a nodos originales y selección de subnodos ---
    private int encontrarIndiceNodoOriginal(List<String> nodosOriginal, String nombre) {
        for (int i = 0; i < nodosOriginal.size(); i++) {
            if (nombre.equals(nodosOriginal.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private int resolverIndiceOriginalDesdeNombre(List<String> nodosOriginal, String nombre) throws Exception {
        int idx = encontrarIndiceNodoOriginal(nodosOriginal, nombre);
        if (idx >= 0) {
            return idx;
        }
        for (String p : dividirNombreCompuesto(nombre)) {
            idx = encontrarIndiceNodoOriginal(nodosOriginal, p);
            if (idx >= 0) {
                return idx;
            }
        }
        for (int i = 0; i < nodosOriginal.size(); i++) {
            String nom = nodosOriginal.get(i);
            if (!nom.contains("/")) {
                continue;
            }
            for (String piece : dividirNombreCompuesto(nom)) {
                if (piece.equals(nombre)) {
                    return i;
                }
            }
        }
        throw new Exception("restaurarCiclos: cannot resolve node name '" + nombre + "' into nodosOriginal. ");
    }

    private int resolverIndiceOriginalSubNodo(String nombreSubNodo, int[] subNodoOrigIdx, int pos, List<String> nodosOriginal) throws Exception {
        if (subNodoOrigIdx[pos] >= 0) {
            return subNodoOrigIdx[pos];
        }
        int idx = resolverIndiceOriginalDesdeNombre(nodosOriginal, nombreSubNodo);
        subNodoOrigIdx[pos] = idx;
        return idx;
    }

    private int elegirSubNodoParaAristaEntrante(
            int[][] matrizOriginal,
            int xOrig,
            String[] partes,
            int[] subNodoOrigIdx,
            List<String> nodosOriginal) throws Exception {

        Integer bestPos = null;
        Integer bestOrigIdx = null;
        for (int p = 0; p < partes.length; p++) {
            int subOrig = resolverIndiceOriginalSubNodo(partes[p], subNodoOrigIdx, p, nodosOriginal);
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
            int subOrig = resolverIndiceOriginalSubNodo(partes[p], subNodoOrigIdx, p, nodosOriginal);
            if (subOrig == forbiddenOrig) {
                continue;
            }
            if (matrizOriginal[xOrig][subOrig] > 0) {
                if (bestOrigIdx == null || subOrig < bestOrigIdx) {
                    bestOrigIdx = subOrig;
                    bestPos = p;
                }
            }
        }
        return bestPos;
    }

    private int elegirSubNodoParaAristaSaliente(
            int[][] matrizOriginal,
            int yOrig,
            String[] partes,
            int[] subNodoOrigIdx,
            List<String> nodosOriginal) throws Exception {

        Integer bestPos = null;
        Integer bestOrigIdx = null;
        for (int p = 0; p < partes.length; p++) {
            int subOrig = resolverIndiceOriginalSubNodo(partes[p], subNodoOrigIdx, p, nodosOriginal);
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
            int subOrig = resolverIndiceOriginalSubNodo(partes[p], subNodoOrigIdx, p, nodosOriginal);
            if (subOrig == forbiddenOrig) {
                continue;
            }
            if (matrizOriginal[subOrig][yOrig] > 0) {
                if (bestOrigIdx == null || subOrig < bestOrigIdx) {
                    bestOrigIdx = subOrig;
                    bestPos = p;
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

        ArregloCiclosMapeado_V2 AR = new ArregloCiclosMapeado_V2(matrix, nodes);

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

    private boolean esFuertementeConexo(boolean[][] adj) {
        int n = adj.length;
        if (n == 0) {
            return true;
        }
        boolean[] vis = new boolean[n];
        dfsDir(0, adj, vis);
        for (boolean v : vis) {
            if (!v) {
                return false;
            }
        }
        boolean[][] rev = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                rev[j][i] = adj[i][j];
            }
        }
        Arrays.fill(vis, false);
        dfsDir(0, rev, vis);
        for (boolean v : vis) {
            if (!v) {
                return false;
            }
        }
        return true;
    }

    private void dfsDir(int u, boolean[][] adj, boolean[] vis) {
        if (vis[u]) {
            return;
        }
        vis[u] = true;
        for (int v = 0; v < adj.length; v++) {
            if (adj[u][v]) {
                dfsDir(v, adj, vis);
            }
        }
    }
}
