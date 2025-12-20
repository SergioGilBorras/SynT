/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI;

import java.awt.Component;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import javax.swing.JFrame;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.view.Viewer;

/**
 * Swing window used to display a GraphStream graph with different layout
 * strategies.
 */
public class VentanaGrafo extends javax.swing.JFrame {

    /**
     * GraphStream viewer used to render the graph and control the layout.
     */
    private final Viewer viewer;

    /**
     * Creates the graph window and attaches the given {@link Viewer} to the
     * scroll pane.
     *
     * @param viewer the GraphStream {@link Viewer} whose default view will be
     *               embedded in this frame
     */
    public VentanaGrafo(Viewer viewer) {
        this.viewer = viewer;
        initComponents();
        jScrollPane1.setViewportView((Component) viewer.getDefaultView());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        // Enable the default automatic layout when the window is created
        viewer.enableAutoLayout();
        // viewer.getGraphicGraph().display();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Model graph");

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));
        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 29));
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 29));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LinLog", "Circle", "Grid", "Random", "Radial", "SpringBox" }));
        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(306, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Reacts to changes in the layout selection combo box and updates the graph
     * layout accordingly.
     *
     * @param evt the Swing action event fired when the combo box selection
     *            changes
     */
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        Object selected = jComboBox1.getModel().getSelectedItem();
        if ("LinLog".equals(selected)) {
            viewer.disableAutoLayout();
            viewer.enableAutoLayout(new LinLog());
        } else if ("SpringBox".equals(selected)) {
            viewer.disableAutoLayout();
            // viewer.enableAutoLayout(new SpringBox());
        } else if ("Random".equals(selected)) {
            viewer.disableAutoLayout();
            for (Node node : viewer.getGraphicGraph()) {
                double x = Math.random() * this.getWidth();
                double y = Math.random() * this.getHeight();
                node.setAttribute("xyz", x, y, 0);
            }
        } else if ("Circle".equals(selected)) {
            int radius = 150;

            viewer.disableAutoLayout();
            int n = viewer.getGraphicGraph().getNodeCount();
            double angleStep = 2 * Math.PI / n;

            int i = 0;
            for (Node node : viewer.getGraphicGraph()) {
                double x = radius * Math.cos(i * angleStep);
                double y = radius * Math.sin(i * angleStep);
                node.setAttribute("xyz", x, y, 0);
                i++;
            }
        } else if ("Radial".equals(selected)) {
            int radiusStep = 35;

            viewer.disableAutoLayout();

            Node root = getMostConnectedNode();
            // Place the root in the center of the graph
            root.setAttribute("xyz", 0, 0, 0);

            HashMap<Integer, Integer> depthCount = new HashMap<>();
            for (Node node : viewer.getGraphicGraph()) {
                int depth = getDepth(viewer.getGraphicGraph(), root, node);
                depthCount.merge(depth, 1, Integer::sum);
            }
            HashMap<Integer, Integer> depthIndex = new HashMap<>();
            for (Node node : viewer.getGraphicGraph()) {
                int depth = getDepth(viewer.getGraphicGraph(), root, node);
                depthIndex.merge(depth, 1, Integer::sum);
                double angle = 2 * Math.PI / depthCount.get(depth);
                double radius = depth * radiusStep;
                node.setAttribute("xyz", radius * Math.cos(angle * depthIndex.get(depth)), radius * Math.sin(angle * depthIndex.get(depth)), 0);
            }
        } else if ("Grid".equals(selected)) {
            viewer.disableAutoLayout();
            int n = viewer.getGraphicGraph().getNodeCount();
            int cols = (int) Math.ceil(Math.sqrt(n));
            int rows = (int) Math.ceil((double) n / cols);
            double spacingX = (double) this.getWidth() / (cols + 1);
            double spacingY = (double) this.getHeight() / (rows + 1);

            int i = 0;
            for (Node node : viewer.getGraphicGraph()) {
                int col = i % cols;
                int row = i / cols;
                node.setAttribute("xyz", col * spacingX, -row * spacingY, 0);
                i++;
            }

        }

    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * Returns the node with the highest degree (most incident edges) in the
     * current graph.
     *
     * @return the most connected {@link Node}, or {@code null} if the graph is
     *         empty
     */
    private Node getMostConnectedNode() {
        Node maxNode = null;
        int maxDegree = -1;

        for (Node node : viewer.getGraphicGraph()) {
            int degree = node.getDegree();
            if (degree > maxDegree) {
                maxDegree = degree;
                maxNode = node;
            }
        }
        return maxNode;
    }

    /**
     * Computes the breadth-first search (BFS) distance between a root node and
     * a target node.
     *
     * @param graph  the graph in which the search is performed
     * @param root   the starting node
     * @param target the node whose distance from {@code root} is requested
     * @return the depth (number of edges) between {@code root} and
     *         {@code target}, or {@code -1} if the target is not reachable
     */
    public static int getDepth(Graph graph, Node root, Node target) {
        Queue<Node> queue = new LinkedList<>();
        Map<Node, Integer> depthMap = new HashMap<>();

        queue.add(root);
        depthMap.put(root, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int currentDepth = depthMap.get(current);

            if (current == target) {
                return currentDepth;
            }
            for (Edge edge : current) {
                Node neighbor = edge.getOpposite(current);
                if (!depthMap.containsKey(neighbor)) {
                    queue.add(neighbor);
                    depthMap.put(neighbor, currentDepth + 1);
                }
            }
        }
        return -1;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    /**
     * Combo box used to select the layout algorithm applied to the graph.
     */
    private javax.swing.JComboBox<String> jComboBox1;
    /**
     * Top control panel containing the layout selection combo box.
     */
    private javax.swing.JPanel jPanel1;
    /**
     * Scroll pane that hosts the GraphStream view component.
     */
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
