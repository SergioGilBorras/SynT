/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI.Componets;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Renderer para mostrar elementos {@link CheckComboItem} como casillas
 * seleccionables dentro de un JList/Combo.
 */
public class CheckComboRenderer implements ListCellRenderer<CheckComboItem> {

    /**
     * Construye el componente visual para un item del combo/lista.
     *
     * @param list la lista que se está pintando
     * @param value el valor del elemento (CheckComboItem)
     * @param index índice del elemento en la lista
     * @param isSelected si la celda está seleccionada (en la lista)
     * @param cellHasFocus si la celda tiene foco
     * @return un JCheckBox configurado con el texto y selección del item
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends CheckComboItem> list,
                                                  CheckComboItem value,
                                                  int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        if (value == null) {
            return null;
        }
        JCheckBox check = new JCheckBox(value.toString());
        check.setSelected(value.isSelected());
        check.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        check.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        return check;
    }
}
