/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 * Represents a variable in the domain theory.
 * <p>
 * A variable is associated with a {@link Construct} and a {@link Universe},
 * and has both a full name and a short nickname used in implications and
 * matrices.
 * </p>
 */
public class Variable implements Element {

    /**
     * Full name of the variable.
     */
    private String name;
    /**
     * Short alias or nickname used in formulas and matrices.
     */
    private String nickname;
    /**
     * Construct to which this variable belongs.
     */
    private Construct construct;
    /**
     * Universe that defines the type and allowed relations for this variable.
     */
    private Universe universe;

    /**
     * Creates a new variable associated with the given construct and universe.
     *
     * @param name      the full name of the variable
     * @param nickname  the short alias used in formulas
     * @param construct the construct this variable belongs to
     * @param universe  the universe describing the domain of this variable
     */
    public Variable(String name, String nickname, Construct construct, Universe universe) {
        this.name = name.trim();
        this.nickname = nickname.trim();
        this.construct = construct;
        this.universe = universe;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the nickname (alias) of this variable.
     *
     * @return the variable nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the construct this variable belongs to.
     *
     * @return the associated {@link Construct}
     */
    public Construct getConstruct() {
        return construct;
    }

    /**
     * Returns the universe that defines the domain of this variable.
     *
     * @return the associated {@link Universe}
     */
    public Universe getUniverse() {
        return universe;
    }

    /**
     * Updates the variable name.
     *
     * @param name the new full name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the variable nickname.
     *
     * @param nickname the new alias
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Updates the construct associated with this variable.
     *
     * @param construct the new {@link Construct}
     */
    public void setConstruct(Construct construct) {
        this.construct = construct;
    }

    /**
     * Updates the universe associated with this variable.
     *
     * @param universe the new {@link Universe}
     */
    public void setUniverse(Universe universe) {
        this.universe = universe;
    }

    /**
     * Returns a formatted representation of the variable including its
     * nickname, construct and universe.
     *
     * @return a human-readable string with the form
     *         {@code name(nickname) / construct / universe}
     */
    @Override
    public String toString() {
        return name + " (" + nickname + ") / " + construct.getName() + " / " + universe.getName();
    }
}
