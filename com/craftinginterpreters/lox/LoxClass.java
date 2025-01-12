package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Map;

class LoxClass implements LoxCallable {
    final String name;
    final LoxClass superclass;
    private final Map<String, LoxFunction> methods;
    
    LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
        this.superclass = superclass;
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
     * Calls the class to create a new instance of it. If the class has an initializer
     * method named "init", it will be called with the provided arguments to initialize
     * the new instance.
     *
     * @param interpreter the interpreter to use while calling the initializer
     * @param arguments   the arguments to pass to the initializer
     * @return the new instance of the class
     */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }


    /**
     * Returns the number of arguments that the initializer method of this class
     * takes. If the class does not have an initializer method, it returns 0.
     *
     * @return the number of arguments that the initializer method takes, or 0 if there is no initializer
     */
    @Override
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }
}
