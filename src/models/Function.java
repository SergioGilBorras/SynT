package models;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * Models a function that can be associated to a universe (e.g. aggregation or
 * transformation) and exposes its name plus arity.
 */
public class Function implements Element {

    /**
     * Function Name associated with the universe (e.g. aggregation function).
     */
    private String name;

    /**
     * Arity of the function.
     */
    private int aridad;

    /**
     * Creates a function.
     *
     * @param name Function name associated with the universe
     * @param aridad arity of the function
     */
    public Function(String name, int aridad) {
        this.name = name;
        this.aridad = aridad;
    }

    /**
     * Parses a function description of the form {@code name (arity)}.
     *
     * @param functionToString textual representation that includes the arity
     */
    public Function(String functionToString) {
        String[] parts = functionToString.split("\\s*\\(");
        this.name = parts[0].trim();
        this.aridad = 0;
        try {
            this.aridad = Integer.parseUnsignedInt(parts[1].substring(0, parts[1].length() - 1).trim());
        } catch (NumberFormatException e) {
            this.aridad = 0;
        }
    }

    /**
     * Returns the name.
     *
     * @return the function name, or an empty string if none
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the arity of the associated function.
     *
     * @return the function arity
     */
    public int getAridad() {
        return aridad;
    }

    /**
     * Updates the function name.
     *
     * @param name the new name; surrounding whitespace will be trimmed
     */
    public void setName(String name) {
        this.name = name.trim();
    }

    /**
     * Updates the arity of the function.
     *
     * @param aridad the new arity value
     */
    public void setAridad(int aridad) {
        this.aridad = aridad;
    }

    /**
     * Returns a formatted description of the function.
     *
     * @return {@code "name (arity)"}
     */
    @Override
    public String toString() {
        return name + " (" + aridad + ")";
    }

}
