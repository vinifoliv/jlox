package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE, 
        FUNCTION, 
        METHOD
    }

    /**
     * Resolves a list of statements, which means visiting each statement
     * and allowing it to resolve any expressions or other statements it
     * may have.
     *
     * @param statements The list of statements to resolve.
     */
    void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    /**
     * Resolves a function, which means declaring each of its parameters
     * and resolving its body.
     *
     * @param function The function to resolve.
     */
    private void resolveFunction(Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    /**
     * Begins a new scope by pushing a new map onto the scopes stack.
     * This map represents a new local scope for variable declarations.
     */
    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    /**
     * Ends the current scope by popping the most recently added
     * scope from the scopes stack. This is used to leave the scope
     * of a block once all of its statements have been resolved.
     */
    private void endScope() {
        scopes.pop();
    }

    /**
     * Declares a variable in the current scope by adding its name to the scope
     * map with an initial value indicating that it is not yet defined.
     * If there are no scopes, the method does nothing.
     *
     * @param name The token representing the name of the variable to declare.
     */
    private void declare(Token name) {
        if (scopes.isEmpty())
            return;

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Lox.error(name, "Already a variable with this name in this scope.");
        }
        scope.put(name.lexeme, false);
    }

    /**
     * Defines a variable in the current scope by updating the value
     * in the scope map from false (indicating that the variable is
     * not yet defined) to true (indicating that it is defined).
     * If there are no scopes, the method does nothing.
     *
     * @param name The token representing the name of the variable
     *             to define.
     */
    private void define(Token name) {
        if (scopes.isEmpty())
            return;
        scopes.peek().put(name.lexeme, true);
    }

    /**
     * Resolves a variable in the current scope chain by finding the
     * innermost scope that contains the variable and then resolving
     * the variable to the distance from the current scope to the
     * innermost scope.
     *
     * @param expr The expression containing the variable to resolve.
     * @param name The token representing the name of the variable to
     *             resolve.
     */
    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    /**
     * Visits a block statement, creating a new scope for the block's
     * statements, resolving each statement within this new scope,
     * and then ending the scope once all statements have been resolved.
     *
     * @param stmt The block statement to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();

        return null;
    }

    /**
     * Visits a class statement, declaring the class name in the current
     * scope, defining it, and then resolving each of its methods.
     *
     * @param stmt The class statement to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        declare(stmt.name);
        define(stmt.name);

        for (Stmt.Function method : stmt.methods) {
            FunctionType declaration = FunctionType.METHOD;
            resolveFunction(method, declaration);
        }

        return null;
    }

    /**
     * Visits an expression statement, resolving the expression within
     * the current scope.
     *
     * @param stmt The expression statement to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    /**
     * Visits a function statement, declaring the function name in the current
     * scope and defining it, and then resolving the function.
     *
     * @param stmt The function statement to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    /**
     * Visits an if statement, resolving the condition, then branch, and
     * optionally else branch.
     *
     * @param stmt The if statement to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    /**
     * Visits a print statement, resolving the expression to be printed
     * within the current scope.
     *
     * @param stmt The print statement to visit.
     * @return Always returns null.
     */

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    /**
     * Visits a return statement, resolving the expression to be returned
     * within the current scope if the statement has one.
     *
     * @param stmt The return statement to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword, "Can't return from top-level code.");
        }

        if (stmt.value != null) {
            resolve(stmt.value);
        }

        return null;
    }

    /**
     * Visits a variable declaration statement, declaring the variable
     * name within the current scope, resolving the variable's initializer
     * if it has one, and then defining the variable.
     *
     * @param stmt The variable declaration statement to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    /**
     * Visits a while statement, resolving the condition and then resolving
     * the body of the loop in the current scope.
     *
     * @param stmt The while statement to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    /**
     * Visits an assignment expression, resolving the value on the right side
     * of the assignment and then resolving the variable on the left side
     * of the assignment in the current scope.
     *
     * @param expr The assignment expression to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    /**
     * Visits a binary expression, resolving both the left and right sides
     * of the expression within the current scope.
     *
     * @param expr The binary expression to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    /**
     * Visits a call expression, resolving the expression to be called
     * within the current scope, and then resolving all of the arguments
     * to the call within the current scope.
     *
     * @param expr The call expression to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);
        
        for (Expr argument : expr.arguments) {
            resolve(argument);
        }

        return null;
    }

    /**
     * Visits a get expression, resolving the object to be gotten within
     * the current scope.
     *
     * @param expr The get expression to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    /**
     * Visits a grouping expression, resolving the expression inside of
     * parentheses within the current scope.
     *
     * @param expr The grouping expression to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    /**
     * Visits a literal expression, which requires no additional resolution
     * (literals are already fully evaluated).
     *
     * @param expr The literal expression to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    /**
     * Visits a logical expression, resolving both the left and right
     * operands of the logical operator within the current scope.
     *
     * @param expr The logical expression to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    /**
     * Visits a set expression, resolving the value to be assigned
     * on the right side of the assignment and the object to be
     * assigned on the left side of the assignment within the current
     * scope.
     *
     * @param expr The set expression to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    /**
     * Visits a unary expression, resolving the operand expression
     * on the right side of the unary operator within the current scope.
     *
     * @param expr The unary expression to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    /**
     * Visits a variable expression, checking that the variable is not being
     * read within its own initializer (which is illegal), and then resolving
     * the variable to its value.
     *
     * @param expr The variable expression to visit.
     * @return Always returns null.
     */
    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() &&
            scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Lox.error(expr.name, "Can't read local variable in its own initializer");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    /**
     * Resolves a statement, which means visiting the statement and allowing
     * it to resolve any expressions or other statements it may have.
     *
     * @param stmt The statement to resolve.
     */
    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    /**
     * Resolves an expression, which means visiting the expression and allowing
     * it to resolve itself.
     *
     * @param expr The expression to resolve.
     */
    private void resolve(Expr expr) {
        expr.accept(this);
    }
}