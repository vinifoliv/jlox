package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Map;

class LoxClass implements LoxCallable {
    final String name;
    
    LoxClass(String name) {
        this.name = name;
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
