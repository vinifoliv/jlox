package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

// Automatically generates an AST (Expr.java)
public class GenerateAst {
    public static void main(String[] args) throws IOException {
        // output directory lacking
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }

        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
            "Assign   : Token name, Expr value",
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Object value",
            "Logical  : Expr left, Token operator, Expr right",
            "Unary    : Token operator, Expr right",
            "Variable : Token name"
        ));
        defineAst(outputDir, "Stmt", Arrays.asList(
            "If         : Expr condition, Stmt thenBranch," +
                        " Stmt elseBranch",
            "Block      : List<Stmt> statements",
            "Expression : Expr expression",
            "Print      : Expr expression",
            "Var        : Token name, Expr initializer"
        ));
    }

    private static void defineAst(
        String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java"; // path = com/craftinginterpreters/lox/Expr.java
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        // Package
        writer.println("package com.craftinginterpreters.lox;");
        writer.println();

        // Imports
        writer.println("import java.util.List;");
        writer.println();

        // Class definition
        writer.println("abstract class " + baseName + " {");
        writer.println();

        // Visitor definition
        defineVisitor(writer, baseName, types);

        // Subclasses definition
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        // accept()
        writer.println();
        writer.println("\tabstract <R> R accept(Visitor<R> visitor);");

        // End of Expr class
        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("\tinterface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            
            writer.println("\t\tR visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("\t}");
        writer.println();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("\tstatic class " + className + " extends " + baseName + " {");

        // Constructor
        writer.println("\t\t" + className + "(" + fieldList + ") {");

        // Parameters to fields
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("\t\t\tthis." + name + " = " + name + ";");
        }

        writer.println("\t\t}");

        // Visitor pattern
        writer.println();
        writer.println("\t\t@Override");
        writer.println("\t\t<R> R accept(Visitor<R> visitor) {");
        writer.println("\t\t\treturn visitor.visit" + className + baseName + "(this);");
        writer.println("\t\t}");
        writer.println();

        // Fields
        for (String field : fields) {
            writer.println("\t\tfinal " + field + ";");
        }

        writer.println("\t}\n");
    }
}
