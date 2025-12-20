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
 *
 * @author Sergio
 */
public class CheckComboRenderer implements ListCellRenderer<CheckComboItem> {

    @Override
    public Component getListCellRendererComponent(JList<? extends CheckComboItem> list,
                                                  CheckComboItem value,
                                                  int index, boolean isSelected,
                                                  boolean cellHasFocus) {

        JCheckBox check = new JCheckBox(value.toString());
        check.setSelected(value.isSelected());
        check.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        check.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        return check;
    }
}
