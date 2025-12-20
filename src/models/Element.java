/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 * Common interface for all domain elements used in the SynT model.
 * <p>
 * Implementations are expected to provide a human-readable name that
 * identifies the element in the context of theory synthesis.
 * </p>
 */
public interface Element {

    /**
     * Returns the logical name of this element.
     *
     * @return the element name, never {@code null}
     */
    String getName();
}
