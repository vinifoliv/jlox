package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    private static final Interpreter interpreter = new Interpreter(); // static so as to reuse the same interpreter for successive calls to run()
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) {
        // Multiple arguments - bad usage
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }
        // Single argument - run a specified file
        else if (args.length == 1) {
            try {
                runFile(args[0]);
            } 
            catch (Exception e) { }
        }
        // No argument - run the prompt
        else {
            try {
                runPrompt();
            }
            catch (Exception e) {}
        }
    }

    // Run a specified file
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset())); // it reads the whole file and shows all errors before quitting

        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    // Run the prompt
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run (line);
            hadError = false;
        }
    }

    /**
     * Executes the given source code.
     *
     * This method scans the source code to generate tokens,
     * parses the tokens into statements, and then interprets
     * the statements. If any errors occur during scanning or
     * parsing, the method exits early without executing the
     * statements.
     *
     * @param source The source code to be executed.
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if errors occured
        if (hadError) return;

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

        if (hadError) return;

        interpreter.interpret(statements);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(
            error.getMessage() +
            "\n[line " + error.token.line + "]"
        );

        hadRuntimeError = true;
    }

    private static void report(int line, String where, String message) {
        System.err.println(
            "[line " + line + "] Error" + where + ": " + message
        );
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        }
        else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
}