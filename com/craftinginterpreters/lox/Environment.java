package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable '" + "'.");
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    /**
     * Defines a variable in the current environment by
     * adding it to the map with the specified value.
     *
     * @param name The name of the variable to define.
     * @param value The value to associate with the variable.
     */
    void define(String name, Object value) {
        values.put(name, value);
    }

    /**
     * Gets the ancestor environment {@code distance} steps away from this environment.
     * If the distance is 0, the environment itself is returned.
     *
     * @param distance the number of ancestors to traverse
     * @return the specified ancestor environment
     */
    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

    /**
     * Gets the value of the variable with the given name
     * in the scope which is {@code distance} ancestors away.
     *
     * @param distance the number of scopes to traverse
     * @param name the name of the variable
     * @return the value of the variable
     */
    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    /**
     * Assigns the value to the variable with the given name in the scope which
     * is {@code distance} ancestors away.
     *
     * @param distance the number of scopes to traverse
     * @param name the name of the variable
     * @param value the value to assign
     */
    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }
}