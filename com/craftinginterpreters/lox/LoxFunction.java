package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    LoxFunction(Stmt.Function declaration, Environment closure, Boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.declaration = declaration;
        this.closure = closure;
    }

    /**
     * Returns a new LoxFunction that has the same declaration as this one,
     * but with the given instance bound to the "this" variable.
     *
     * @param instance the instance to bind
     * @return a new LoxFunction with the given instance bound
     */
    LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new LoxFunction(declaration, environment, isInitializer);
    }

    /**
     * Returns the string representation of the LoxFunction,
     * which includes the function name in the format "<fn {name}>".
     *
     * @return a string in the format "<fn {name}>"
     */
    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

    /**
     * Returns the number of parameters that the function accepts.
     *
     * @return the number of parameters that the function accepts
     */
    @Override
    public int arity() {
        return declaration.params.size();
    }

    /**
     * Calls the function with the given arguments. If the function is an
     * initializer and the call returns normally, it returns the value of
     * the "this" variable. Otherwise, it returns the value returned by the
     * function.
     *
     * @param interpreter the interpreter to use to execute the function
     * @param arguments   the arguments to pass to the function
     * @return the result of calling the function
     */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);

        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme,
                arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) return closure.getAt(0, "this");
                        
            return returnValue.value;
        }

        if (isInitializer) return closure.getAt(0, "this");
        return null;
    }
}