/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI.Componets;

/**
 *
 * @author Sergio
 */

public class CheckComboItem {
    private String label;
    private boolean selected;

    public CheckComboItem(String label) {
        this.label = label;
    }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public String toString() { return label; }
}
