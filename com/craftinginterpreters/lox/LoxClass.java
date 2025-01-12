package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Map;

class LoxClass {
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
}
