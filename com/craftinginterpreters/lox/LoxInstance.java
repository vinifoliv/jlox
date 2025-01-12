package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

class LoxInstance {
    private LoxClass klass;

    LoxInstance(LoxClass klass) {
        this.klass = klass;
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
