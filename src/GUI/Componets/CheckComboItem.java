/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI.Componets;

/**
 * Elemento para combos con selección múltiple mediante checkbox.
 * <p>
 * Mantiene una etiqueta visible y un estado de selección.
 * </p>
 */
public class CheckComboItem {
    private final String label;
    private boolean selected;

    /**
     * Crea un nuevo item checkeable con la etiqueta indicada.
     *
     * @param label texto a mostrar en el combo
     */
    public CheckComboItem(String label) {
        this.label = label;
    }

    /**
     * Indica si el elemento está seleccionado.
     *
     * @return {@code true} si está seleccionado; en caso contrario {@code false}
     */
    public boolean isSelected() { return selected; }

    /**
     * Establece el estado de selección del elemento.
     *
     * @param selected {@code true} para seleccionar; {@code false} para deseleccionar
     */
    public void setSelected(boolean selected) { this.selected = selected; }

    /**
     * Devuelve la etiqueta visible del elemento.
     *
     * @return la etiqueta de texto
     */
    @Override
    public String toString() { return label; }
}
