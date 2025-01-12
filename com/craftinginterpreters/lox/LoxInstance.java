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
     * Retrieves the value of a field from the object. If the field is not an instance variable,
     * it checks if there is a method with the same name and returns that. If there is neither
     * an instance variable or a method with the given name, it throws a runtime error.
     *

     * @param name the name of the field to retrieve
     * @return the value of the field, or the method with the same name
     * @throws RuntimeError if the field does not exist
     */
    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        LoxFunction method = klass.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

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
