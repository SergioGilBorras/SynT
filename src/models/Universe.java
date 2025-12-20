package models;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Represents a universe (domain) for variables in the theory.
 * <p>
 * A universe can be enumerated, real-valued or boolean, and defines both the
 * value space (enumeration or numeric range) and the allowed comparison
 * relations for variables that use it.
 * </p>
 */
public class Universe implements Element {

    /**
     * Name of the universe.
     */
    private String name;
    /**
     * Universe type (e.g. {@code "Enum (Scalar)"}, {@code "Real"}, {@code "Bool"}).
     */
    private final String type;
    /**
     * Enumerated values when the universe is of enum type; {@code null}
     * otherwise.
     */
    private String valueEnum;
    /**
     * Minimum numeric value when the universe is real-valued.
     */
    private double valueMin = Double.MAX_VALUE;
    /**
     * Maximum numeric value when the universe is real-valued.
     */
    private double valueMax = Double.MIN_VALUE;
    /**
     * Optional function associated with the universe (e.g. aggregation
     * function).
     */
    private String function;
    /**
     * Arity of the function associated with the universe.
     */
    private int aridad;
    /**
     * Whether equality (=) is allowed for variables in this universe.
     */
    private boolean equal;
    /**
     * Whether strict greater than (>) is allowed.
     */
    private boolean greater;
    /**
     * Whether greater or equal (>=) is allowed.
     */
    private boolean greater_equal;
    /**
     * Whether not equal (!=) is allowed.
     */
    private boolean not_equal;
    /**
     * Whether strict less than (<) is allowed.
     */
    private boolean less;
    /**
     * Whether less or equal (<=) is allowed.
     */
    private boolean less_equal;

    /**
     * Creates an enumerated universe.
     *
     * @param name         the universe name
     * @param type         the type string (enum variant)
     * @param valueEnum    the semicolon-separated list of allowed values
     * @param function     optional function name associated with the universe
     * @param aridad       arity of the function
     * @param equal        whether equality is allowed
     * @param greater      whether > is allowed
     * @param greater_equal whether >= is allowed
     * @param not_equal     whether != is allowed
     * @param less         whether < is allowed
     * @param less_equal   whether <= is allowed
     */
    public Universe(String name, String type, String valueEnum, String function, int aridad, boolean equal, boolean greater, boolean greater_equal, boolean not_equal, boolean less, boolean less_equal) {
        this.name = name;
        this.type = type;
        this.valueEnum = valueEnum;
        this.function = function;
        this.aridad = aridad;
        this.equal = equal;
        this.greater = greater;
        this.greater_equal = greater_equal;
        this.not_equal = not_equal;
        this.less = less;
        this.less_equal = less_equal;
    }

    /**
     * Creates a real-valued universe.
     *
     * @param name          the universe name
     * @param type          the type string (real variant)
     * @param valueMin      minimum allowed value
     * @param valueMax      maximum allowed value
     * @param function      optional function name associated with the universe
     * @param aridad        arity of the function
     * @param equal         whether equality is allowed
     * @param greater       whether > is allowed
     * @param greater_equal whether >= is allowed
     * @param not_equal      whether != is allowed
     * @param less          whether < is allowed
     * @param less_equal    whether <= is allowed
     */
    public Universe(String name, String type, double valueMin, double valueMax, String function, int aridad, boolean equal, boolean greater, boolean greater_equal, boolean not_equal, boolean less, boolean less_equal) {
        this.name = name;
        this.type = type;
        this.valueMin = valueMin;
        this.valueMax = valueMax;
        this.function = function;
        this.aridad = aridad;
        this.equal = equal;
        this.greater = greater;
        this.greater_equal = greater_equal;
        this.not_equal = not_equal;
        this.less = less;
        this.less_equal = less_equal;
    }

    /**
     * Creates a boolean universe.
     *
     * @param name          the universe name
     * @param type          the type string (boolean variant)
     * @param function      optional function name associated with the universe
     * @param aridad        arity of the function
     * @param equal         whether equality is allowed
     * @param greater       whether > is allowed
     * @param greater_equal whether >= is allowed
     * @param not_equal      whether != is allowed
     * @param less          whether < is allowed
     * @param less_equal    whether <= is allowed
     */
    public Universe(String name, String type, String function, int aridad, boolean equal, boolean greater, boolean greater_equal, boolean not_equal, boolean less, boolean less_equal) {
        this.name = name;
        this.type = type;
        this.function = function;
        this.aridad = aridad;
        this.equal = equal;
        this.greater = greater;
        this.greater_equal = greater_equal;
        this.not_equal = not_equal;
        this.less = less;
        this.less_equal = less_equal;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the type of this universe.
     *
     * @return the type string
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the enumerated values for this universe, if applicable.
     *
     * @return the raw enumeration string, or {@code null} if not enumerated
     */
    public String getValueEnum() {
        return valueEnum;
    }

    /**
     * Returns the minimum numeric value for this universe.
     *
     * @return the minimum value
     */
    public double getValueMin() {
        return valueMin;
    }

    /**
     * Returns the maximum numeric value for this universe.
     *
     * @return the maximum value
     */
    public double getValueMax() {
        return valueMax;
    }

    /**
     * Returns the function associated with this universe, if any.
     *
     * @return the function name, or an empty string if none
     */
    public String getFunction() {
        return function;
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
     * Indicates whether equality (=) is allowed.
     *
     * @return {@code true} if equality is allowed; {@code false} otherwise
     */
    public boolean isEqual() {
        return equal;
    }

    /**
     * Indicates whether strict greater than (>) is allowed.
     *
     * @return {@code true} if > is allowed; {@code false} otherwise
     */
    public boolean isGreater() {
        return greater;
    }

    /**
     * Indicates whether greater or equal (>=) is allowed.
     *
     * @return {@code true} if >= is allowed; {@code false} otherwise
     */
    public boolean isGreater_equal() {
        return greater_equal;
    }

    /**
     * Indicates whether not equal (!=) is allowed.
     *
     * @return {@code true} if != is allowed; {@code false} otherwise
     */
    public boolean isNot_equal() {
        return not_equal;
    }

    /**
     * Indicates whether strict less than (<) is allowed.
     *
     * @return {@code true} if < is allowed; {@code false} otherwise
     */
    public boolean isLess() {
        return less;
    }

    /**
     * Indicates whether less or equal (<=) is allowed.
     *
     * @return {@code true} if <= is allowed; {@code false} otherwise
     */
    public boolean isLess_equal() {
        return less_equal;
    }

    /**
     * Updates the universe name.
     *
     * @param name the new name; surrounding whitespace will be trimmed
     */
    public void setName(String name) {
        this.name = name.trim();
    }

    /**
     * Updates the function associated with this universe.
     *
     * @param function the new function name
     */
    public void setFunction(String function) {
        this.function = function;
    }

    /**
     * Updates the arity of the function associated with this universe.
     *
     * @param aridad the new arity value
     */
    public void setAridad(int aridad) {
        this.aridad = aridad;
    }

    /**
     * Sets whether equality (=) is allowed.
     *
     * @param equal {@code true} to allow equality; {@code false} otherwise
     */
    public void setEqual(boolean equal) {
        this.equal = equal;
    }

    /**
     * Sets whether strict greater than (>) is allowed.
     *
     * @param greater {@code true} to allow >; {@code false} otherwise
     */
    public void setGreater(boolean greater) {
        this.greater = greater;
    }

    /**
     * Sets whether greater or equal (>=) is allowed.
     *
     * @param greater_equal {@code true} to allow >=; {@code false} otherwise
     */
    public void setGreater_equal(boolean greater_equal) {
        this.greater_equal = greater_equal;
    }

    /**
     * Sets whether not equal (!=) is allowed.
     *
     * @param not_equal {@code true} to allow !=; {@code false} otherwise
     */
    public void setNot_equal(boolean not_equal) {
        this.not_equal = not_equal;
    }

    /**
     * Sets whether strict less than (<) is allowed.
     *
     * @param less {@code true} to allow <; {@code false} otherwise
     */
    public void setLess(boolean less) {
        this.less = less;
    }

    /**
     * Sets whether less or equal (<=) is allowed.
     *
     * @param less_equal {@code true} to allow <=; {@code false} otherwise
     */
    public void setLess_equal(boolean less_equal) {
        this.less_equal = less_equal;
    }

    /**
     * Returns a formatted description of the universe, including its type,
     * value range or enumeration, function and allowed relations.
     *
     * @return a human-readable summary of this universe
     */
    @Override
    public String toString() {
        String function_aridad = "";
        if (!function.isEmpty()) {
            function_aridad = "[" + function + " (" + aridad + ")]";
        }
        if (valueEnum != null) {
            return name + " [" + type + " (" + valueEnum + ")] " + function_aridad + " [" + getRelations() + "]";
        } else if (valueMax > Double.MIN_VALUE && valueMin < Double.MAX_VALUE && valueMax > valueMin) {
            return name + " [" + type + " (" + valueMin + "|" + valueMax + ")] " + function_aridad + " [" + getRelations() + "]";
        } else {
            return name + " [" + type + "] " + function_aridad + " [" + getRelations() + "]";
        }
    }

    /**
     * Builds a string with all enabled comparison relations for this universe.
     *
     * @return a semicolon-separated list of enabled relations
     */
    private String getRelations() {
        String relations = "= ; ";
        if (isGreater()) {
            relations += " > ; ";
        }
        if (isGreater_equal()) {
            relations += " >= ; ";
        }
        if (isNot_equal()) {
            relations += " != ; ";
        }
        if (isLess()) {
            relations += " < ; ";
        }
        if (isLess_equal()) {
            relations += " <= ; ";
        }

        return relations.substring(0, relations.length() - 3);
    }
}
