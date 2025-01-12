package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class LoxInstance {
    private LoxClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    /**
     * Retrieves the value of a property from the LoxInstance.
     * 
     * @param name The token for the property name.
     * @return The value of the property.
     * @throws RuntimeError if the property does not exist.
     */
    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    /**
     * Sets the value of a property on the LoxInstance.
     * 
     * @param name The token for the property name.
     * @param value The value to set for the property.
     */
    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    /**
     * Returns the string representation of the LoxInstance,
     * which includes the name of the class and the word "instance".
     *
     * @return a string in the format "{class name} instance"
     */
    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
