package GUI;

import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 * Swing window that displays a matrix-based model as a table.
 * <p>
 * The first column contains the node labels and the remaining columns contain
 * the matrix values. The window configures column widths, header tooltips and
 * selection behavior (by column, by row or by single cell).
 * </p>
 */
public class VentanaTabla extends javax.swing.JFrame {

    /**
     * Matrix that represents the model to be shown in the table.
     */
    private final int[][] matriz;
    /**
     * List of node labels corresponding to the matrix indices.
     */
    private final List<String> nodos;
    /**
     * Backing model for the Swing {@link javax.swing.JTable}.
     */
    private DefaultTableModel tablaModel;
    /**
     * Length of the widest header label, used to size the first column.
     */
    private int anchoHeader = 0;
    /**
     * Text of the widest header label.
     */
    private String maxAncho;

    /**
     * Creates a new table window for the given matrix and node list.
     * <p>
     * The constructor builds the table model, initializes the UI components
     * and configures column widths, cell renderers and selection behavior.
     * </p>
     *
     * @param matriz the matrix representing the model
     * @param nodos  the list of node labels, one per row/column in the matrix
     */
    public VentanaTabla(int[][] matriz, List<String> nodos) {
        this.matriz = matriz;
        this.nodos = nodos;
        loadTabla();
        initComponents();
        setVisible(true);

        // Use the table header renderer for the first column so labels look like headers
        TableColumn firstColumn = jTable1.getColumnModel().getColumn(0);
        firstColumn.setCellRenderer(jTable1.getTableHeader().getDefaultRenderer());

        // Compute minimum width for the first column based on the longest header text
        int width = jTable1.getFontMetrics(jTable1.getFont()).stringWidth(maxAncho) + 10;
        firstColumn.setMinWidth(width);

        // Compute a dynamic window size based on the table content
        int totalHeight = jTable1.getRowHeight() * jTable1.getRowCount()
                + jTable1.getTableHeader().getPreferredSize().height + 50;
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int totalWidth = Math.max(jTable1.getPreferredSize().width + 50, screenWidth);

        // Center-align all matrix value columns (all except the first one)
        DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
        tableCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(tableCellRenderer);
        }

        // Make the table read-only
        jTable1.setDefaultEditor(Object.class, null);

        // Show full column header text as a tooltip when hovering over it
        JTableHeader header = jTable1.getTableHeader();
        header.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = header.columnAtPoint(e.getPoint());
                if (col > 0) {
                    header.setToolTipText(jTable1.getColumnName(col));
                }
            }
        });

        // When clicking on a header, select the whole column
        jTable1.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTable1.columnAtPoint(e.getPoint());
                if (col > -1) {
                    jTable1.setColumnSelectionAllowed(true);
                    jTable1.setRowSelectionAllowed(false);
                    jTable1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                    jTable1.setColumnSelectionInterval(col, col);
                }
            }
        });

        // Custom click behavior:
        //  - clicking the first column selects the whole row
        //  - clicking any other column selects a single cell
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = jTable1.rowAtPoint(e.getPoint());
                int col = jTable1.columnAtPoint(e.getPoint());
                jTable1.setRowSelectionAllowed(true);
                if (col == 0) {
                    jTable1.setColumnSelectionAllowed(false);
                    jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    jTable1.setRowSelectionInterval(row, row);
                } else {
                    jTable1.setColumnSelectionAllowed(true);
                    jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    jTable1.setCellSelectionEnabled(true);
                }
            }
        });

        setSize(totalWidth, totalHeight);
    }

    /**
     * Builds the table model from the internal matrix and node list.
     * <p>
     * The first column is filled with node labels, and each subsequent column
     * with the corresponding matrix row values. The header is built from the
     * node list as well, and the widest header text is tracked for sizing.
     * </p>
     */
    private void loadTabla() {
        int rows = matriz.length;
        int cols = matriz[0].length + 1;
        String[][] tableData = new String[rows][cols];

        // Fill first column with node labels and the rest with matrix values
        for (int i = 0; i < rows; i++) {
            tableData[i][0] = nodos.get(i);
            for (int j = 0; j < matriz[i].length; j++) {
                tableData[i][j + 1] = String.valueOf(matriz[i][j]);
            }
        }

        // Build header from the node list
        String[] header = new String[cols];
        header[0] = "";
        for (int i = 1; i < cols; i++) {
            header[i] = nodos.get(i - 1);
            if (anchoHeader < header[i].length()) {
                anchoHeader = header[i].length();
                maxAncho = header[i];
            }
        }
        tablaModel = new DefaultTableModel(tableData, header);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Model table");
        setAlwaysOnTop(true);
        setType(java.awt.Window.Type.POPUP);

        jTable1.setModel(tablaModel);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    /**
     * Scroll pane that contains the table showing the model matrix.
     */
    private javax.swing.JScrollPane jScrollPane1;
    /**
     * JTable used to display the model matrix and its node labels.
     */
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
