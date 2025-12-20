/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 * Represents a construct in the domain theory.
 * <p>
 * A construct has a name, a textual description of its origin ("from") and
 * a scope description. It is one of the core building blocks used when
 * defining variables and implications in the SynT tool.
 * </p>
 */
public class Construct implements Element {

    /**
     * Human-readable name of the construct.
     */
    private String name;
    /**
     * Description of the source or origin of the construct.
     */
    private String from;
    /**
     * Scope conditions or context where the construct is applicable.
     */
    private String scope;

    /**
     * Creates a new construct with the given attributes.
     *
     * @param name  the construct name
     * @param from  textual description of the origin of the construct
     * @param scope scope conditions or context of applicability
     */
    public Construct(String name, String from, String scope) {
        this.name = name.trim();
        this.from = from.trim();
        this.scope = scope.trim();
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the textual description of the origin of this construct.
     *
     * @return the "from" description
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the scope description of this construct.
     *
     * @return the scope conditions
     */
    public String getScope() {
        return scope;
    }

    /**
     * Updates the construct name.
     *
     * @param name the new name; surrounding whitespace will be trimmed
     */
    public void setName(String name) {
        this.name = name.trim();
    }

    /**
     * Updates the "from" description of the construct.
     *
     * @param from the new origin description; surrounding whitespace will be
     *             trimmed
     */
    public void setFrom(String from) {
        this.from = from.trim();
    }

    /**
     * Updates the scope conditions of the construct.
     *
     * @param scope the new scope description; surrounding whitespace will be
     *              trimmed
     */
    public void setScope(String scope) {
        this.scope = scope.trim();
    }

    /**
     * Returns a formatted representation of the construct including its scope
     * and origin.
     *
     * @return a human-readable string with the form
     *         {@code [scope] name (from)}
     */
    @Override
    public String toString() {
        return "[" + scope + "] " + name + " (" + from + ")";
    }
}
