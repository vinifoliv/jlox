package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    Interpreter() {
        globals.define("clock", new LoxCallable() {
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() { return "<native fn>"; }
        });
    }

    /**
     * Interprets a list of statements by executing each statement
     * sequentially. If a runtime error occurs during the execution
     * of any statement, it catches the error and reports it using
     * the Lox runtime error mechanism.
     *
     * @param statements The list of statements to be interpreted.
     */
    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        }
        catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    
    /**
     * Evaluates a literal expression and returns its value.
     *
     * @param expr The literal expression to evaluate.
     * @return The value of the literal expression.
     */
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    /**
     * Evaluates a logical expression by first evaluating the left operand.
     * If the operator is "or" and the left operand is truthy, returns the
     * left operand. Otherwise, if the operator is "and" and the left operand
     * is falsy, returns the left operand. If neither condition is met, it
     * evaluates and returns the right operand.
     *
     * @param expr The logical expression to evaluate.
     * @return The result of the logical expression evaluation.
     */
    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    /**
     * Evaluates a set expression by first resolving the object to be
     * assigned in the current scope. If the object is not an instance,
     * it throws a runtime error, as only instances have fields. Then,
     * it evaluates the value to be assigned to the object and assigns
     * it to the object, returning the value.
     *
     * @param expr The set expression to evaluate.
     * @return The value of the assignment.
     */
    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);

        if (!(object instanceof LoxInstance)) {
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }

        Object value = evaluate(expr.value);
        ((LoxInstance)object).set(expr.name, value);
        return value;
    }

    /**
     * Evaluates a super expression by resolving the superclass and
     * instance associated with the current scope. It first retrieves
     * the superclass using the resolved distance for the "super"
     * keyword and the instance using the resolved distance for the
     * "this" keyword. Then, it finds the method in the superclass
     * specified by the super expression and binds it to the instance.
     *
     * @param expr The super expression to evaluate.
     * @return The bound method from the superclass.
     */
    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        LoxClass superclass = (LoxClass)environment.getAt(distance, "super");

        LoxInstance object = (LoxInstance)environment.getAt(distance - 1, "this");

        LoxFunction method = superclass.findMethod(expr.method.lexeme);

        if (method == null) {
            throw new RuntimeError(expr.method, "Undefined property '" + expr.method.lexeme + "''.");
        }

        return method.bind(object);
    }

    /**
     * Evaluates a this expression by looking up the this keyword
     * in the current scope, returning the value associated with
     * the this keyword.
     *
     * @param expr The this expression to evaluate.
     * @return The value associated with the this keyword.
     */
    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }

    /**
     * Evaluates the right-side of a unary expression and returns either its either numerically (-) or logically (!) opposite value.
     */
    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch(expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
        }

        // Unreachable
        return null;
    }

    /**
     * Visits a variable expression, retrieving the value of the variable
     * by looking up its name in the current environment or global scope.
     *
     * @param expr The variable expression to visit.
     * @return The value of the variable.
     */
    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    /**
     * Looks up the value of a variable by traversing the scope chain.
     *
     * @param name The token for the variable name.
     * @param expr The expression where the variable is used.
     * @return The value of the variable.
     */
    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    /**
     * Checks if the operand is valid.
     */
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;

        throw new RuntimeError(operator, "Operand must be a number."); 
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;

        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    /**
     * Returns the logical value of an expression.
     * The only falsey values are nil and false.
     */
    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }
 
    /**
     * Returns whether two expression values are equal.
     * Implemented separately so as to avoid throwing a NullPointerException in the case of having nil/null values.
     */
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    /**
     * Converts the expression's final value into a string.
     */
    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0"))
                text = text.substring(0, text.length() - 2);
            return text;
        }

        return object.toString();
    }

    /**
     * Evaluates the expression inside of parentheses.
     */
    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    /**
     * Sends an expression back to the interpreter recursively.
     */
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    /**
     * Executes a statement. This is the entry-point for the entire interpreter.
     */
    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    /**
     * Associates an expression with a depth, which is how many scopes to
     * traverse to resolve the expression.
     *
     * @param expr The expression to associate with a depth.
     * @param depth The depth of the expression.
     */
    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;

        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    /**
     * Visits a block statement by creating a new environment scope
     * for the block's statements and executing each statement within
     * this new scope.
     *
     * @param stmt The block statement to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    /**
     * Visits a class statement by defining the class name in the current
     * environment with a placeholder value, creating a map of class methods,
     * instantiating a LoxClass with the class name and methods, and then
     * assigning the LoxClass to the class name in the environment.
     *
     * @param stmt The class statement to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if (!(superclass instanceof LoxClass)) {
                throw new RuntimeError(stmt.superclass.name, "Superclass must be a class.");
            }
        }

        environment.define(stmt.name.lexeme, null);

        if (stmt.superclass != null) {
            environment = new Environment(environment);
            environment.define("super", superclass);
        }

        Map<String, LoxFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods) {
            LoxFunction function = new LoxFunction(method, environment, method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme, function);
        }

        LoxClass klass = new LoxClass(stmt.name.lexeme, (LoxClass)superclass, methods);

        if (superclass != null) {
            environment = environment.enclosing;
        }

        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    /**
     * Visits a function statement, declaring the function name in the current
     * scope and defining the function.
     *
     * @param stmt The function statement to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt, environment, false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        }
        else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }

        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);

        throw new Return(value);
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override 
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        
        return null;
    }

    /**
     * Evaluates an assignment expression, computes the value to be assigned,
     * and updates the value of the variable in the appropriate scope.
     *
     * @param expr The assignment expression to visit.
     * @return The value that was assigned.
     */
    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);

        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }

        return value;
    }

    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            // Equality operators
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);

            // Comparison operators
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;

            // Arithmetic operators
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double)
                    return (double)left + (double)right;
                if (left instanceof String && right instanceof String) 
                    return (String)left + (String)right;
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
        }

        // Unreachable
        return null;
    }

    /**
     * Evaluates a call expression, resolving the callee to be called
     * within the current scope, resolving all of the arguments to the
     * call within the current scope, and then calling the resolved
     * callable with the resolved arguments.
     *
     * @param expr The call expression to visit.
     * @return The result of calling the resolved function.
     */
    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }

        LoxCallable function = (LoxCallable)callee;

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expect " + 
                function.arity() + " arguments but got " +
                arguments.size());
        }

        return function.call(this, arguments);
    }

    /**
     * Evaluates a get expression, resolving the object to be gotten
     * within the current scope, and then getting the field from the
     * resolved object.
     *
     * @param expr The get expression to visit.
     * @return The value of the field.
     */
    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof LoxInstance) {
            return ((LoxInstance) object).get(expr.name);
        }

        throw new RuntimeError(expr.name, "Only instances have properties.");
    }
}
