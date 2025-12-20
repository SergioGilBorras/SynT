/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.function.Function;
import javax.swing.DefaultListModel;

/**
 * Generic collection wrapper used to manage model elements and their
 * corresponding string representations for Swing list components.
 * <p>
 * Internally it keeps two synchronized {@link DefaultListModel} instances:
 * one for the actual elements and another for the strings shown in the GUI.
 * </p>
 *
 * @param <E> the element type, which must implement {@link Element}
 */
public class ColElements<E extends Element> {

    /**
     * List model containing the actual element instances.
     */
    private final DefaultListModel<E> listModel;
    /**
     * List model containing the string representation of each element.
     */
    private final DefaultListModel<String> listModelString;

    /**
     * Creates an empty collection of elements.
     */
    public ColElements() {
        this.listModel = new DefaultListModel<>();
        this.listModelString = new DefaultListModel<>();
    }

    /**
     * Returns the list model with the string representations of the elements.
     *
     * @return the string list model
     */
    public DefaultListModel<String> getModel() {
        return listModelString;
    }

    /**
     * Returns the list model with the actual element instances.
     *
     * @return the element list model
     */
    public DefaultListModel<E> getModelElement() {
        return listModel;
    }

    /**
     * Returns the number of elements in this collection.
     *
     * @return the current size of the collection
     */
    public int size() {
        return this.listModel.size();
    }

    /**
     * Adds a new element to the collection and its string representation to
     * the parallel list model.
     *
     * @param element the element to add
     */
    public void add(E element) {
        this.listModel.addElement(element);
        this.listModelString.addElement(element.toString());
    }

    /**
     * Removes the element at the given index from both internal list models.
     *
     * @param index the position of the element to remove
     */
    public void remove(int index) {
        this.listModel.remove(index);
        this.listModelString.remove(index);
    }

    /**
     * Removes all elements from this collection.
     */
    public void empty() {
        this.listModel.removeAllElements();
        this.listModelString.removeAllElements();
    }

    /**
     * Moves the element at the given index one position up in the list.
     *
     * @param selectedIndex the index of the element to move
     * @return {@code true} if the element was moved; {@code false} if it was
     *         already at the top
     */
    public boolean indexUp(int selectedIndex) {
        if (selectedIndex > 0) {
            E selectedConstruct = this.listModel.get(selectedIndex);
            this.listModel.set(selectedIndex, this.listModel.get(selectedIndex - 1));
            this.listModel.set(selectedIndex - 1, selectedConstruct);

            String selectedConstructString = this.listModelString.get(selectedIndex);
            this.listModelString.set(selectedIndex, this.listModelString.get(selectedIndex - 1));
            this.listModelString.set(selectedIndex - 1, selectedConstructString);
            return true;
        }
        return false;
    }

    /**
     * Moves the element at the given index one position down in the list.
     *
     * @param selectedIndex the index of the element to move
     * @return {@code true} if the element was moved; {@code false} if it was
     *         already at the bottom
     */
    public boolean indexDown(int selectedIndex) {
        if (selectedIndex < this.listModel.getSize() - 1) {
            E selectedConstruct = this.listModel.get(selectedIndex);
            this.listModel.set(selectedIndex, this.listModel.get(selectedIndex + 1));
            this.listModel.set(selectedIndex + 1, selectedConstruct);

            String selectedConstructString = this.listModelString.get(selectedIndex);
            this.listModelString.set(selectedIndex, this.listModelString.get(selectedIndex + 1));
            this.listModelString.set(selectedIndex + 1, selectedConstructString);
            return true;
        }
        return false;
    }

    /**
     * Checks whether the collection contains an element with the given name.
     *
     * @param Name the name to look for
     * @return {@code true} if an element with that name exists; {@code false}
     *         otherwise
     */
    public boolean containsName(String Name) {
        for (int i = 0; i < this.listModel.getSize(); i++) {
            E element = this.listModel.getElementAt(i);
            if (element.getName().equals(Name.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Searches for an element with the given name.
     *
     * @param Name the name to search
     * @return the first element whose name matches, or {@code null} if none is
     *         found
     */
    public E findName(String Name) {
        for (int i = 0; i < this.listModel.getSize(); i++) {
            E element = this.listModel.getElementAt(i);
            if (element.getName().equals(Name.trim())) {
                return element;
            }
        }
        return null;
    }
    
    /**
     * Searches for an element with the given value for one getter.
     *
     * @param <T>
     * @param value
     * @param getter
     * @return the first element whose name matches, or {@code null} if none is
     *         found
     */
    public <T> E find(T value, Function<E, T> getter) {
        for (int i = 0; i < this.listModel.getSize(); i++) {
            E element = this.listModel.getElementAt(i);

            if (getter.apply(element).equals(value)) {
                return element;
            }
        }
        return null;
    }
    
    public void set(int selectedIndex, E element) {
        this.listModel.setElementAt(element, selectedIndex);
        this.listModelString.setElementAt(element.toString(), selectedIndex);
        
    }

}
