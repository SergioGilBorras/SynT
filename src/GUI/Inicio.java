/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI;

import GUI.Componets.CheckComboItem;
import GUI.Componets.CheckComboRenderer;
import models.*;
import org.graphstream.ui.view.Viewer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import theorybuildingse.dibujaGrafos;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static GUI.utils.caminoMinimo;
import static GUI.utils.dividirAristaEnNodos;
import static theorybuildingse.MatrixToLatex.toLatex;

/**
 * Main Swing frame for SynT, a tool for automating the theory synthesis
 * process.
 * <p>
 * This window orchestrates the definition of constructs, universes, variables
 * and implications, and provides several views for exploring the generated
 * theory model (table, LaTeX matrix, graph and CSV export).
 * </p>
 */
public class Inicio extends javax.swing.JFrame {

    /**
     * Collection of all constructs currently defined in the session.
     */
    ColElements<Construct> colConstruct = new ColElements<>();

    /**
     * Collection of all functions currently defined in the session.
     */
    ColElements<Function> colFunction = new ColElements<>();

    /**
     * Collection of all universes currently defined in the session.
     */
    ColElements<Universe> colUniverse = new ColElements<>();

    /**
     * Collection of all variables currently defined in the session.
     */
    ColElements<Variable> colVariable = new ColElements<>();

    /**
     * Collection of all Implications currently defined in the session.
     */
    ColElements<Implication> colImplication = new ColElements<>();

    /**
     * List model for the first literal of each implication.
     */
    DefaultListModel<String> listModelLiterales1 = new DefaultListModel<>();
    /**
     * List model for the second literal of each implication.
     */
    DefaultListModel<String> listModelLiterales2 = new DefaultListModel<>();
    /**
     * List model storing the negation flag for the first literal of each
     * implication.
     */
    DefaultListModel<String> listModelImplicacionesNot1 = new DefaultListModel<>();
    /**
     * List model storing the negation flag for the second literal of each
     * implication.
     */
    DefaultListModel<String> listModelImplicacionesNot2 = new DefaultListModel<>();
    /**
     * Human-readable list of implications, as shown in the Implications tab.
     */
    DefaultListModel<String> listModelImplicacionesList = new DefaultListModel<>();
    /**
     * Human-readable list of implications in contrapositive form (used for
     * cycle reduction).
     */
    DefaultListModel<String> listModelImplicacionesListCR = new DefaultListModel<>();

    /**
     * Human-readable list of implications used in Generation Tab).
     */
    DefaultListModel<String> listModelGeneration = new DefaultListModel<>();

    /**
     * Human-readable list of implications in contrapositive form used in
     * Generation Tab).
     */
    DefaultListModel<String> listModelGenerationCR = new DefaultListModel<>();

    /**
     * Combo box model containing all variable aliases used as literal 1.
     */
    DefaultComboBoxModel<String> CombolistModelAlias1 = new DefaultComboBoxModel<>();
    /**
     * Combo box model containing all variable aliases used as literal 2.
     */
    DefaultComboBoxModel<String> CombolistModelAlias2 = new DefaultComboBoxModel<>();

    /**
     * Combo box model with the relations allowed for the universe of literal 1.
     */
    DefaultComboBoxModel<String> CombolistModelRelations1 = new DefaultComboBoxModel<>();
    /**
     * Combo box model with the relations allowed for the universe of literal 2.
     */
    DefaultComboBoxModel<String> CombolistModelRelations2 = new DefaultComboBoxModel<>();
    /**
     * Combo box model with the values allowed for the universe of literal 1.
     */
    DefaultComboBoxModel<String> CombolistModelValues1 = new DefaultComboBoxModel<>();
    /**
     * Combo box model with the values allowed for the universe of literal 2.
     */
    DefaultComboBoxModel<String> CombolistModelValues2 = new DefaultComboBoxModel<>();

    /**
     * Combo box model with all construct names, used when defining variables.
     */
    DefaultComboBoxModel<String> CombolistModelConstructs = new DefaultComboBoxModel<>();
    /**
     * Combo box model with all universe names, used when defining variables.
     */
    DefaultComboBoxModel<String> CombolistModelUniverses = new DefaultComboBoxModel<>();
    /**
     * Combo box model with all functions, used when defining universes.
     */
    DefaultComboBoxModel<CheckComboItem> CombolistModelFunctions = new DefaultComboBoxModel<>();

    /**
     * Currently selected index in the Constructs list.
     * <p>
     * A value of {@code -1} indicates that nothing is selected.
     * </p>
     */
    int selectedIndexConstruct = -1;

    /**
     * Currently selected index in the Function list.
     * <p>
     * A value of {@code -1} indicates that nothing is selected.
     * </p>
     */
    int selectedIndexFunctions = -1;

    /**
     * Currently selected index in the Variables list.
     * <p>
     * A value of {@code -1} indicates that nothing is selected.
     * </p>
     */
    int selectedIndexVariable = -1;

    /**
     * Currently selected index in the Universes list.
     * <p>
     * A value of {@code -1} indicates that nothing is selected.
     * </p>
     */
    int selectedIndexUniverse = -1;

    /**
     * Currently selected index in the Implications list.
     * <p>
     * A value of {@code -1} indicates that nothing is selected.
     * </p>
     */
    int selectedIndexImplication = -1;

    /**
     * Creates the main SynT frame and initializes all Swing components.
     * <p>
     * The constructor initializes the GUI components generated by the form
     * editor, disables window resizing and synchronizes the scroll bars in the
     * Implications tab so that the two lists scroll together.
     * </p>
     */
    public Inicio() {
        initComponents();
        this.setResizable(false);
        sincronizarScrollImplicaciones();
        sincronizarJListImplicaciones();
        initComboBoxCheckItem();
    }

    /**
     * Checks whether a {@link DefaultListModel} contains a given string.
     * <p>
     * {@link DefaultListModel} does not provide a {@code contains(...)} method,
     * so we scan it linearly.
     * </p>
     *
     * @param model the list model to scan
     * @param valor the value to search for
     * @return {@code true} if the model contains {@code valor}; {@code false} otherwise
     */
    private static boolean defaultListModelContain(DefaultListModel<String> model, String valor) {
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i).equals(valor)) {
                //System.out.println("ENTRE::::::::");
                return true;
            }
        }
        return false;
    }

    /**
     * Concatenates two {@link DefaultListModel} instances into a new model.
     * <p>
     * The returned model preserves the order of the original elements:
     * all elements from {@code a} first, followed by all elements from {@code b}.
     * </p>
     *
     * @param a first list model
     * @param b second list model
     * @return a new {@link DefaultListModel} containing {@code a} + {@code b}
     */
    public static DefaultListModel<String> defaultListModelSum(
            DefaultListModel<String> a,
            DefaultListModel<String> b) {

        DefaultListModel<String> resultado = new DefaultListModel<>();

        for (int i = 0; i < a.size(); i++) {
            resultado.addElement(a.get(i));
        }

        for (int i = 0; i < b.size(); i++) {
            resultado.addElement(b.get(i));
        }

        return resultado;
    }

    /**
     * Sorts a {@link DefaultComboBoxModel} of strings in-place (case-insensitive).
     * <p>
     * The model is copied into a temporary list, sorted using
     * {@link String#compareToIgnoreCase(String)}, then the combo model is cleared
     * and repopulated with the sorted items.
     * </p>
     *
     * @param CombolistModel combo box model to sort
     */
    public static void ordenarDefaultComboBoxModel(DefaultComboBoxModel<String> CombolistModel) {

        List<String> lista = new ArrayList<>();
        for (int i = 0; i < CombolistModel.getSize(); i++) {
            lista.add(CombolistModel.getElementAt(i));
        }

        lista.sort(String::compareToIgnoreCase);

        CombolistModel.removeAllElements();
        lista.forEach(CombolistModel::addElement);
    }

    /**
     * Application entry point.
     * <p>
     * Sets the Nimbus look and feel when available and shows the main SynT
     * frame on the Event Dispatch Thread.
     * </p>
     *
     * @param args command line arguments (not used)
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Inicio.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Inicio().setVisible(true));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jTextField6 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        jLabel17 = new javax.swing.JLabel();
        jButton23 = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jTextField16 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList<>();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jTextField9 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList<>();
        jLabel21 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jTextField12 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jTextField13 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        jButton24 = new javax.swing.JButton();
        jComboBox8 = new JComboBox<CheckComboItem>();
        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        jComboBox7 = new javax.swing.JComboBox<>();
        jButton25 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList<>();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jComboBox10 = new javax.swing.JComboBox<>();
        jPanel13 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jComboBox11 = new JComboBox<CheckComboItem>();
        jPanel21 = new javax.swing.JPanel();
        jComboBox15 = new javax.swing.JComboBox<>();
        jPanel18 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jComboBox12 = new javax.swing.JComboBox<>();
        jPanel17 = new javax.swing.JPanel();
        jTextField15 = new javax.swing.JTextField();
        jPanel19 = new javax.swing.JPanel();
        jComboBox13 = new JComboBox<CheckComboItem>();
        jPanel20 = new javax.swing.JPanel();
        jComboBox14 = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jButton26 = new javax.swing.JButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jRadioButton11 = new javax.swing.JRadioButton();
        jRadioButton12 = new javax.swing.JRadioButton();
        jRadioButton13 = new javax.swing.JRadioButton();
        jCheckBox9 = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        jList7 = new javax.swing.JList<>();
        jScrollPane10 = new javax.swing.JScrollPane();
        jList8 = new javax.swing.JList<>();
        jRadioButton14 = new javax.swing.JRadioButton();
        jComboBox9 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel29 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SynT - A software tool for automating the theory synthesis process");

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jLabel14.setText("Name");

        jLabel15.setText("From (Separated by ';')");

        jLabel16.setText("Scope-conditions");

        jButton13.setText("Add");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("Del");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setText("<");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setText(">");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setText("Clear list");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jList3.setModel(colConstruct.getModel());
        jList3.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(jList3);

        jLabel17.setBackground(new java.awt.Color(222, 222, 222));
        jLabel17.setForeground(new java.awt.Color(255, 51, 102));

        jButton23.setText("Mode edit");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jButton15)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jTextField8)
                        .addComponent(jTextField7)
                        .addComponent(jTextField6)
                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton13)
                            .addComponent(jButton14)
                            .addComponent(jButton23))
                        .addGap(152, 152, 152)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton15)
                            .addComponent(jButton16)
                            .addComponent(jButton17))))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Constructs", jPanel4);

        jLabel33.setText("Function name");

        jButton27.setText("Add");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jButton28.setText("Del");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jButton29.setText("Mode edit");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jButton30.setText("<");
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jButton31.setText(">");
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jButton32.setText("Clear list");
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jList6.setModel(colFunction.getModel());
        jList6.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane8.setViewportView(jList6);

        jLabel34.setText("Arity");

        jLabel35.setBackground(new java.awt.Color(222, 222, 222));
        jLabel35.setForeground(new java.awt.Color(255, 51, 102));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jButton27, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jButton30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton27)
                            .addComponent(jButton28)
                            .addComponent(jButton29))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton30)
                            .addComponent(jButton31)
                            .addComponent(jButton32))))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Functions", jPanel15);

        jLabel18.setText("Name");

        jLabel19.setText("Type");

        jButton18.setText("Add");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setText("Del");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setText("<");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setText(">");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton22.setText("Clear list");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jList4.setModel(colUniverse.getModel());
        jList4.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(jList4);

        jLabel21.setBackground(new java.awt.Color(222, 222, 222));
        jLabel21.setForeground(new java.awt.Color(255, 51, 102));

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enum (Scalar)", "Enum (Collection)", "Real", "Bool" }));
        jComboBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox5ActionPerformed(evt);
            }
        });

        jLabel25.setText("Functions");

        jPanel7.setLayout(new java.awt.CardLayout());

        jLabel22.setText("Value (Separated by ';')");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField12)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 142, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel8, "card_Enum");

        jLabel23.setText("Min value");

        jLabel24.setText("Max value");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel24))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel6, "card_Real");

        jLabel27.setText("Selection (True, False)");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel9, "card_Bool");

        jLabel26.setText("Relation");

        jCheckBox3.setSelected(true);
        jCheckBox3.setText("E (=)");
        jCheckBox3.setActionCommand("");

        jCheckBox4.setText("G (>)");
        jCheckBox4.setActionCommand("");

        jCheckBox5.setText("GE (>=)");
        jCheckBox5.setActionCommand("");

        jCheckBox6.setText("NE (!=)");
        jCheckBox6.setActionCommand("");

        jCheckBox7.setText("L (<)");
        jCheckBox7.setActionCommand("");

        jCheckBox8.setText("LE (<=)");
        jCheckBox8.setActionCommand("");

        jButton24.setText("Mode edit");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jComboBox8.setModel(CombolistModelFunctions);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19)
                            .addComponent(jLabel25)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jCheckBox6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jCheckBox7))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jCheckBox3)
                                        .addGap(24, 24, 24)
                                        .addComponent(jCheckBox4)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBox5)
                                    .addComponent(jCheckBox8)))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(26, 26, 26))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jComboBox5, javax.swing.GroupLayout.Alignment.LEADING, 0, 288, Short.MAX_VALUE))
                            .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jButton20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(77, 77, 77))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBox3)
                            .addComponent(jCheckBox4)
                            .addComponent(jCheckBox5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBox6)
                            .addComponent(jCheckBox7)
                            .addComponent(jCheckBox8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton18)
                            .addComponent(jButton19)
                            .addComponent(jButton24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton20)
                            .addComponent(jButton21)
                            .addComponent(jButton22))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Universes", jPanel5);

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Name");

        jList1.setModel(colVariable.getModel());
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jList1);

        jLabel2.setText("Nickname");

        jLabel3.setBackground(new java.awt.Color(222, 222, 222));
        jLabel3.setForeground(new java.awt.Color(255, 51, 102));

        jButton2.setText("Clear list");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel4.setText("Construct");

        jButton3.setText("Del");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("<");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText(">");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel30.setText("Universe");

        jComboBox6.setModel(CombolistModelConstructs);

        jComboBox7.setModel(CombolistModelUniverses);

        jButton25.setText("Mode edit");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jTextField1)
                    .addComponent(jTextField2)
                    .addComponent(jComboBox6, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox7, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)
                        .addComponent(jLabel4)
                        .addComponent(jLabel30)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton3)
                            .addComponent(jButton25))
                        .addGap(85, 85, 85)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton4)
                            .addComponent(jButton5)
                            .addComponent(jButton2))))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Variables", jPanel1);

        jComboBox1.setModel(CombolistModelAlias1);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Literal 1:");

        jLabel6.setText("Relation");

        jComboBox2.setModel(CombolistModelRelations1);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Literal 2:");

        jComboBox3.setModel(CombolistModelAlias2);
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jComboBox4.setModel(CombolistModelRelations2);

        jLabel9.setText("Relation");

        jList2.setModel(listModelImplicacionesList);
        jList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jList2);

        jButton6.setText("Add");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Del");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("<");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText(">");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("Clear list");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(222, 222, 222));
        jLabel11.setForeground(new java.awt.Color(255, 51, 102));

        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jCheckBox1.setText("Negated");

        jCheckBox2.setText("Negated");

        jList5.setModel(listModelImplicacionesListCR);
        jScrollPane7.setViewportView(jList5);

        jPanel11.setLayout(new java.awt.CardLayout());

        jComboBox10.setModel(CombolistModelValues1);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox10, 0, 130, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox10, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel11.add(jPanel12, "card-combo1");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel11.add(jPanel13, "card-text1");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox11, 0, 130, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox11, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel11.add(jPanel14, "card-comboCol1");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox15, 0, 130, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox15, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel11.add(jPanel21, "card-comboFunc1");

        jPanel18.setLayout(new java.awt.CardLayout());

        jComboBox12.setModel(CombolistModelValues2);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel18.add(jPanel16, "card-combo2");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField15, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel18.add(jPanel17, "card-text2");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox13, 0, 130, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel18.add(jPanel19, "card-comboCol2");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox14, 0, 130, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox14, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel18.add(jPanel20, "card-comboFunc2");

        jLabel31.setText("Variable name");

        jLabel32.setText("Variable name");

        jButton26.setText("Mode edit");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioButton10);
        jRadioButton10.setSelected(true);
        jRadioButton10.setText("Value");
        jRadioButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton10ActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioButton11);
        jRadioButton11.setText("Function");
        jRadioButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton11ActionPerformed(evt);
            }
        });

        buttonGroup4.add(jRadioButton12);
        jRadioButton12.setSelected(true);
        jRadioButton12.setText("Value");
        jRadioButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton12ActionPerformed(evt);
            }
        });

        buttonGroup4.add(jRadioButton13);
        jRadioButton13.setText("Function");
        jRadioButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton13ActionPerformed(evt);
            }
        });

        jCheckBox9.setSelected(true);
        jCheckBox9.setText("Display Contrapositives");
        jCheckBox9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton26, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel31)
                                .addComponent(jLabel6)
                                .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel5)
                            .addComponent(jCheckBox1)
                            .addComponent(jRadioButton10)
                            .addComponent(jRadioButton11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox2)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel9)
                                .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel32)
                                .addComponent(jLabel8))
                            .addComponent(jRadioButton12)
                            .addComponent(jRadioButton13))))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane7))
                        .addGap(108, 108, 108))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox9)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel32)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12)
                                        .addComponent(jRadioButton12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel31)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jRadioButton10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRadioButton11)
                                        .addGap(14, 14, 14)
                                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCheckBox1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jCheckBox2, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton6)
                            .addComponent(jButton7)
                            .addComponent(jButton26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton8)
                            .addComponent(jButton9)
                            .addComponent(jButton10))))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Implications", jPanel2);

        buttonGroup2.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Table");

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setText("Latex");

        buttonGroup2.add(jRadioButton2);
        jRadioButton2.setText("Graph");

        buttonGroup2.add(jRadioButton4);
        jRadioButton4.setText("Excel (csv)");

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setSelected(true);
        jRadioButton5.setText("Initial");

        buttonGroup1.add(jRadioButton6);
        jRadioButton6.setText("Reduced Cycles");

        buttonGroup1.add(jRadioButton7);
        jRadioButton7.setText("Transitive Closure");

        buttonGroup1.add(jRadioButton8);
        jRadioButton8.setText("Transitive Reduction");

        buttonGroup1.add(jRadioButton9);
        jRadioButton9.setText("Expanded Cycles");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Model stage");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Format");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        jButton11.setText("Generate");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Update");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jList7.setModel(listModelGeneration);
        jList7.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane9.setViewportView(jList7);

        jList8.setModel(listModelGenerationCR);
        jList8.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane10.setViewportView(jList8);

        buttonGroup2.add(jRadioButton14);
        jRadioButton14.setText("Txt");

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "V1", "V2" }));
        jComboBox9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox9ActionPerformed(evt);
            }
        });

        jLabel7.setText("Expanded Cycles");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton8)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel12))
                    .addComponent(jRadioButton7)
                    .addComponent(jRadioButton5)
                    .addComponent(jRadioButton6)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel13))
                    .addComponent(jRadioButton9)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton4)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jRadioButton14)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                    .addComponent(jScrollPane10)
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel12)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jRadioButton5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jRadioButton6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jRadioButton7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jRadioButton8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jRadioButton9)
                            .addGap(59, 59, 59)
                            .addComponent(jLabel7))))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton12)
                            .addComponent(jButton11)))
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );

        jButton12.getAccessibleContext().setAccessibleName("Update ");

        jTabbedPane1.addTab("Generation", jPanel3);
        jPanel3.getAccessibleContext().setAccessibleName("");

        jTextArea2.setEditable(false);
        jTextArea2.setText("\nVersion 1.2.0\n\n Authors:\n\n  - Sergio Gil Borrás (sergio.gil@upm.es)\n  - Jorge Pérez Martinez (jorgeenrique.perez@upm.es)\n  - Jéssica Díaz Fernández (yesica.diaz@upm.es)\n  - Ángel González Prieto (angelgonzalezprieto@ucm.es)\n                                                                                                                 \nSynT is a software tool developed in the context of the Technical University of Madrid (UPM),  aimed at the automatic synthesis of theories from \nformal specifications.\n                                                                                                                 \nThe main goal of SynT is to support the synthesis of theories in domains related to empirical studies involving open systems, facilitating the \ntransition from conceptual models to analyzable formal structures..\n                                                                                                                 \nWith SynT you can:\n\n  * Define constructs, universes, and variables from a study domain.\n  * Specify implications and logical relationships between these elements.\n  * Generate and explore different models (initial, reduced, transitive closures, etc.).\n  * Visualize the information through tables, matrices in LaTeX format, graphs, and CSV files.\n                                                                                                                 \nThe tool is designed to support teaching and research, making it easier to work with formal theories and conceptual models in academic and \nprofessional environments.");
        jScrollPane6.setViewportView(jTextArea2);

        jLabel29.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel29.setText("    SynT - A software tool for automating the theory synthesis process");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane6)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addGap(26, 26, 26)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("About", jPanel10);

        jMenu1.setText("File");

        jMenuItem1.setText("Load");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Save");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 803, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 543, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /* TAB -> Variables --------------------------------------------------------------- */
    /**
     * Synchronizes the scroll bars of the original and cycle-reduced
     * implications lists so that they scroll together.
     */
    private void sincronizarScrollImplicaciones() {
        jScrollPane2.getVerticalScrollBar().setModel(
                jScrollPane7.getVerticalScrollBar().getModel()
        );

        jScrollPane2.getHorizontalScrollBar().setModel(
                jScrollPane7.getHorizontalScrollBar().getModel()
        );
    }

    /**
     * Synchronizes the selection between the two implications JLists.
     * <p>
     * The Implications tab shows two parallel lists (original implications and
     * their cycle-reduced/contrapositive representation). This method ensures
     * that selecting an element in one list selects the corresponding element
     * in the other list, without causing an infinite event loop.
     * </p>
     */
    private void sincronizarJListImplicaciones() {

        AtomicBoolean syncing = new AtomicBoolean(false);

        jList5.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !syncing.get()) {
                syncing.set(true);
                jList2.setSelectedIndex(jList5.getSelectedIndex());
                syncing.set(false);
            }
        });

        jList2.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !syncing.get()) {
                syncing.set(true);
                jList5.setSelectedIndex(jList2.getSelectedIndex());
                syncing.set(false);
            }
        });
    }

    /**
     * Initializes the checkable combo boxes used to select multiple values for
     * collection universes.
     * <p>
     * Sets a custom renderer and toggles the selected state when the user
     * clicks an item.
     * </p>
     */
    private void initComboBoxCheckItem() {
        jComboBox11.setRenderer(new CheckComboRenderer());
        jComboBox13.setRenderer(new CheckComboRenderer());
        jComboBox8.setRenderer(new CheckComboRenderer());

        final AtomicReference<CheckComboItem> lastChecked11 = new AtomicReference<>(null);
        final AtomicReference<CheckComboItem> lastChecked13 = new AtomicReference<>(null);
        final AtomicReference<CheckComboItem> lastChecked8 = new AtomicReference<>(null);

        jComboBox11.setSelectedIndex(-1);
        jComboBox13.setSelectedIndex(-1);
        jComboBox8.setSelectedIndex(-1);

        java.util.function.BiConsumer<JComboBox<CheckComboItem>, AtomicReference<CheckComboItem>> install
                = (combo, lastChecked) -> {
                    combo.putClientProperty("checkComboML", null);
                    combo.addPopupMenuListener(new PopupMenuListener() {
                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                            try {
                                ComboPopup popup = (ComboPopup) combo.getUI().getAccessibleChild(combo, 0);
                                @SuppressWarnings("unchecked")
                                JList<CheckComboItem> list = (JList) popup.getList();

                                if (combo.getClientProperty("checkComboML") != null) {
                                    return;
                                }

                                MouseListener ml = new MouseAdapter() {
                                    @Override
                                    public void mousePressed(MouseEvent me) {
                                        int index = list.locationToIndex(me.getPoint());
                                        if (index < 0) {
                                            return;
                                        }
                                        java.awt.Rectangle cellBounds = list.getCellBounds(index, index);
                                        if (cellBounds == null || !cellBounds.contains(me.getPoint())) {
                                            return;
                                        }

                                        CheckComboItem item = list.getModel().getElementAt(index);
                                        if (item == null) {
                                            return;
                                        }

                                        boolean newState = !item.isSelected();
                                        item.setSelected(newState);

                                        if (newState) {
                                            // marcar como último marcado y mostrarlo
                                            lastChecked.set(item);
                                        } else {
                                            // si se desmarca el último marcado, intentar encontrar otro marcado
                                            if (Objects.equals(lastChecked.get(), item)) {
                                                CheckComboItem found = null;
                                                for (int i = 0; i < list.getModel().getSize(); i++) {
                                                    CheckComboItem ci = list.getModel().getElementAt(i);
                                                    if (ci != null && ci.isSelected()) {
                                                        found = ci;
                                                        break;
                                                    }
                                                }
                                                lastChecked.set(found);
                                            }
                                        }

                                        // Restaurar la selección visible al último marcado o dejar sin selección
                                        final CheckComboItem visible = lastChecked.get();
                                        SwingUtilities.invokeLater(() -> {
                                            if (visible != null) {
                                                combo.setSelectedItem(visible);
                                            } else {
                                                combo.setSelectedIndex(-1);
                                            }
                                            list.repaint();
                                            combo.setPopupVisible(true); // mantener abierto para marcar varios
                                        });
                                    }
                                };

                                list.addMouseListener(ml);
                                combo.putClientProperty("checkComboML", ml);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                            try {
                                MouseListener ml = (MouseListener) combo.getClientProperty("checkComboML");
                                if (ml != null) {
                                    ComboPopup popup = (ComboPopup) combo.getUI().getAccessibleChild(combo, 0);
                                    popup.getList().removeMouseListener(ml);
                                    combo.putClientProperty("checkComboML", null);
                                }
                            } catch (Exception ignored) {
                            }
                        }

                        @Override
                        public void popupMenuCanceled(PopupMenuEvent e) {
                        }
                    });
                };

        install.accept(jComboBox11, lastChecked11);
        install.accept(jComboBox13, lastChecked13);
        install.accept(jComboBox8, lastChecked8);
    }

    /**
     * Returns the text representation of all checked items in a checkable combo
     * box.
     *
     * @param combo the combo box containing {@link CheckComboItem} elements
     * @return a list of strings for all selected items (never {@code null})
     */
    public List<String> getSelectedItems(JComboBox<CheckComboItem> combo) {
        List<String> selected = new ArrayList<>();

        for (int i = 0; i < combo.getItemCount(); i++) {
            CheckComboItem item = combo.getItemAt(i);
            if (item.isSelected()) {
                selected.add(item.toString().trim());
            }
        }
        return selected;
    }

    /**
     * Returns a list of {@link Function} objects corresponding to the selected
     * items in a {@link JComboBox} of {@link CheckComboItem}.
     *
     * @param combo the combo box containing {@link CheckComboItem} values with
     * function names
     * @return an {@link ArrayList} of {@link Function} built from the selected
     * items
     */
    public ArrayList<Function> getSelectedFunctions(JComboBox<CheckComboItem> combo) {
        ArrayList<Function> selected = new ArrayList<>();

        for (int i = 0; i < combo.getItemCount(); i++) {
            CheckComboItem item = combo.getItemAt(i);
            if (item.isSelected()) {
                selected.add(new Function(item.toString()));
            }
        }
        return selected;
    }

    /**
     * Marks as selected the combo box items that match the provided function
     * list. Non-matching items are unselected.
     *
     * @param combo the combo box containing {@link CheckComboItem}
     * @param functionsToSelect list of {@link Function} instances that must
     * remain selected
     */
    public void setSelectedFunctions(
            JComboBox<CheckComboItem> combo,
            ArrayList<Function> functionsToSelect) {

        for (int i = 0; i < combo.getItemCount(); i++) {
            CheckComboItem item = combo.getItemAt(i);
            boolean sel = false;
            for (Function f : functionsToSelect) {
                if (item.toString().equals(f.toString())) {
                    item.setSelected(true);
                    sel = true;
                    break;
                }
            }
            if (!sel) {
                item.setSelected(false);
            }
        }

        combo.repaint();
    }

    /**
     * Unselects all items from a checkable functions combo box.
     *
     * @param combo the combo box containing {@link CheckComboItem} items
     */
    public void unSelectedFunctions(JComboBox<CheckComboItem> combo) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            CheckComboItem item = combo.getItemAt(i);
            item.setSelected(false);
        }
    }

    /**
     * Marks in the combo box the items whose text matches any of the values in
     * the provided array. All other items are unselected.
     *
     * @param combo the combo box containing {@link CheckComboItem} items
     * @param selectedItems array of labels that should remain selected
     */
    public void setSelectedItems(
            JComboBox<CheckComboItem> combo,
            String[] selectedItems) {

        for (int i = 0; i < combo.getItemCount(); i++) {
            CheckComboItem item = combo.getItemAt(i);
            boolean sel = false;
            for (String f : selectedItems) {
                if (item.toString().trim().equals(f.trim())) {
                    item.setSelected(true);
                    sel = true;
                    break;
                }
            }
            if (!sel) {
                item.setSelected(false);
            }
        }

        combo.repaint();
    }

    /**
     * Checks whether a checkable combo box has at least one selected item.
     *
     * @param combo the combo box containing {@link CheckComboItem} elements
     * @return {@code true} if at least one item is selected; otherwise
     * {@code false}
     */
    public boolean isSelectedItems(JComboBox<CheckComboItem> combo) {

        for (int i = 0; i < combo.getItemCount(); i++) {
            CheckComboItem item = combo.getItemAt(i);
            if (item.isSelected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the Add button on the Variables tab.
     * <p>
     * Validates name and nickname, checks for duplicates, creates a new
     * {@link Variable} associated with the selected {@link Construct} and
     * {@link Universe}, and adds it to {@link #colVariable}.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (jTextField1.getText().isEmpty()) {
            jTextField1.grabFocus();
            jLabel3.setText("<html>You cannot leave the name blank.</html>");
        } else if ("Add".equals(jButton1.getText()) && colVariable.containsName(jTextField1.getText())) {
            jTextField1.grabFocus();
            jLabel3.setText("<html>You cannot add duplicate variables.<br/>The name must be unique.</html>");
        } else if (jTextField2.getText().isEmpty()) {
            jTextField2.grabFocus();
            jLabel3.setText("<html>You cannot leave the nickname blank.</html>");
        } else {
            Construct construct = colConstruct.getModelElement().elementAt(jComboBox6.getSelectedIndex());
            Universe universe = colUniverse.getModelElement().elementAt(jComboBox7.getSelectedIndex());
            Variable variable = new Variable(jTextField1.getText().trim(), jTextField2.getText().trim(), construct, universe);
            if ("Add".equals(jButton1.getText())) {
                colVariable.add(variable);
                jLabel3.setText("<html>The variable has been added successfully.</html>");
            } else {
                colVariable.set(selectedIndexVariable, variable);
                jLabel3.setText("<html>The variable has been edited successfully.</html>");
                jButton1.setText("Add");
                jButton25.setText("Mode edit");
            }
        }
        utils.emptyMatriz();
    }

    /**
     * Handles the Clear list button on the Variables tab.
     * <p>
     * Removes all defined variables from {@link #colVariable} and clears the
     * feedback label.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        colVariable.empty();
        jLabel3.setText("<html>The list has been cleared successfully.</html>");
        utils.emptyMatriz();
    }

    /**
     * Handles the Delete button on the Variables tab.
     * <p>
     * Deletes the selected variable from {@link #colVariable}. If no element is
     * selected, a message is shown on the feedback label.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedIndex = jList1.getSelectedIndex();
        if (selectedIndex != -1) {
            colVariable.remove(selectedIndex);
        } else {
            jLabel3.setText("<html>Select an item to delete.</html>");
        }
        utils.emptyMatriz();
    }

    /**
     * Handles the Move up button on the Variables tab.
     * <p>
     * Moves the selected variable one position up in {@link #colVariable}. If
     * the element cannot be moved or no element is selected, an informative
     * message is shown.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedIndex = jList1.getSelectedIndex();
        if (selectedIndex != -1) {
            if (colVariable.indexUp(selectedIndex)) {
                jList1.setSelectedIndex(selectedIndex - 1);
                jLabel3.setText("<html>Item moved.</html>");
            } else {
                jLabel3.setText("<html>The selected item cannot be moved up further.</html>");
            }
        } else {
            jLabel3.setText("<html>Select an item to move up.</html>");
        }
        utils.emptyMatriz();
    }

    /**
     * Handles the Move down button on the Variables tab.
     * <p>
     * Moves the selected variable one position down in {@link #colVariable}. If
     * the element cannot be moved or no element is selected, an informative
     * message is shown.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedIndex = jList1.getSelectedIndex();
        if (selectedIndex != -1) {
            if (colVariable.indexDown(selectedIndex)) {
                jList1.setSelectedIndex(selectedIndex + 1);
                jLabel3.setText("<html>Item moved.</html>");
            } else {
                jLabel3.setText("<html>The selected item cannot be moved down further.</html>");
            }
        } else {
            jLabel3.setText("<html>Select an item to move down.</html>");
        }
        utils.emptyMatriz();
    }

    /* TAB -> Implicaciones ----------------------------------------------------------- */
    /**
     * Handles the Clear list button on the Implications tab.
     * <p>
     * Removes all implications from every internal list model and resets the
     * feedback label.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {
        listModelLiterales1.removeAllElements();
        listModelLiterales2.removeAllElements();
        listModelImplicacionesNot1.removeAllElements();
        listModelImplicacionesNot2.removeAllElements();
        listModelImplicacionesList.removeAllElements();
        listModelImplicacionesListCR.removeAllElements();
        colImplication.empty();
        jLabel3.setText("<html>All implications have been cleared from the list.</html>");
        utils.emptyMatriz();
    }

    /**
     * Handles the Move down button on the Implications tab.
     * <p>
     * Moves the selected implication one position down, keeping all parallel
     * internal lists in sync.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedIndex = jList2.getSelectedIndex();
        if (selectedIndex != -1) {
            if (selectedIndex < listModelImplicacionesList.getSize() - 1) {

                String selectedValue = listModelImplicacionesList.get(selectedIndex);
                String selectedValueCR = listModelImplicacionesListCR.get(selectedIndex);
                String selectedValue1 = listModelLiterales1.get(selectedIndex);
                String selectedValue2 = listModelLiterales2.get(selectedIndex);
                String selectedValueNot1 = listModelImplicacionesNot1.get(selectedIndex);
                String selectedValueNot2 = listModelImplicacionesNot2.get(selectedIndex);

                listModelImplicacionesList.set(selectedIndex, listModelImplicacionesList.get(selectedIndex + 1));
                listModelImplicacionesListCR.set(selectedIndex, listModelImplicacionesListCR.get(selectedIndex + 1));
                listModelLiterales1.set(selectedIndex, listModelLiterales1.get(selectedIndex + 1));
                listModelLiterales2.set(selectedIndex, listModelLiterales2.get(selectedIndex + 1));
                listModelImplicacionesNot1.set(selectedIndex, listModelImplicacionesNot1.get(selectedIndex + 1));
                listModelImplicacionesNot2.set(selectedIndex, listModelImplicacionesNot2.get(selectedIndex + 1));

                listModelImplicacionesList.set(selectedIndex + 1, selectedValue);
                listModelImplicacionesListCR.set(selectedIndex + 1, selectedValueCR);
                listModelLiterales1.set(selectedIndex + 1, selectedValue1);
                listModelLiterales2.set(selectedIndex + 1, selectedValue2);
                listModelImplicacionesNot1.set(selectedIndex + 1, selectedValueNot1);
                listModelImplicacionesNot2.set(selectedIndex + 1, selectedValueNot2);

                colImplication.indexDown(selectedIndex);

                jList2.setSelectedIndex(selectedIndex + 1);
                jLabel3.setText("<html>Item moved.</html>");
            }
        } else {
            jLabel3.setText("<html>Select an item to move down.</html>");
        }
        utils.emptyMatriz();
    }

    /**
     * Handles the Move up button on the Implications tab.
     * <p>
     * Moves the selected implication one position up, keeping all parallel
     * internal lists in sync.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedIndex = jList2.getSelectedIndex();
        if (selectedIndex != -1) {
            if (selectedIndex > 0) {

                String selectedValue = listModelImplicacionesList.get(selectedIndex);
                String selectedValueRC = listModelImplicacionesListCR.get(selectedIndex);
                String selectedValue1 = listModelLiterales1.get(selectedIndex);
                String selectedValue2 = listModelLiterales2.get(selectedIndex);
                String selectedValueNot1 = listModelImplicacionesNot1.get(selectedIndex);
                String selectedValueNot2 = listModelImplicacionesNot2.get(selectedIndex);

                listModelImplicacionesList.set(selectedIndex, listModelImplicacionesList.get(selectedIndex - 1));
                listModelImplicacionesListCR.set(selectedIndex, listModelImplicacionesListCR.get(selectedIndex - 1));
                listModelLiterales1.set(selectedIndex, listModelLiterales1.get(selectedIndex - 1));
                listModelLiterales2.set(selectedIndex, listModelLiterales2.get(selectedIndex - 1));
                listModelImplicacionesNot1.set(selectedIndex, listModelImplicacionesNot1.get(selectedIndex - 1));
                listModelImplicacionesNot2.set(selectedIndex, listModelImplicacionesNot2.get(selectedIndex - 1));

                listModelImplicacionesList.set(selectedIndex - 1, selectedValue);
                listModelImplicacionesListCR.set(selectedIndex - 1, selectedValueRC);
                listModelLiterales1.set(selectedIndex - 1, selectedValue1);
                listModelLiterales2.set(selectedIndex - 1, selectedValue2);
                listModelImplicacionesNot1.set(selectedIndex - 1, selectedValueNot1);
                listModelImplicacionesNot2.set(selectedIndex - 1, selectedValueNot2);

                colImplication.indexUp(selectedIndex);

                jList2.setSelectedIndex(selectedIndex - 1);
                jLabel3.setText("<html>Item moved.</html>");
            }
        } else {
            jLabel3.setText("<html>Select an item to move up.</html>");
        }
        utils.emptyMatriz();
    }

    /**
     * Handles the Delete button on the Implications tab.
     * <p>
     * Deletes the selected implication from all associated internal list
     * models. If no implication is selected, a short message is shown on the
     * feedback label.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedIndex = jList2.getSelectedIndex();
        if (selectedIndex != -1) {
            listModelLiterales1.remove(selectedIndex);
            listModelLiterales2.remove(selectedIndex);
            listModelImplicacionesNot1.remove(selectedIndex);
            listModelImplicacionesNot2.remove(selectedIndex);
            listModelImplicacionesList.remove(selectedIndex);
            listModelImplicacionesListCR.remove(selectedIndex);
            colImplication.remove(selectedIndex);
        } else {
            jLabel11.setText("<html>Select an item to delete.</html>");
        }
        utils.emptyMatriz();
    }

    /**
     * Handles the Add button on the Implications tab.
     * <p>
     * Validates the selection and values of the two literals, prevents
     * implications between the same variable, and then stores the implication
     * in the corresponding list models (including its negated form and
     * contrapositive representation).
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
        if (jComboBox1.getSelectedIndex() == -1 || "".equals(Objects.requireNonNull(jComboBox1.getSelectedItem()).toString())) {
            jComboBox1.grabFocus();
            jLabel11.setText("<html>You must select a variable<br/>for literal 1.</html>");
            return;
        } else if (jComboBox2.getSelectedIndex() == -1 || "".equals(Objects.requireNonNull(jComboBox2.getSelectedItem()).toString())) {
            jComboBox2.grabFocus();
            jLabel11.setText("<html>You must select a relation<br/>for literal 1.</html>");
            return;
        }
        Variable variable1 = colVariable.find((String) jComboBox1.getSelectedItem(), Variable::getNickname);
        switch (variable1.getUniverse().getType()) {
            case "Enum (Scalar)", "Bool" -> {
                if (jComboBox10.getSelectedIndex() == -1 || "".equals(Objects.requireNonNull(jComboBox10.getSelectedItem()).toString())) {
                    jComboBox10.grabFocus();
                    jLabel11.setText("<html>You must select a variable<br/>for value 1.</html>");
                }
            }
            case "Enum (Collection)" -> {
                if (!isSelectedItems(jComboBox11)) {
                    jComboBox11.grabFocus();
                    jLabel11.setText("<html>You must select a variable<br/>for value 1.</html>");
                }
            }
            case "Real" -> {
                try {
                    double value = Double.parseDouble(jTextField4.getText());
                    if (jTextField4.getText().isEmpty()) {
                        jTextField4.grabFocus();
                        jLabel11.setText("<html>You cannot leave the value of<br/>literal 1 blank.</html>");
                        return;
                    } else if (value < variable1.getUniverse().getValueMin()) {
                        jTextField4.grabFocus();
                        jLabel11.setText("<html>The value of literal 1 must be greater than " + variable1.getUniverse().getValueMin() + ".</html>");
                        return;
                    } else if (value > variable1.getUniverse().getValueMax()) {
                        jTextField4.grabFocus();
                        jLabel11.setText("<html>The value of literal 1 must be less than " + variable1.getUniverse().getValueMax() + ".</html>");
                        return;
                    }
                } catch (NumberFormatException e) {
                    jTextField4.grabFocus();
                    jLabel11.setText("<html>The value of literal 1 must be a Double.</html>");
                    return;
                }
            }
        }
        if (jComboBox3.getSelectedIndex() == -1 || "".equals(Objects.requireNonNull(jComboBox3.getSelectedItem()).toString())) {
            jComboBox3.grabFocus();
            jLabel11.setText("<html>You must select a variable<br/>for literal 2.</html>");
            return;
        } else if (jComboBox4.getSelectedIndex() == -1 || "".equals(Objects.requireNonNull(jComboBox4.getSelectedItem()).toString())) {
            jComboBox4.grabFocus();
            jLabel11.setText("<html>You must select a relation<br/>for literal 2.</html>");
            return;
        }
        Variable variable2 = colVariable.find((String) jComboBox3.getSelectedItem(), Variable::getNickname);
        switch (variable2.getUniverse().getType()) {
            case "Enum (Scalar)", "Bool" -> {
                if (jComboBox12.getSelectedIndex() == -1 || "".equals(Objects.requireNonNull(jComboBox12.getSelectedItem()).toString())) {
                    jComboBox12.grabFocus();
                    jLabel11.setText("<html>You must select a variable<br/>for value 2.</html>");
                }
            }
            case "Enum (Collection)" -> {
                if (!isSelectedItems(jComboBox13)) {
                    jComboBox13.grabFocus();
                    jLabel11.setText("<html>You must select a variable<br/>for value 2.</html>");
                }
            }
            case "Real" -> {
                try {
                    double value = Double.parseDouble(jTextField15.getText());
                    if (jTextField15.getText().isEmpty()) {
                        jTextField15.grabFocus();
                        jLabel11.setText("<html>You cannot leave the value of<br/>literal 2 blank.</html>");
                        return;
                    } else if (value < variable2.getUniverse().getValueMin()) {
                        jTextField15.grabFocus();
                        jLabel11.setText("<html>The value of literal 2 must be greater than " + variable2.getUniverse().getValueMin() + ".</html>");
                        return;
                    } else if (value > variable2.getUniverse().getValueMax()) {
                        jTextField15.grabFocus();
                        jLabel11.setText("<html>The value of literal 2 must be less than " + variable2.getUniverse().getValueMax() + ".</html>");
                        return;
                    }
                } catch (NumberFormatException e) {
                    jTextField15.grabFocus();
                    jLabel11.setText("<html>The value of literal 2 must be a Double.</html>");
                    return;
                }
            }
        }
        if (jComboBox3.getSelectedIndex() == jComboBox1.getSelectedIndex()) {
            jComboBox3.grabFocus();
            jLabel11.setText("<html>You cannot create implications<br/>between the same variable.</html>");
        } else {

            String value1 = "";
            if (jRadioButton10.isSelected()) {
                switch (variable1.getUniverse().getType()) {
                    case "Enum (Scalar)", "Bool" ->
                        value1 = String.valueOf(jComboBox10.getSelectedItem());
                    case "Enum (Collection)" ->
                        value1 = String.valueOf(getSelectedItems(jComboBox11));
                    case "Real" ->
                        value1 = jTextField4.getText();
                }
            } else {
                value1 = String.valueOf(jComboBox15.getSelectedItem());
            }

            String value2 = "";
            if (jRadioButton12.isSelected()) {
                switch (variable2.getUniverse().getType()) {
                    case "Enum (Scalar)", "Bool" ->
                        value2 = String.valueOf(jComboBox12.getSelectedItem());
                    case "Enum (Collection)" ->
                        value2 = String.valueOf(getSelectedItems(jComboBox13));
                    case "Real" ->
                        value2 = jTextField15.getText();
                }
            } else {
                value2 = String.valueOf(jComboBox14.getSelectedItem());
            }
            boolean valueFunction1 = jRadioButton10.isSelected();
            boolean valueFunction2 = jRadioButton12.isSelected();
            Implication implication = new Implication(colVariable.find(String.valueOf(jComboBox1.getSelectedItem()), Variable::getNickname), String.valueOf(jComboBox2.getSelectedItem()), value1, jCheckBox1.isSelected(), valueFunction1,
                    colVariable.find(String.valueOf(jComboBox3.getSelectedItem()), Variable::getNickname), String.valueOf(jComboBox4.getSelectedItem()), value2, jCheckBox2.isSelected(), valueFunction2);

            if ("Add".equals(jButton6.getText())) {
                if (listModelImplicacionesList.contains(implication.toString())) {
                    jLabel11.setText("<html>You cannot add duplicate implications.</html>");
                } else {
                    colImplication.add(implication);

                    listModelLiterales1.addElement(implication.getLiteral1());
                    listModelLiterales2.addElement(implication.getLiteral2());
                    listModelImplicacionesNot1.addElement(String.valueOf(implication.isNegated1()));
                    listModelImplicacionesNot2.addElement(String.valueOf(implication.isNegated2()));
                    listModelImplicacionesList.addElement(implication.toString());
                    listModelImplicacionesListCR.addElement(implication.toStringCR());
                    jLabel11.setText("<html>The implication has been added successfully.</html>");
                    jCheckBox1.setSelected(false);
                    jCheckBox2.setSelected(false);
                    jList2.ensureIndexIsVisible(listModelImplicacionesList.size() - 1);
                    jList5.ensureIndexIsVisible(listModelImplicacionesListCR.size() - 1);
                }
            } else {
                colImplication.set(selectedIndexImplication, implication);

                listModelLiterales1.setElementAt(implication.getLiteral1(), selectedIndexImplication);
                listModelLiterales2.setElementAt(implication.getLiteral2(), selectedIndexImplication);
                listModelImplicacionesNot1.setElementAt(String.valueOf(implication.isNegated1()), selectedIndexImplication);
                listModelImplicacionesNot2.setElementAt(String.valueOf(implication.isNegated2()), selectedIndexImplication);
                listModelImplicacionesList.setElementAt(implication.toString(), selectedIndexImplication);
                listModelImplicacionesListCR.setElementAt(implication.toStringCR(), selectedIndexImplication);
                jLabel11.setText("<html>The implication has been edit successfully.</html>");
                jButton6.setText("Add");
                jButton26.setText("Mode edit");

            }
            utils.emptyMatriz();
        }
    }

    /**
     * Handles the Save menu item.
     * <p>
     * Opens a {@link JFileChooser} dialog to select a destination
     * <code>.sgb</code> file and serializes the current session as JSON.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the menu item
     */
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save File");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Files with extension (*.sgb)", "sgb");
        fileChooser.setFileFilter(filter);

        // Show file chooser dialog
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getName();
            if (!fileName.endsWith(".sgb")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".sgb"); // Add default extension
            }
            // Content to save
            String content = utils.generarJSON(colConstruct, colUniverse, colVariable, colImplication, colFunction);

            // Guardar el contenido en el archivo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "File saved at: " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }

    /**
     * Handles the Load menu item.
     * <p>
     * Opens a {@link JFileChooser} dialog to select a <code>.sgb</code> file
     * and loads the stored session into the current GUI.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the menu item
     */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select .sgb file to load");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Files with extension (*.sgb)", "sgb");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            String fileName = fileToLoad.getName();
            if (!fileName.endsWith(".sgb")) {
                JOptionPane.showMessageDialog(this, "Error loading file: the file must have a .sgb extension");
            } else {
                try {
                    cargarStringJSON(fileToLoad);
                    jTabbedPane1.setSelectedIndex(0);
                    JOptionPane.showMessageDialog(this, "File loaded successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Loads a session from a JSON file generated by SynT.
     *
     * @param fileToLoad the <code>.sgb</code> file to read
     * @throws JSONException if the JSON structure is invalid or missing fields
     * @throws IOException if the file cannot be read
     */
    private void cargarStringJSON(File fileToLoad) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileToLoad.getAbsolutePath())));
        JSONObject json = new JSONObject(content);

        /* //////////////////////// Constructs //////////////////////////// */
        JSONArray jConstructs = json.getJSONArray("Constructs");

        colConstruct.empty();
        for (int i = 0; i < jConstructs.length(); i++) {
            Construct construct = new Construct(jConstructs.getJSONObject(i).getString("Name"), jConstructs.getJSONObject(i).getString("From"), jConstructs.getJSONObject(i).getString("Scope"));
            colConstruct.add(construct);
        }

        /* //////////////////////// Functions //////////////////////////// */
        JSONArray jFunctions = json.getJSONArray("Functions");

        colFunction.empty();
        for (int i = 0; i < jFunctions.length(); i++) {
            Function function = new Function(jFunctions.getJSONObject(i).getString("Name"), jFunctions.getJSONObject(i).getInt("Aridad"));
            colFunction.add(function);
        }

        /* //////////////////////// Universes //////////////////////////// */
        JSONArray jUniverses = json.getJSONArray("Universes");

        colUniverse.empty();
        for (int i = 0; i < jUniverses.length(); i++) {

            JSONArray jUniFunction = jUniverses.getJSONObject(i).getJSONArray("Functions");
            ArrayList<Function> ALFunctions = new ArrayList<>();
            for (int j = 0; j < jUniFunction.length(); j++) {
                ALFunctions.add(new Function(jUniFunction.getJSONObject(j).getString("Name"), jUniFunction.getJSONObject(j).getInt("Aridad")));
            }

            Universe universe = switch (jUniverses.getJSONObject(i).getString("Type")) {
                case "Enum (Scalar)", "Enum (Collection)" ->
                    new Universe(jUniverses.getJSONObject(i).getString("Name"), jUniverses.getJSONObject(i).getString("Type"), jUniverses.getJSONObject(i).getString("ValueEnum"), ALFunctions, jUniverses.getJSONObject(i).getBoolean("Equal"), jUniverses.getJSONObject(i).getBoolean("Greater"), jUniverses.getJSONObject(i).getBoolean("Greater_equal"), jUniverses.getJSONObject(i).getBoolean("Not_equal"), jUniverses.getJSONObject(i).getBoolean("Less"), jUniverses.getJSONObject(i).getBoolean("Less_equal"));
                case "Real" ->
                    new Universe(jUniverses.getJSONObject(i).getString("Name"), jUniverses.getJSONObject(i).getString("Type"), jUniverses.getJSONObject(i).getDouble("ValueMin"), jUniverses.getJSONObject(i).getDouble("ValueMax"), ALFunctions, jUniverses.getJSONObject(i).getBoolean("Equal"), jUniverses.getJSONObject(i).getBoolean("Greater"), jUniverses.getJSONObject(i).getBoolean("Greater_equal"), jUniverses.getJSONObject(i).getBoolean("Not_equal"), jUniverses.getJSONObject(i).getBoolean("Less"), jUniverses.getJSONObject(i).getBoolean("Less_equal"));
                case "Bool" ->
                    new Universe(jUniverses.getJSONObject(i).getString("Name"), jUniverses.getJSONObject(i).getString("Type"), ALFunctions, jUniverses.getJSONObject(i).getBoolean("Equal"), jUniverses.getJSONObject(i).getBoolean("Greater"), jUniverses.getJSONObject(i).getBoolean("Greater_equal"), jUniverses.getJSONObject(i).getBoolean("Not_equal"), jUniverses.getJSONObject(i).getBoolean("Less"), jUniverses.getJSONObject(i).getBoolean("Less_equal"));
                default ->
                    throw new AssertionError();
            };
            colUniverse.add(universe);
        }

        /* ///////////////////////////// Variables ///////////////////////// */
        JSONArray jVariables = json.getJSONArray("Variables");

        colVariable.empty();
        for (int i = 0; i < jVariables.length(); i++) {
            Construct lConstruct = colConstruct.findName(jVariables.getJSONObject(i).getString("ConstructName"));
            Universe lUniverse = colUniverse.findName(jVariables.getJSONObject(i).getString("UniverseName"));
            Variable variable = new Variable(jVariables.getJSONObject(i).getString("Name"), jVariables.getJSONObject(i).getString("Nickname"), lConstruct, lUniverse);
            colVariable.add(variable);
        }

        /* ////////////////////// Implicaciones //////////////////////////////// */
        JSONArray jImplications = json.getJSONArray("Implications");

        listModelLiterales1.removeAllElements(); // Clear the list
        listModelLiterales2.removeAllElements(); // Clear the list
        listModelImplicacionesNot1.removeAllElements(); // Clear the list
        listModelImplicacionesNot2.removeAllElements(); // Clear the list
        listModelImplicacionesList.removeAllElements(); // Clear the list

        colImplication.empty(); // Clear the list

        for (int i = 0; i < jImplications.length(); i++) {

            Implication implication = new Implication(colVariable.find(jImplications.getJSONObject(i).getString("Variable1"), Variable::getNickname),
                    jImplications.getJSONObject(i).getString("Relation1"),
                    jImplications.getJSONObject(i).getString("Value1"),
                    jImplications.getJSONObject(i).getBoolean("Negated1"),
                    jImplications.getJSONObject(i).getBoolean("ValueFunction1"),
                    colVariable.find(jImplications.getJSONObject(i).getString("Variable2"), Variable::getNickname),
                    jImplications.getJSONObject(i).getString("Relation2"),
                    jImplications.getJSONObject(i).getString("Value2"),
                    jImplications.getJSONObject(i).getBoolean("Negated2"),
                    jImplications.getJSONObject(i).getBoolean("ValueFunction2"));

            colImplication.add(implication);

            listModelImplicacionesList.addElement(implication.toString());
            listModelImplicacionesListCR.addElement(implication.toStringCR());

            listModelLiterales1.addElement(implication.getLiteral1());
            listModelLiterales2.addElement(implication.getLiteral2());
            listModelImplicacionesNot1.addElement(String.valueOf(implication.isNegated1()));
            listModelImplicacionesNot2.addElement(String.valueOf(implication.isNegated2()));
        }
    }

    /**
     * Handles the Generate button on the Generation tab.
     * <p>
     * Retrieves the matrix and node list selected according to the current
     * radio buttons and delegates to the appropriate representation method
     * (table, graph, LaTeX or CSV).
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {
        int[][] matriz = getMatrizSeleccionada();
        if (matriz != null) {
            List<String> nodos = getNodosSeleccionados();
            if (nodos != null) {

                cargarRelacionesGenerarTab(matriz, nodos);

                if (jRadioButton1.isSelected()) {
                    representacionTipoTabla(matriz, nodos);
                } else if (jRadioButton2.isSelected()) {
                    representacionTipoGrafo(matriz, nodos);
                } else if (jRadioButton3.isSelected()) {
                    representacionTipoLatex(matriz, nodos);
                } else if (jRadioButton4.isSelected()) {
                    representacionTipoExcel(matriz, nodos);
                } else if (jRadioButton14.isSelected()) {
                    representacionTipoTxt(matriz, nodos);
                }
            }
        }
    }

    /**
     * Handles the Update button on the Generation tab.
     * <p>
     * This does not change the currently selected representation (table/graph/etc.).
     * It only refreshes the relationship lists shown on the right side by
     * re-reading the currently selected matrix and node list.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {
        int[][] matriz = getMatrizSeleccionada();
        if (matriz != null) {
            List<String> nodos = getNodosSeleccionados();
            if (nodos != null) {
                cargarRelacionesGenerarTab(matriz, nodos);
            }
        }
    }

    /**
     * Populates a JList with relationships in the form {@code Node1 --> Node2}
     * by scanning the implication matrix.
     * <p>
     * Assumes {@code matriz} is {@code N x N} and {@code nodos} has size
     * {@code N}.
     * </p>
     *
     * @param matriz implication/counter-implication matrix ({@code N x N})
     * @param nodos list of node labels ({@code N})
     * @throws IllegalArgumentException if the matrix is smaller than
     * {@code N x N}
     */
    public void cargarRelacionesGenerarTab(int[][] matriz, List<String> nodos) {
        int n = nodos.size();

        listModelGeneration.removeAllElements();

        // Minimal validation to avoid index errors
        if (matriz == null || matriz.length < n || matriz[0].length < n) {
            //System.err.println("Error: " + matriz.length + "x" + matriz[0].length);
            //System.err.println("Error: " + nodos);
            //System.err.println("Error: " + nodos.size());
            throw new IllegalArgumentException("The matrix must be at least NxN to process the first quadrant.");
        }

        int pos = 1;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                // Define the "there is a relation" condition here
                // ( 1 = hay relación, 0 = no hay)
                if (matriz[i][j] != 0) {
                    //String rel = "[" + nodos.get(i) + "] --> [" + nodos.get(j) + "]";
                    String rel = nodos.get(i) + " --> " + nodos.get(j);

                    if (!jRadioButton9.isSelected()) {
                        rel = pos + "|  " + rel;
                    }

                    pos++;
                    listModelGeneration.addElement(rel);

                }
            }
        }

        if (jRadioButton9.isSelected()) {
            DefaultListModel<String> total = listModelImplicacionesList;//defaultListModelSum(listModelImplicacionesList, listModelImplicacionesListCR);
            jList8.setEnabled(true);
            listModelGenerationCR.removeAllElements();
            listModelGenerationCR = defaultListModelDiff(total, listModelGeneration);
            //System.out.println("SIZEEE: : :  " + listModelGenerationCR.size());
            jList8.setModel(listModelGenerationCR);
        } else {
            listModelGenerationCR.removeAllElements();
            jList8.setEnabled(false);
        }


        pos = 1;
        listModelGeneration.removeAllElements();
        int imp = 0;
        int new_imp = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Define the "there is a relation" condition here
                // ( 1 = hay relación, 0 = no hay)
                if (matriz[i][j] != 0) {
                    //String rel = "[" + nodos.get(i) + "] --> [" + nodos.get(j) + "]";

                    String rel_i = nodos.get(i) + " --> " + nodos.get(j);
                    String rel = pos + "|  " + rel_i;

                    if (listModelImplicacionesList.contains(rel_i)) {
                        pos++;
                        listModelGeneration.addElement(rel);
                        imp ++;
                    } else {
                        String rel_ii = negarVariable(nodos.get(j)) + " --> " + negarVariable(nodos.get(i));
                        if (!listModelImplicacionesList.contains(rel_ii)) {
                            pos++;
                            listModelGeneration.addElement(rel + " *");
                            new_imp ++;
                        }
                    }

                }
            }
        }
        if (jRadioButton9.isSelected()) {
            if (new_imp>0) {
                updateInfoModelo(imp + " + " + new_imp);
            }else{
                updateInfoModelo(String.valueOf(imp));
            }
        }
    }

    /**
     * Returns the logical negation of a node label.
     * <p>
     * Node labels represent literals as strings and use the prefix {@code "¬ "}
     * (note the trailing space). If the prefix is present, it is removed;
     * otherwise, it is added.
     * </p>
     *
     * @param variable node label (e.g. {@code "[Tw = True]"} or {@code "¬ [Tw = True]"})
     * @return the negated label
     */
    private String negarVariable(String variable) {
        if (variable.contains("¬ ")) {
            return variable.substring(2);
        } else {
            return "¬ " + variable;
        }
    }

    /**
     * Computes the set difference {@code a \ b} for two list models.
     * <p>
     * The resulting list keeps the iteration order from {@code a} and numbers
     * each entry using the format {@code "pos|  element"}.
     * </p>
     *
     * @param a base list model
     * @param b elements to exclude from {@code a}
     * @return a new list model containing all elements from {@code a} not present in {@code b}
     */
    private DefaultListModel<String> defaultListModelDiff(
            DefaultListModel<String> a,
            DefaultListModel<String> b) {

        DefaultListModel<String> resultado = new DefaultListModel<>();
        int pos = 1;
        for (int i = 0; i < a.size(); i++) {
            String elemento = a.get(i);

            if (!defaultListModelContain(b, elemento)) {
                resultado.addElement(pos + "|  " + elemento);
                pos++;
            }
        }
        return resultado;
    }

    /**
     * Reacts to changing the selected tab in the main tabbed pane.
     * <p>
     * When switching to the Variables, Implications or Generation tabs, this
     * method refreshes the corresponding combo box models and model matrices.
     * </p>
     *
     * @param evt the change event fired by {@link #jTabbedPane1}
     */
    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {
        switch (jTabbedPane1.getSelectedIndex()) {
            case 2:
                eventoTabUniverses();
                break;
            case 3:
                eventoTabVariables();
                break;
            case 4:
                eventoTabImplicaciones();
                break;
            case 5:
                eventoTabImplicaciones();
                eventoTabGeneracion();
                break;
            default:
                break;
        }
    }

    /**
     * Handles the Update from matrices button on the Implications tab.
     * <p>
     * Rebuilds the list of implications from the last generated model matrix,
     * keeping the internal list models (literals and negation flags)
     * consistent.
     * </p>
     *
     */
    private void updateNewImplicationAfterReduction() {
        if (utils.getListMatrices() != null && !utils.getListMatrices().isEmpty()) {

            listModelLiterales1.clear();
            listModelLiterales2.clear();
            listModelImplicacionesNot1.clear();
            listModelImplicacionesNot2.clear();
            listModelImplicacionesList.clear();

            int[][] matriz = utils.getListMatrices().get(utils.getListMatrices().size() - 1);

            for (int i = 0; i < matriz.length; i++) {
                if (utils.estanConectadosNodos(matriz, 0, i)) {
                    for (int j = 0; j < matriz[i].length; j++) {
                        if (matriz[i][j] > 0) {
                            String l1 = utils.getNodos().get(i);
                            String l2 = utils.getNodos().get(j);
                            boolean l1_not = l1.contains("¬");
                            boolean l2_not = l2.contains("¬");
                            l1 = l1.replace("¬", "").trim();
                            l2 = l2.replace("¬", "").trim();
                            listModelLiterales1.addElement(l1);
                            listModelLiterales2.addElement(l2);
                            listModelImplicacionesNot1.addElement(l1_not ? "true" : "false");
                            listModelImplicacionesNot2.addElement(l2_not ? "true" : "false");
                            listModelImplicacionesList.addElement((l1_not ? "¬ " : "") + l1 + " --> " + (l2_not ? "¬ " : "") + l2);
                        }
                    }
                }
            }
            utils.emptyMatriz();
        }
    }

    /* TAB -> Constructs -------------------------------------------------------------- */
    /**
     * Handles the Add button on the Constructs tab.
     * <p>
     * Validates the construct fields, prevents duplicate names and stores the
     * new {@link Construct} in {@link #colConstruct}.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        if (jTextField6.getText().isEmpty()) {
            jTextField6.grabFocus();
            jLabel17.setText("<html>You cannot leave the name blank.</html>");
        } else if ("Add".equals(jButton13.getText()) && colConstruct.containsName(jTextField6.getText())) {
            jTextField6.grabFocus();
            jLabel17.setText("<html>You cannot add duplicate names.</html>");
        } else if (jTextField7.getText().isEmpty()) {
            jTextField7.grabFocus();
            jLabel17.setText("<html>You cannot leave the 'From' field<br/>blank.</html>");
        } else if (jTextField8.getText().isEmpty()) {
            jTextField8.grabFocus();
            jLabel17.setText("<html>You cannot leave the scope conditions<br/>blank.</html>");
        } else {
            Construct construct = new Construct(jTextField6.getText().trim(), jTextField7.getText().trim(), jTextField8.getText().trim());
            if ("Add".equals(jButton13.getText())) {
                colConstruct.add(construct);
                jLabel17.setText("<html>The construct has been added successfully.</html>");
            } else {
                colConstruct.set(selectedIndexConstruct, construct);
                jLabel17.setText("<html>The construct has been edited successfully.</html>");
                jButton23.setText("Mode edit");
                jButton13.setText("Add");
            }
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton13ActionPerformed

    /**
     * Handles the Delete button on the Constructs tab.
     * <p>
     * Deletes the selected construct from {@link #colConstruct}. If no element
     * is selected, a message is shown on the feedback label.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        int selectedIndex = jList3.getSelectedIndex();
        if (selectedIndex != -1) {
            colConstruct.remove(selectedIndex);
        } else {
            jLabel17.setText("<html>Select an item to delete.</html>");
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton14ActionPerformed

    /**
     * Handles the Move up button on the Constructs tab.
     * <p>
     * Moves the selected construct one position up in {@link #colConstruct}. If
     * the element cannot be moved or no element is selected, an informative
     * message is shown.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        int selectedIndex = jList3.getSelectedIndex();
        if (selectedIndex != -1) {
            if (colConstruct.indexUp(selectedIndex)) {
                jList3.setSelectedIndex(selectedIndex - 1);
                jLabel17.setText("<html>Item moved.</html>");
            } else {
                jLabel17.setText("<html>The selected item cannot be moved up further.</html>");
            }
        } else {
            jLabel17.setText("<html>Select an item to move up.</html>");
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton15ActionPerformed

    /**
     * Handles the Move down button on the Constructs tab.
     * <p>
     * Moves the selected construct one position down in {@link #colConstruct}.
     * If the element cannot be moved or no element is selected, an informative
     * message is shown.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        int selectedIndex = jList3.getSelectedIndex();
        if (selectedIndex != -1) {
            if (colConstruct.indexDown(selectedIndex)) {
                jList3.setSelectedIndex(selectedIndex + 1);
                jLabel17.setText("<html>Item moved.</html>");
            } else {
                jLabel17.setText("<html>The selected item cannot be moved down further.</html>");
            }
        } else {
            jLabel17.setText("<html>Select an item to move down.</html>");
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton16ActionPerformed

    /**
     * Handles the Clear list button on the Constructs tab.
     * <p>
     * Removes all constructs from {@link #colConstruct} and clears the feedback
     * label.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        colConstruct.empty();
        jLabel17.setText("<html>The list has been cleared successfully.</html>");
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton17ActionPerformed

    /* TAB --> UNIVERSE --------------------------------------------------- */
    /**
     * Handles the Add button on the Universes tab.
     * <p>
     * Depending on the selected universe type (Enum/Real/Bool), validates the
     * input fields and creates the corresponding {@link Universe} instance.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        if (jTextField9.getText().isEmpty()) {
            jTextField9.grabFocus();
            jLabel21.setText("<html>You cannot leave the name blank.</html>");
            return;
        } else if ("Add".equals(jButton18.getText()) && colUniverse.containsName(jTextField9.getText())) {
            jTextField9.grabFocus();
            jLabel21.setText("<html>You cannot add duplicate names.</html>");
            return;
        }
        ArrayList<Function> ALFunction = getSelectedFunctions(jComboBox8);
        switch (jComboBox5.getSelectedIndex()) {
            case 0, 1: //Enums
                if (jTextField12.getText().isEmpty()) {
                    jTextField12.grabFocus();
                    jLabel21.setText("<html>You cannot leave the Enum Values<br/>blank.</html>");
                    return;
                } else {
                    Universe universe = new Universe(jTextField9.getText().trim(), (String) jComboBox5.getSelectedItem(), jTextField12.getText().trim(), ALFunction, jCheckBox3.isSelected(), jCheckBox4.isSelected(), jCheckBox5.isSelected(), jCheckBox6.isSelected(), jCheckBox7.isSelected(), jCheckBox8.isSelected());

                    if ("Add".equals(jButton18.getText())) {
                        colUniverse.add(universe);
                    } else {
                        colUniverse.set(selectedIndexUniverse, universe);
                    }
                }
                break;
            case 2: //Real
                if (jTextField13.getText().isEmpty()) {
                    jTextField13.grabFocus();
                    jLabel21.setText("<html>You cannot leave the Min value blank.</html>");
                    return;
                } else if (jTextField14.getText().isEmpty()) {
                    jTextField14.grabFocus();
                    jLabel21.setText("<html>You cannot leave Max value blank.</html>");
                    return;
                } else {
                    double max;
                    try {
                        max = Double.parseDouble(jTextField14.getText());
                    } catch (NumberFormatException e) {
                        max = Double.MIN_VALUE;
                    }
                    double min;
                    try {
                        min = Double.parseDouble(jTextField13.getText());
                    } catch (NumberFormatException e) {
                        min = Double.MAX_VALUE;
                    }
                    Universe universe = new Universe(jTextField9.getText().trim(), (String) jComboBox5.getSelectedItem(), min, max, ALFunction, jCheckBox3.isSelected(), jCheckBox4.isSelected(), jCheckBox5.isSelected(), jCheckBox6.isSelected(), jCheckBox7.isSelected(), jCheckBox8.isSelected());

                    if ("Add".equals(jButton18.getText())) {
                        colUniverse.add(universe);
                    } else {
                        colUniverse.set(selectedIndexUniverse, universe);
                    }
                }
                break;
            case 3: //Bool
                Universe universe = new Universe(jTextField9.getText().trim(), (String) jComboBox5.getSelectedItem(), ALFunction, jCheckBox3.isSelected(), jCheckBox4.isSelected(), jCheckBox5.isSelected(), jCheckBox6.isSelected(), jCheckBox7.isSelected(), jCheckBox8.isSelected());

                if ("Add".equals(jButton18.getText())) {
                    colUniverse.add(universe);
                } else {
                    colUniverse.set(selectedIndexUniverse, universe);
                }
                break;
            default:
                throw new AssertionError();
        }
        if ("Add".equals(jButton18.getText())) {
            jLabel21.setText("<html>The Universe has been added successfully!</html>");
        } else {
            jLabel21.setText("<html>The Universe has been edited successfully!</html>");
            jButton18.setText("Add");
            jButton24.setText("Mode edit");
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton18ActionPerformed

    /**
     * Handles the Delete button on the Universes tab.
     * <p>
     * Deletes the selected universe from {@link #colUniverse}. If no element is
     * selected, a message is shown on the feedback label.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        int selectedIndex = jList4.getSelectedIndex();
        if (selectedIndex != -1) {
            colUniverse.remove(selectedIndex);
        } else {
            jLabel21.setText("<html>Select an item to delete.</html>");
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton19ActionPerformed

    /**
     * Handles the Move up button on the Universes tab.
     * <p>
     * Moves the selected universe one position up in {@link #colUniverse}. If
     * the element cannot be moved or no element is selected, an informative
     * message is shown.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        int selectedIndex = jList4.getSelectedIndex();
        if (selectedIndex != -1) {
            if (colUniverse.indexUp(selectedIndex)) {
                jList4.setSelectedIndex(selectedIndex - 1);
                jLabel21.setText("<html>Item moved.</html>");
            } else {
                jLabel21.setText("<html>The selected item cannot be moved up further.</html>");
            }
        } else {
            jLabel21.setText("<html>Select an item to move up.</html>");
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton20ActionPerformed

    /**
     * Handles the Move down button on the Universes tab.
     * <p>
     * Moves the selected universe one position down in {@link #colUniverse}. If
     * the element cannot be moved or no element is selected, an informative
     * message is shown.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        int selectedIndex = jList4.getSelectedIndex();
        if (selectedIndex != -1) {
            if (colUniverse.indexDown(selectedIndex)) {
                jList4.setSelectedIndex(selectedIndex + 1);
                jLabel21.setText("<html>Item moved.</html>");
            } else {
                jLabel21.setText("<html>The selected item cannot be moved down further.</html>");
            }
        } else {
            jLabel21.setText("<html>Select an item to move down.</html>");
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton21ActionPerformed

    /**
     * Handles the Clear list button on the Universes tab.
     * <p>
     * Removes all universes from {@link #colUniverse} and clears the feedback
     * label.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        colUniverse.empty();
        jLabel21.setText("<html>The list has been cleared successfully.</html>");
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton22ActionPerformed

    /**
     * Reacts to a change in the universe type combo box on the Universes tab.
     * <p>
     * Shows the appropriate card (Enum, Real or Bool) for the universe details.
     * </p>
     *
     * @param evt the Swing action event triggered by changing the selection
     */
    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
        CardLayout cl = (CardLayout) jPanel7.getLayout();

        switch (jComboBox5.getSelectedIndex()) {
            case 0, 1 ->
                cl.show(jPanel7, "card_Enum");
            case 2 ->
                cl.show(jPanel7, "card_Real");
            case 3 ->
                cl.show(jPanel7, "card_Bool");
        }

    }//GEN-LAST:event_jComboBox5ActionPerformed

    /**
     * Reacts to a change in the second literal variable combo box.
     * <p>
     * Updates the available relations for literal 2 according to the selected
     * variable's universe.
     * </p>
     *
     * @param evt the Swing action event triggered by changing the selection
     */
    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        CombolistModelRelations2.removeAllElements();
        Variable variable = colVariable.find((String) jComboBox3.getSelectedItem(), Variable::getNickname);
        if (variable != null) {
            if (variable.getUniverse().isEqual()) {
                CombolistModelRelations2.addElement("=");
            }
            if (variable.getUniverse().isGreater()) {
                CombolistModelRelations2.addElement(">");
            }
            if (variable.getUniverse().isGreater_equal()) {
                CombolistModelRelations2.addElement(">=");
            }
            if (variable.getUniverse().isNot_equal()) {
                CombolistModelRelations2.addElement("!=");
            }
            if (variable.getUniverse().isLess()) {
                CombolistModelRelations2.addElement("<");
            }
            if (variable.getUniverse().isLess_equal()) {
                CombolistModelRelations2.addElement("<=");
            }

            CardLayout cl = (CardLayout) jPanel18.getLayout();

            switch (variable.getUniverse().getType()) {
                case "Enum (Scalar)", "Bool" ->
                    cl.show(jPanel18, "card-combo2");
                case "Enum (Collection)" ->
                    cl.show(jPanel18, "card-comboCol2");
                case "Real" ->
                    cl.show(jPanel18, "card-text2");
            }

            CombolistModelValues2.removeAllElements();
            jComboBox13.removeAllItems();
            switch (variable.getUniverse().getType()) {
                case "Enum (Scalar)" -> {
                    String[] values = variable.getUniverse().getValueEnum().split(",");
                    for (String value : values) {
                        CombolistModelValues2.addElement(value.trim());
                    }
                }
                case "Enum (Collection)" -> {
                    String[] values = variable.getUniverse().getValueEnum().split(",");
                    for (String value : values) {
                        CheckComboItem item = new CheckComboItem(value.trim());
                        item.setSelected(false);
                        jComboBox13.addItem(item);
                    }
                }
                case "Bool" -> {
                    CombolistModelValues2.addElement("True");
                    CombolistModelValues2.addElement("False");
                }
            }

            jComboBox14.removeAllItems();
            ArrayList<Function> functions = variable.getUniverse().getFunctions();
            for (Function function : functions) {
                jComboBox14.addItem(function.toString().trim());
            }
            jComboBox14.setSelectedIndex(-1);
            jRadioButton12.setSelected(true);
            if (functions.isEmpty()) {
                jRadioButton13.setEnabled(false);
            } else {
                jRadioButton13.setEnabled(true);
            }
        }
    }//GEN-LAST:event_jComboBox3ActionPerformed

    /**
     * Reacts to a change in the first literal variable combo box.
     * <p>
     * Updates the available relations for literal 1 according to the selected
     * variable's universe.
     * </p>
     *
     * @param evt the Swing action event triggered by changing the selection
     */
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        CombolistModelRelations1.removeAllElements();
        Variable variable = colVariable.find((String) jComboBox1.getSelectedItem(), Variable::getNickname);
        if (variable != null) {
            if (variable.getUniverse().isEqual()) {
                CombolistModelRelations1.addElement("=");
            }
            if (variable.getUniverse().isGreater()) {
                CombolistModelRelations1.addElement(">");
            }
            if (variable.getUniverse().isGreater_equal()) {
                CombolistModelRelations1.addElement(">=");
            }
            if (variable.getUniverse().isNot_equal()) {
                CombolistModelRelations1.addElement("!=");
            }
            if (variable.getUniverse().isLess()) {
                CombolistModelRelations1.addElement("<");
            }
            if (variable.getUniverse().isLess_equal()) {
                CombolistModelRelations1.addElement("<=");
            }

            CardLayout cl = (CardLayout) jPanel11.getLayout();

            switch (variable.getUniverse().getType()) {
                case "Enum (Scalar)", "Bool" ->
                    cl.show(jPanel11, "card-combo1");
                case "Enum (Collection)" ->
                    cl.show(jPanel11, "card-comboCol1");
                case "Real" ->
                    cl.show(jPanel11, "card-text1");
            }

            CombolistModelValues1.removeAllElements();
            jComboBox11.removeAllItems();
            switch (variable.getUniverse().getType()) {
                case "Enum (Scalar)" -> {
                    String[] values = variable.getUniverse().getValueEnum().split(",");
                    for (String value : values) {
                        CombolistModelValues1.addElement(value.trim());
                    }
                }
                case "Enum (Collection)" -> {
                    String[] values = variable.getUniverse().getValueEnum().split(",");
                    for (String value : values) {
                        CheckComboItem item = new CheckComboItem(value.trim());
                        item.setSelected(false);
                        jComboBox11.addItem(item);
                    }
                }
                case "Bool" -> {
                    CombolistModelValues1.addElement("True");
                    CombolistModelValues1.addElement("False");
                }
            }

            jComboBox15.removeAllItems();
            ArrayList<Function> functions = variable.getUniverse().getFunctions();
            for (Function function : functions) {
                jComboBox15.addItem(function.toString().trim());
            }
            jComboBox15.setSelectedIndex(-1);
            jRadioButton10.setSelected(true);
            if (functions.isEmpty()) {
                jRadioButton11.setEnabled(false);
            } else {
                jRadioButton11.setEnabled(true);
            }
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * Handles the Edit/Cancel toggle for Constructs.
     * <p>
     * When entering edit mode, it loads the selected {@link Construct} into the
     * form fields and switches the buttons to <em>Cancel</em>/<em>Save</em>.
     * When canceling, it resets the form fields and returns to
     * <em>Edit</em>/<em>Add</em> mode.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        if (jButton23.getText().equals("Mode add")) {
            jButton23.setText("Mode edit");
            jButton13.setText("Add");
            selectedIndexConstruct = -1;
            jTextField6.setText("");
            jTextField7.setText("");
            jTextField8.setText("");
            jList3.setSelectedIndex(-1);
        } else {
            selectedIndexConstruct = jList3.getSelectedIndex();
            if (selectedIndexConstruct != -1) {
                Construct construct = colConstruct.getModelElement().elementAt(selectedIndexConstruct);
                jTextField6.setText(construct.getName());
                jTextField7.setText(construct.getFrom());
                jTextField8.setText(construct.getScope());
                jButton23.setText("Mode add");
                jButton13.setText("Save");
                jLabel17.setText("");
            } else {
                jLabel17.setText("<html>Select an item to edit.</html>");
            }
        }
    }//GEN-LAST:event_jButton23ActionPerformed

    /**
     * Handles the Edit/Cancel toggle for Universes.
     * <p>
     * When entering edit mode, it loads the selected {@link Universe} into the
     * form fields (including type-specific fields and allowed relations) and
     * switches the buttons to <em>Cancel</em>/<em>Save</em>. When canceling, it
     * clears the universe form and restores default values.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        if (jButton24.getText().equals("Mode add")) {
            jButton24.setText("Mode edit");
            jButton18.setText("Add");
            selectedIndexUniverse = -1;
            jTextField9.setText("");

            unSelectedFunctions(jComboBox8);

            jTextField12.setText("");
            jTextField13.setText("");
            jTextField14.setText("");
            jCheckBox3.setSelected(true);
            jCheckBox4.setSelected(false);
            jCheckBox5.setSelected(false);
            jCheckBox6.setSelected(false);
            jCheckBox7.setSelected(false);
            jCheckBox8.setSelected(false);
            jComboBox5.setSelectedIndex(0);
            jList4.setSelectedIndex(-1);
        } else {
            selectedIndexUniverse = jList4.getSelectedIndex();
            if (selectedIndexUniverse != -1) {
                Universe universe = colUniverse.getModelElement().elementAt(selectedIndexUniverse);

                jTextField9.setText(universe.getName());
                setSelectedFunctions(jComboBox8, universe.getFunctions());
                switch (universe.getType()) {
                    case "Enum (Scalar)" -> {
                        jComboBox5.setSelectedIndex(0);
                        jTextField12.setText(universe.getValueEnum());
                        jTextField13.setText("");
                        jTextField14.setText("");
                    }
                    case "Enum (Collection)" -> {
                        jComboBox5.setSelectedIndex(1);
                        jTextField12.setText(universe.getValueEnum());
                        jTextField13.setText("");
                        jTextField14.setText("");

                    }
                    case "Real" -> {
                        jComboBox5.setSelectedIndex(2);
                        jTextField12.setText("");
                        jTextField13.setText(String.valueOf(universe.getValueMin()));
                        jTextField14.setText(String.valueOf(universe.getValueMax()));

                    }
                    case "Bool" -> {
                        jComboBox5.setSelectedIndex(3);
                        jTextField12.setText("");
                        jTextField13.setText("");
                        jTextField14.setText("");
                    }
                }
                jCheckBox3.setSelected(universe.isEqual());
                jCheckBox4.setSelected(universe.isGreater());
                jCheckBox5.setSelected(universe.isGreater_equal());
                jCheckBox6.setSelected(universe.isNot_equal());
                jCheckBox7.setSelected(universe.isLess());
                jCheckBox8.setSelected(universe.isLess_equal());

                jButton24.setText("Mode add");
                jButton18.setText("Save");
                jLabel21.setText("");
            } else {
                jLabel21.setText("<html>Select an item to edit.</html>");
            }
        }
    }//GEN-LAST:event_jButton24ActionPerformed

    /**
     * Handles the Edit/Cancel toggle for Variables.
     * <p>
     * When entering edit mode, it loads the selected {@link Variable} into the
     * form fields (name, nickname, construct and universe) and switches the
     * buttons to <em>Cancel</em>/<em>Save</em>. When canceling, it clears the
     * form and restores <em>Edit</em>/<em>Add</em> mode.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        if (jButton25.getText().equals("Mode add")) {
            jButton25.setText("Mode edit");
            jButton1.setText("Add");
            selectedIndexVariable = -1;
            jTextField1.setText("");
            jTextField2.setText("");
            jComboBox6.setSelectedIndex(0);
            jComboBox7.setSelectedIndex(0);
            jList1.setSelectedIndex(-1);
        } else {
            selectedIndexVariable = jList1.getSelectedIndex();
            if (selectedIndexVariable != -1) {
                Variable variable = colVariable.getModelElement().elementAt(selectedIndexVariable);
                jTextField1.setText(variable.getName());
                jTextField2.setText(variable.getNickname());
                jComboBox6.setSelectedItem(variable.getConstruct().getName());
                jComboBox7.setSelectedItem(variable.getUniverse().getName());
                jButton25.setText("Mode add");
                jButton1.setText("Save");
                jLabel3.setText("");
            } else {
                jLabel3.setText("<html>Select an item to edit.</html>");
            }
        }
    }//GEN-LAST:event_jButton25ActionPerformed

    /**
     * Handles the Edit/Cancel toggle for Implications.
     * <p>
     * When entering edit mode, it loads the selected {@link Implication} into
     * the literal controls (variables, relations, values and negation flags)
     * and switches the buttons to <em>Cancel</em>/<em>Save</em>. When
     * canceling, it resets all implication controls to their default state.
     * </p>
     *
     * @param evt the Swing action event triggered by clicking the button
     */
    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        if (jButton26.getText().equals("Mode add")) {
            jButton26.setText("Mode edit");
            jButton6.setText("Add");
            selectedIndexImplication = -1;
            jComboBox1.setSelectedIndex(0);
            jComboBox2.setSelectedIndex(0);
            jComboBox3.setSelectedIndex(0);
            jComboBox4.setSelectedIndex(0);
            jComboBox10.setSelectedIndex(0);
            jComboBox12.setSelectedIndex(0);
            jCheckBox1.setSelected(false);
            jCheckBox2.setSelected(false);
            jList2.setSelectedIndex(-1);
            jList5.setSelectedIndex(-1);
            jRadioButton10.setSelected(true);
            jRadioButton12.setSelected(true);
        } else {
            selectedIndexImplication = jList2.getSelectedIndex();
            if (selectedIndexImplication != -1) {

                Implication implication = colImplication.getModelElement().elementAt(selectedIndexImplication);

                jComboBox1.setSelectedItem(implication.getVariable1().getNickname());
                jComboBox2.setSelectedItem(implication.getRelation1());
                jComboBox3.setSelectedItem(implication.getVariable2().getNickname());
                jComboBox4.setSelectedItem(implication.getRelation2());
                if (implication.isValueFunction1()) {
                    switch (implication.getVariable1().getUniverse().getType()) {
                        case "Enum (Scalar)", "Bool" ->
                            jComboBox10.setSelectedItem(implication.getValue1().trim());
                        case "Enum (Collection)" -> {
                            String[] partsVal = implication.getValue1().substring(1, implication.getValue1().length() - 1).split(",");
                            setSelectedItems(jComboBox11, partsVal);
                        }
                        case "Real" ->
                            jTextField4.setText(implication.getValue1());
                        default ->
                            throw new AssertionError();
                    }
                    jRadioButton10.setSelected(true);
                } else {
                    jComboBox15.setSelectedItem(implication.getValue1().trim());
                    jRadioButton11.setSelected(true);
                    CardLayout cl = (CardLayout) jPanel11.getLayout();
                    cl.show(jPanel11, "card-comboFunc1");
                }

                if (implication.isValueFunction2()) {
                    switch (implication.getVariable2().getUniverse().getType()) {
                        case "Enum (Scalar)", "Bool" ->
                            jComboBox12.setSelectedItem(implication.getValue2().trim());
                        case "Enum (Collection)" -> {
                            String[] partsVal = implication.getValue2().substring(1, implication.getValue2().length() - 1).split(",");
                            setSelectedItems(jComboBox13, partsVal);
                        }
                        case "Real" ->
                            jTextField15.setText(implication.getValue2());
                        default ->
                            throw new AssertionError();
                    }
                    jRadioButton12.setSelected(true);
                } else {
                    jComboBox14.setSelectedItem(implication.getValue2().trim());
                    jRadioButton13.setSelected(true);
                    CardLayout cl = (CardLayout) jPanel18.getLayout();
                    cl.show(jPanel18, "card-comboFunc2");
                }
                jCheckBox1.setSelected(implication.isNegated1());
                jCheckBox2.setSelected(implication.isNegated2());
                jButton26.setText("Mode add");
                jButton6.setText("Save");
                jLabel11.setText("");
            } else {
                jLabel11.setText("<html>Select an item to edit.</html>");
            }
        }
    }//GEN-LAST:event_jButton26ActionPerformed

    /**
     * Handles the Add button on the Functions tab.
     * <p>
     * Validates name and arity, prevents duplicates, and adds/edits the
     * function in the corresponding collection.
     * </p>
     *
     * @param evt the Swing action event
     */
    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        if (jTextField17.getText().isEmpty()) {
            jTextField17.grabFocus();
            jLabel35.setText("<html>You cannot leave the name function blank.</html>");
        } else if ("Add".equals(jButton27.getText()) && colFunction.containsName(jTextField17.getText())) {
            jTextField17.grabFocus();
            jLabel35.setText("<html>You cannot add duplicate names.</html>");
        } else if (jTextField16.getText().isEmpty()) {
            jTextField16.grabFocus();
            jLabel35.setText("<html>You cannot leave the aridad of the function blank.</html>");
        } else {
            int aridad = 0;
            try {
                aridad = Integer.parseUnsignedInt(jTextField16.getText());
            } catch (NumberFormatException e) {
                jTextField16.grabFocus();
                jLabel35.setText("<html>The aridad must be a positive number.</html>");
                return;
            }
            Function function = new Function(jTextField17.getText().trim(), aridad);
            if ("Add".equals(jButton27.getText())) {
                colFunction.add(function);
                jLabel35.setText("<html>The function has been added successfully.</html>");
            } else {
                colFunction.set(selectedIndexFunctions, function);
                jLabel35.setText("<html>The function has been edited successfully.</html>");
                jButton29.setText("Mode edit");
                jButton27.setText("Add");
            }
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton27ActionPerformed

    /**
     * Handles the Delete button on the Functions tab.
     * <p>
     * Removes the currently selected function when a selection exists.
     * </p>
     *
     * @param evt the Swing action event
     */
    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        int selectedIndex = jList6.getSelectedIndex();
        if (selectedIndex != -1) {
            colFunction.remove(selectedIndex);
        } else {
            jLabel35.setText("<html>Select an item to delete.</html>");
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton28ActionPerformed

    /**
     * Toggles edit/add mode on the Functions tab.
     * <p>
     * Loads the selected function for editing, or cancels editing and clears
     * the form.
     * </p>
     *
     * @param evt the Swing action event
     */
    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        if (jButton29.getText().equals("Mode add")) {
            jButton29.setText("Mode edit");
            jButton27.setText("Add");
            selectedIndexFunctions = -1;
            jList6.setSelectedIndex(-1);
        } else {
            selectedIndexFunctions = jList6.getSelectedIndex();
            if (selectedIndexFunctions != -1) {
                Function function = colFunction.getModelElement().elementAt(selectedIndexFunctions);
                jTextField17.setText(function.getName());
                jTextField16.setText(Integer.toString(function.getAridad()));
                jButton29.setText("Mode add");
                jButton27.setText("Save");
                jLabel35.setText("");
            } else {
                jLabel35.setText("<html>Select an item to edit.</html>");
            }
        }
    }//GEN-LAST:event_jButton29ActionPerformed

    /**
     * Moves the selected function one position up in the list.
     *
     * @param evt the Swing action event
     */
    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        int selectedIndex = jList6.getSelectedIndex();
        if (selectedIndex != -1) {
            if (colFunction.indexUp(selectedIndex)) {
                jList6.setSelectedIndex(selectedIndex - 1);
                jLabel35.setText("<html>Item moved.</html>");
            } else {
                jLabel35.setText("<html>The selected item cannot be moved up further.</html>");
            }
        } else {
            jLabel35.setText("<html>Select an item to move up.</html>");
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton30ActionPerformed

    /**
     * Moves the selected function one position down in the list.
     *
     * @param evt the Swing action event
     */
    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        int selectedIndex = jList6.getSelectedIndex();
        if (selectedIndex != -1) {
            if (colFunction.indexDown(selectedIndex)) {
                jList6.setSelectedIndex(selectedIndex + 1);
                jLabel35.setText("<html>Item moved.</html>");
            } else {
                jLabel35.setText("<html>The selected item cannot be moved down further.</html>");
            }
        } else {
            jLabel35.setText("<html>Select an item to move down.</html>");
        }
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton31ActionPerformed

    /**
     * Clears the entire functions list.
     *
     * @param evt the Swing action event
     */
    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        colFunction.empty();
        jLabel35.setText("<html>The list has been cleared successfully.</html>");
        utils.emptyMatriz();
    }//GEN-LAST:event_jButton32ActionPerformed

    /**
     * Selects Value mode for literal 1 and shows the appropriate editor
     * according to the universe type (combo box, multi-select combo or numeric
     * field).
     *
     * @param evt the Swing action event
     */
    private void jRadioButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton10ActionPerformed
        Variable variable = colVariable.find((String) jComboBox1.getSelectedItem(), Variable::getNickname);
        if (variable != null) {

            CardLayout cl = (CardLayout) jPanel11.getLayout();

            switch (variable.getUniverse().getType()) {
                case "Enum (Scalar)", "Bool" ->
                    cl.show(jPanel11, "card-combo1");
                case "Enum (Collection)" ->
                    cl.show(jPanel11, "card-comboCol1");
                case "Real" ->
                    cl.show(jPanel11, "card-text1");
            }
        }
    }//GEN-LAST:event_jRadioButton10ActionPerformed

    /**
     * Selects Function mode for literal 1 and shows the functions selector.
     *
     * @param evt the Swing action event
     */
    private void jRadioButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton11ActionPerformed
        CardLayout cl = (CardLayout) jPanel11.getLayout();
        cl.show(jPanel11, "card-comboFunc1");
    }//GEN-LAST:event_jRadioButton11ActionPerformed

    /**
     * Selects Value mode for literal 2 and shows the appropriate editor
     * according to the universe type (combo box, multi-select combo or numeric
     * field).
     *
     * @param evt the Swing action event
     */
    private void jRadioButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton12ActionPerformed
        Variable variable = colVariable.find((String) jComboBox3.getSelectedItem(), Variable::getNickname);
        if (variable != null) {

            CardLayout cl = (CardLayout) jPanel18.getLayout();

            switch (variable.getUniverse().getType()) {
                case "Enum (Scalar)", "Bool" ->
                    cl.show(jPanel18, "card-combo2");
                case "Enum (Collection)" ->
                    cl.show(jPanel18, "card-comboCol2");
                case "Real" ->
                    cl.show(jPanel18, "card-text2");
            }
        }
    }//GEN-LAST:event_jRadioButton12ActionPerformed

    /**
     * Selects Function mode for literal 2 and shows the functions selector.
     *
     * @param evt the Swing action event
     */
    private void jRadioButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton13ActionPerformed
        CardLayout cl = (CardLayout) jPanel18.getLayout();
        cl.show(jPanel18, "card-comboFunc2");
    }//GEN-LAST:event_jRadioButton13ActionPerformed

    /**
     * Shows/hides the contrapositive implications list and resizes the main
     * implications list accordingly.
     * <p>
     * When enabled, the UI shows two side-by-side lists: original implications
     * and their contrapositives.
     * </p>
     *
     * @param evt the Swing action event
     */
    private void jCheckBox9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox9ActionPerformed
        jScrollPane7.setVisible(jCheckBox9.isSelected());

        if (jCheckBox9.isSelected()) {
            jScrollPane2.setPreferredSize(new Dimension(485, 224));
        } else {
            jScrollPane2.setPreferredSize(new Dimension(485, 490));
        }
        jScrollPane2.revalidate();
        jScrollPane2.getParent().revalidate();
        jScrollPane2.repaint();
    }//GEN-LAST:event_jCheckBox9ActionPerformed

    /**
     * Handles changes in the model version selection combo box.
     * <p>
     * Clears the Generation tab list models, resets cached matrices/nodes in
     * {@link GUI.utils}, and triggers a full model regeneration so that the UI
     * reflects the newly selected generation/restoration algorithm.
     * </p>
     *
     * @param evt the Swing action event triggered by changing the combo box selection
     */
    private void jComboBox9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox9ActionPerformed
        listModelGeneration.clear();
        listModelGenerationCR.clear();
        utils.emptyMatriz();
        getInfoModelo();
    }//GEN-LAST:event_jComboBox9ActionPerformed

    /**
     * Opens a new window showing the selected model matrix as a table.
     *
     * @param matriz the adjacency or implication matrix of the selected model
     * @param nodos the list of node labels corresponding to the matrix indices
     */
    private void representacionTipoTabla(int[][] matriz, List<String> nodos) {
        VentanaTabla ventanaTabla = new VentanaTabla(matriz, nodos);
    }

    /**
     * Appends a LaTeX representation of the selected model matrix to the
     * information text area.
     *
     * @param matriz the adjacency or implication matrix of the selected model
     * @param nodos the list of node labels corresponding to the matrix indices
     */
    private void representacionTipoLatex(int[][] matriz, List<String> nodos) {
        String msg = "";//jTextArea1.getText();
        //msg += "\n\n-----------------------------------";
        //msg += "\nMODEL MATRIX IN LATEX FORMAT";
        //msg += "\n------------------------------------";
        //msg += "\n\n";
        msg += toLatex(matriz);
        //jTextArea1.setText(msg);
        VentanaLatex ventanaLatex = new VentanaLatex(msg);
    }

    /**
     * Opens a new window showing the selected model as a graph.
     *
     * @param matriz the adjacency or implication matrix of the selected model
     * @param nodos the list of node labels corresponding to the matrix indices
     */
    private void representacionTipoGrafo(int[][] matriz, List<String> nodos) {
        dibujaGrafos DG = new dibujaGrafos();
        Viewer viewer = DG.dibujarView(matriz, nodos);
        VentanaGrafo ventanaGrafo = new VentanaGrafo(viewer);
    }

    /**
     * Exports the selected model matrix as a CSV file.
     *
     * @param matriz the adjacency or implication matrix of the selected model
     * @param nodos the list of node labels corresponding to the matrix indices
     */
    private void representacionTipoExcel(int[][] matriz, List<String> nodos) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Files with extension (*.csv)", "csv");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getName();
            if (!fileName.endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }
            String content = utils.generarTablaCSV(matriz, nodos);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "File saved at: " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }

    /**
     * Exports the selected model matrix as a List TXT file of Implications.
     *
     */
    private void representacionTipoTxt(int[][] matriz, List<String> nodos) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save TXT File");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Files with extension (*.txt)", "txt");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getName();
            if (!fileName.endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
            }
            String content = "--- Implications ---\n\n";

            for (Object element : listModelGeneration.toArray()) {
                content += element.toString() + "\n";
            }

            if (jRadioButton9.isSelected()) {
                content += "\n\n--- Delete implications ---\n\n";

                for (Object element : listModelGenerationCR.toArray()) {
                    String[] nodosArista = dividirAristaEnNodos(element.toString());
                    String camino = caminoMinimo(matriz, nodos, nodosArista[0], nodosArista[1]);
                    //System.out.println(nodosArista[0] + " - " + nodosArista[1]);
                    content += element.toString();
                    if (camino != null && !camino.isEmpty()) {
                        content += "  ===> (" + camino + ")\n";
                    } else {
                        content += " (Error:: Sin camino)\n";
                    }

                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "File saved at: " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }

    /**
     * Returns the matrix corresponding to the model stage selected by the radio
     * buttons on the Generation tab.
     * <p>
     * If the required matrix is not available or the selection is inconsistent
     * with the presence of cycles, a descriptive dialog is shown and
     * {@code null} is returned.
     * </p>
     *
     * @return the selected matrix, or {@code null} if it cannot be obtained
     */
    private int[][] getMatrizSeleccionada() {
        if (jRadioButton5.isSelected()) {
            if (!utils.getListMatrices().isEmpty()) {
                return utils.getListMatrices().get(0);
            } else {
                JOptionPane.showMessageDialog(this, "Model generation error: the selected matrix (0) was not generated.");
                return null;
            }
        } else if (jRadioButton6.isSelected()) {
            if (!utils.tieneCiclos()) {
                JOptionPane.showMessageDialog(this, "Invalid model selection: the model has no cycles.");
                return null;
            } else if (utils.getListMatrices().size() > 1) {
                return utils.getListMatrices().get(1);
            } else {
                JOptionPane.showMessageDialog(this, "Model generation error: the selected matrix (1) was not generated.");
                return null;
            }
        } else if (jRadioButton7.isSelected()) {
            if (!utils.tieneCiclos()) {
                if (utils.getListMatrices().size() > 1) {
                    return utils.getListMatrices().get(1);
                } else {
                    JOptionPane.showMessageDialog(this, "Model generation error: the selected matrix (1) was not generated.");
                    return null;
                }
            } else {
                if (utils.getListMatrices().size() > 2) {
                    return utils.getListMatrices().get(2);
                } else {
                    JOptionPane.showMessageDialog(this, "Model generation error: the selected matrix (2) was not generated.");
                    return null;
                }
            }
        } else if (jRadioButton8.isSelected()) {
            if (!utils.tieneCiclos()) {
                if (utils.getListMatrices().size() > 2) {
                    return utils.getListMatrices().get(2);
                } else {
                    JOptionPane.showMessageDialog(this, "Model generation error: the selected matrix (3) was not generated.");
                    return null;
                }
            } else {
                if (utils.getListMatrices().size() > 3) {
                    return utils.getListMatrices().get(3);
                } else {
                    JOptionPane.showMessageDialog(this, "Model generation error: the selected matrix (3) was not generated.");
                    return null;
                }
            }
        } else {
            if (!utils.tieneCiclos()) {
                JOptionPane.showMessageDialog(this, "Invalid model selection: the model has no cycles.");
                return null;
            } else if (utils.getListMatrices().size() > 4) {
                return utils.getListMatrices().get(4);
            } else {
                JOptionPane.showMessageDialog(this, "Model generation error: the selected matrix (4) was not generated.");
                return null;
            }
        }
    }

    /**
     * Returns the list of node labels for the selected model stage.
     * <p>
     * Depending on the presence of cycles and the selected radio button, this
     * method returns either the full node list or the reduced node list. When
     * nodes cannot be generated, a dialog is shown and {@code null} is
     * returned.
     * </p>
     *
     * @return the list of node labels, or {@code null} if it cannot be obtained
     */
    private List<String> getNodosSeleccionados() {
        if (jRadioButton5.isSelected() || jRadioButton9.isSelected()) {
            if (!utils.getNodos().isEmpty()) {
                return utils.getNodos();
            } else {
                JOptionPane.showMessageDialog(this, "Model generation error: the selected matrix (0,4) did not generate the nodes.");
                return null;
            }
        } else if (jRadioButton6.isSelected()) {
            if (!utils.tieneCiclos()) {
                JOptionPane.showMessageDialog(this, "Invalid model selection: the model has no cycles.");
                return null;
            } else if (!utils.getNodosReducidos().isEmpty()) {
                return utils.getNodosReducidos();
            } else {
                JOptionPane.showMessageDialog(this, "Model generation error: the selected matrix (1) did not generate the nodes.");
                return null;
            }
        } else if (jRadioButton7.isSelected() || jRadioButton8.isSelected()) {
            if (!utils.tieneCiclos()) {
                if (!utils.getNodos().isEmpty()) {
                    return utils.getNodos();
                } else {
                    JOptionPane.showMessageDialog(this, "Model generation error: the selected matrix (2,3) did not generate the nodes.");
                    return null;
                }
            } else {
                if (!utils.getNodosReducidos().isEmpty()) {
                    return utils.getNodosReducidos();
                } else {
                    JOptionPane.showMessageDialog(this, "Model generation error: the selected matrix (2,3) did not generate the nodes.");
                    return null;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Model selection error: unknown selection.");
            return null;
        }
    }

    /**
     * Refreshes combo boxes on the Universes tab when the tab becomes active.
     * <p>
     * Populates the functions combo box models with the current elements stored
     * in {@link #colFunction}.
     * </p>
     */
    private void eventoTabUniverses() {
        CombolistModelFunctions.removeAllElements();
        DefaultListModel<Function> listModelFunctions = colFunction.getModelElement();
        for (int i = 0; i < listModelFunctions.size(); i++) {
            CheckComboItem item = new CheckComboItem(listModelFunctions.get(i).toString().trim());
            item.setSelected(false);
            CombolistModelFunctions.addElement(item);
        }
    }

    /**
     * Refreshes combo boxes on the Variables tab when the tab becomes active.
     * <p>
     * Populates the construct and universe combo box models with the current
     * elements stored in {@link #colConstruct} and {@link #colUniverse}.
     * </p>
     */
    private void eventoTabVariables() {
        CombolistModelConstructs.removeAllElements();
        DefaultListModel<Construct> listModelConstructs = colConstruct.getModelElement();
        for (int i = 0; i < listModelConstructs.size(); i++) {
            CombolistModelConstructs.addElement(listModelConstructs.get(i).getName());
        }
        CombolistModelUniverses.removeAllElements();
        DefaultListModel<Universe> listModelUniverses = colUniverse.getModelElement();
        for (int i = 0; i < listModelUniverses.size(); i++) {
            CombolistModelUniverses.addElement(listModelUniverses.get(i).getName());
        }
        utils.emptyMatriz();
    }

    /**
     * Refreshes combo boxes on the Implications tab when the tab becomes
     * active.
     * <p>
     * Populates the alias combo box models with the nicknames of all defined
     * variables.
     * </p>
     */
    private void eventoTabImplicaciones() {
        CombolistModelAlias1.removeAllElements();
        CombolistModelAlias2.removeAllElements();
        DefaultListModel<Variable> listModelVariable = colVariable.getModelElement();
        for (int i = 0; i < listModelVariable.size(); i++) {
            CombolistModelAlias1.addElement(listModelVariable.get(i).getNickname());
            CombolistModelAlias2.addElement(listModelVariable.get(i).getNickname());
        }

        ordenarDefaultComboBoxModel(CombolistModelAlias1);
        ordenarDefaultComboBoxModel(CombolistModelAlias2);

        utils.emptyMatriz();
    }

    /**
     * Prepares the Generation tab by computing all model matrices and enabling
     * or disabling the available model stages according to the presence of
     * cycles.
     */
    private void eventoTabGeneracion() {
        List<int[][]> matrices = getInfoModelo();
        if (matrices != null && !matrices.isEmpty()) {
            jRadioButton1.setSelected(true);
            jRadioButton5.setSelected(true);
            jButton11.setEnabled(true);
            jRadioButton1.setEnabled(true);
            jRadioButton2.setEnabled(true);
            jRadioButton3.setEnabled(true);
            jRadioButton4.setEnabled(true);
            //_______________________________
            jRadioButton5.setEnabled(true);
            jRadioButton7.setEnabled(true);
            jRadioButton8.setEnabled(true);
            if (utils.tieneCiclos()) {
                jRadioButton6.setEnabled(true);
                jRadioButton9.setEnabled(true);
                jLabel7.setVisible(true);
                jComboBox9.setVisible(true);
            } else {
                jRadioButton6.setEnabled(false);
                jRadioButton9.setEnabled(false);
                jLabel7.setVisible(false);
                jComboBox9.setVisible(false);
            }
        } else {
            jButton11.setEnabled(false);
            jRadioButton1.setEnabled(false);
            jRadioButton2.setEnabled(false);
            jRadioButton3.setEnabled(false);
            jRadioButton4.setEnabled(false);
            //_______________________________
            jRadioButton5.setEnabled(false);
            jRadioButton6.setEnabled(false);
            jRadioButton7.setEnabled(false);
            jRadioButton8.setEnabled(false);
            jRadioButton9.setEnabled(false);
            jLabel7.setVisible(false);
            jComboBox9.setVisible(false);
        }
    }

    /**
     * Computes information about the current model and generates all
     * transformation matrices.
     * <p>
     * The method fills the information text area with summary statistics and
     * returns the list of generated matrices. If an error occurs, an
     * informative dialog is shown and the list may be {@code null} or empty.
     * </p>
     *
     * @return the list of generated matrices, or {@code null}/empty if
     * generation fails
     */
    private List<int[][]> getInfoModelo() {
        String msg;
        List<int[][]> matrices = utils.generarMatrices(listModelLiterales1, listModelLiterales2,
                listModelImplicacionesNot1, listModelImplicacionesNot2,
                jComboBox9.getSelectedIndex());
        if (matrices != null && !matrices.isEmpty()) {

            printInfoModelo(matrices);

            String error = utils.getError();
            if (error != null) {
                String msg1 = "An error occurred while generating the model : " + error;
                msg1 += "\n\nThe shown information may not be accurate.";
                JOptionPane.showMessageDialog(this, msg1, "Model generation error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            msg = "The model does not have enough content to be evaluated";
            JOptionPane.showMessageDialog(this, msg, "Model generation error", JOptionPane.ERROR_MESSAGE);
        }
        return matrices;
    }

    /**
     * Writes a small model summary into {@link #jTextArea1}.
     * <p>
     * The summary includes whether cycles exist, the number of variables,
     * literals and initial implications.
     * </p>
     *
     * @param matrices list of generated model matrices; the first matrix
     *                 ({@code matrices.get(0)}) is used to count the initial
     *                 implications
     */
    private void printInfoModelo(List<int[][]> matrices) {
        String msg;
        msg = "The model contains cycles: " + (utils.tieneCiclos() ? "YES" : "NO");
        msg += "\nNumber of variables: " + colVariable.size();
        msg += "\nNumber of literals: " + utils.getNodos().size();
        msg += "\nNumber of initial implications: " + (utils.getNumeroAristas(matrices.get(0)) / 2);
        //msg += "\nNumber of final implications: " + utils.getNumeroAristas(matrices.get(matrices.size() - 1));
        jTextArea1.setText(msg);
    }

    /**
     * Updates (or appends) the "Number of final implications" line in the text
     * area summary.
     *
     * @param val value to show for the final implication count
     */
    private void updateInfoModelo(String val) {
        String msg = jTextArea1.getText();
        String new_msg = "\nNumber of final implications: ";

        if (!msg.contains(new_msg)) {
            msg += "\nNumber of final implications: " + val;
        }else{
            msg = msg.substring(0, msg.lastIndexOf(':')) + ": " + val;
        }
        jTextArea1.setText(msg);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox10;
    private javax.swing.JComboBox<CheckComboItem> jComboBox11;
    private javax.swing.JComboBox<String> jComboBox12;
    private javax.swing.JComboBox<CheckComboItem> jComboBox13;
    private javax.swing.JComboBox<String> jComboBox14;
    private javax.swing.JComboBox<String> jComboBox15;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JComboBox<CheckComboItem> jComboBox8;
    private javax.swing.JComboBox<String> jComboBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<String> jList4;
    private javax.swing.JList<String> jList5;
    private javax.swing.JList<String> jList6;
    private javax.swing.JList<String> jList7;
    private javax.swing.JList<String> jList8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton12;
    private javax.swing.JRadioButton jRadioButton13;
    private javax.swing.JRadioButton jRadioButton14;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
