package models;

import java.util.ArrayList;

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
     * Universe type (e.g.
     * {@code "Enum (Scalar)"}, {@code "Real"}, {@code "Bool"}).
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
    private ArrayList<Function> functions;
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
     * Creates an enumerated universe with a fixed set of constant values and
     * explicit relation permissions.
     *
     * @param name the universe name; whitespace is trimmed before storing
     * @param type textual type label (e.g. {@code "Enum (Scalar)"})
     * @param valueEnum semicolon-separated list of the allowed enumeration values
     * @param functions list of functions (e.g. aggregations) tied to this universe
     * @param equal {@code true} if equality (=) is permitted between variables
     * @param greater {@code true} if the strict greater-than (>) relation is permitted
     * @param greater_equal {@code true} if the greater-or-equal (>=) relation is permitted
     * @param not_equal {@code true} if inequality (!=) is permitted
     * @param less {@code true} if the strict less-than (<) relation is permitted
     * @param less_equal {@code true} if the less-or-equal (<=) relation is permitted
     */
    public Universe(String name, String type, String valueEnum, ArrayList<Function> functions, boolean equal, boolean greater, boolean greater_equal, boolean not_equal, boolean less, boolean less_equal) {
        this.name = name;
        this.type = type;
        this.valueEnum = valueEnum;
        this.functions = functions;
        this.equal = equal;
        this.greater = greater;
        this.greater_equal = greater_equal;
        this.not_equal = not_equal;
        this.less = less;
        this.less_equal = less_equal;
    }

    /**
     * Creates a real-valued universe that controls a numeric range and the
     * permitted relations.
     *
     * @param name nombre con el que se identifica el universo
     * @param type etiqueta de tipo (por ejemplo, {@code "Real"})
     * @param valueMin límite inferior permitido
     * @param valueMax límite superior permitido
     * @param functions lista de funciones admitidas dentro de este universo
     * @param equal {@code true} si se admite la igualdad (=)
     * @param greater {@code true} si se permite la relación estricta mayor que (>)
     * @param greater_equal {@code true} si se permite mayor o igual (>=)
     * @param not_equal {@code true} si se permite la desigualdad (!=)
     * @param less {@code true} si se permite la relación estricta menor que (<)
     * @param less_equal {@code true} si se permite menor o igual (<=)
     */
    public Universe(String name, String type, double valueMin, double valueMax, ArrayList<Function> functions, boolean equal, boolean greater, boolean greater_equal, boolean not_equal, boolean less, boolean less_equal) {
        this.name = name;
        this.type = type;
        this.valueMin = valueMin;
        this.valueMax = valueMax;
        this.functions = functions;
        this.equal = equal;
        this.greater = greater;
        this.greater_equal = greater_equal;
        this.not_equal = not_equal;
        this.less = less;
        this.less_equal = less_equal;
    }

    /**
     * Creates a boolean universe that permits only {@code true}/{@code false} and
     * exposes the available comparison relations.
     *
     * @param name universe identifier
     * @param type type label (usually {@code "Bool"})
     * @param functions functions that operate over this universe
     * @param equal {@code true} to allow equality (=)
     * @param greater {@code true} to allow strict greater than (>)
     * @param greater_equal {@code true} to allow greater or equal (>=)
     * @param not_equal {@code true} to allow not equal (!=)
     * @param less {@code true} to allow strict less than (<)
     * @param less_equal {@code true} to allow less or equal (<=)
     */
    public Universe(String name, String type, ArrayList<Function> functions, boolean equal, boolean greater, boolean greater_equal, boolean not_equal, boolean less, boolean less_equal) {
        this.name = name;
        this.type = type;
        this.functions = functions;
        this.equal = equal;
        this.greater = greater;
        this.greater_equal = greater_equal;
        this.not_equal = not_equal;
        this.less = less;
        this.less_equal = less_equal;
    }

    /**
     * {@inheritDoc}
     */
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
    public ArrayList<Function> getFunctions() {
        return functions;
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
     * @param functions the new ArrayList of functions
     */
    public void setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
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
        if (!functions.isEmpty()) {
            function_aridad = "[";
            for (Function function : functions) {
                function_aridad += function.toString() + " , ";
            }
            function_aridad = function_aridad.substring(0, function_aridad.length() - 3);
            function_aridad += "]";

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
        String relations = "";
        if (isEqual()) {
            relations += " = ; ";
        }
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
        if (relations.length() > 2) {
            return relations.substring(0, relations.length() - 2);
        } else {
            return "";
        }
    }
}
