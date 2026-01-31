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
 * new matrix and a new list of node names. Concrete implementations typically
 * keep this level history (matrices and node lists) to support the restore
 * step.
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
public abstract class ArregloCiclos {

    /**
     * Returns the current list of node labels for the working graph.
     * <p>
     * Implementations typically return the node list corresponding to the most
     * recent matrix produced by the cycle-contraction process.
     * </p>
     *
     * @return the list of node labels (index-aligned with the current adjacency matrix)
     */
    public abstract List<String> getNodes();

    /**
     * Returns the adjacency matrix after contracting (merging) cycles.
     * <p>
     * The returned matrix is expected to represent the graph after one or
     * multiple SCC contractions.
     * </p>
     *
     * @return the contracted adjacency matrix
     */
    public abstract int[][] getMatrizFusionCiclos();

    /**
     * Restores (expands) the cycles previously contracted.
     * <p>
     * Implementations use {@code matrizOriginal} and {@code nodosOriginal} as
     * the ground truth to decide where edges must be reattached when expanding
     * combined nodes (e.g. {@code "A/B/C"}). The matrix {@code matrizAExpandir}
     * is the current working matrix that still contains combined nodes and will
     * be expanded.
     * </p>
     *
     * @param matrizOriginal reference/original adjacency matrix used as truth
     *                       for mapping/restoring edges
     * @param matrizAExpandir adjacency matrix to expand/restore (may contain
     *                        combined nodes)
     * @param nodosOriginal list of original node labels aligned with
     *                      {@code matrizOriginal}
     * @return a new adjacency matrix where contracted cycles have been restored
     * @throws Exception if the restore process cannot be completed consistently
     */
    public abstract int[][] restaurarCiclos(int[][] matrizOriginal, int[][] matrizAExpandir, List<String> nodosOriginal) throws Exception;

}
