package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;

    LoxFunction(Stmt.Function declaration, Environment closure) {
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
        return new LoxFunction(declaration, environment);
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
     * Calls the function with the given arguments by creating a new environment
     * that captures the current closure and defining each parameter in the new
     * environment with the corresponding argument. It then executes the body of
     * the function in the new environment and returns the result of the function
     * call. If the function does not return a value, it returns null.
     *
     * @param interpreter the interpreter to use
     * @param arguments   the arguments to pass to the function
     * @return the result of the function call, or null if the function did not return a value
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
            return returnValue.value;
        }

        return null;
    }
}