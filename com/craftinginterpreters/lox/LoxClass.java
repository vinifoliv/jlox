package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Map;

class LoxClass implements LoxCallable {
    final String name;
    private final Map<String, LoxFunction> methods;
    
    LoxClass(String name, Map<String, LoxFunction> methods) {
        this.name = name;
        this.methods = methods;
    }

    /**
     * Finds a method in the class by name and returns the corresponding
     * LoxFunction if the method exists, or null if it does not.
     *
     * @param name The name of the method to find.
     * @return the LoxFunction matching the given name, or null if the method does not exist.
     */
    LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        return null;
    }

    /**
     * Returns the string representation of the LoxClass,
     * which is the name of the class.
     *
     * @return the name of the class as a string
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Calls the class to create a new instance of the class.
     *
     * @param interpreter the interpreter to use
     * @param arguments   the arguments to pass to the class's initializer
     * @return the newly created instance of the class
     */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        return instance;
    }

    /**
     * Gets the number of parameters that the class's initializer accepts.
     *
     * @return the number of parameters that the class's initializer accepts
     */
    @Override
    public int arity() {
        return 0;
    }
}
