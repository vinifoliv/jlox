ROOT_DIR = ../../..
PACKAGE = com/craftinginterpreters/lox
CLASSES = TokenType.java    \
		  Token.java        \
		  Environment.java  \
		  Scanner.java      \
		  Expr.java         \
		  Stmt.java         \
		  Parser.java       \
		  RuntimeError.java \
		  Interpreter.java  \
		  Lox.java

default: compile

compile: $(CLASSES)
	javac $(CLASSES)

clean:
	@rm -rf *.class