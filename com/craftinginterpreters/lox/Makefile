ROOT_DIR = ../../..
PACKAGE = com/craftinginterpreters/lox
CLASSES = TokenType.java    \
		  Token.java        \
		  Scanner.java      \
		  Expr.java         \
		  Stmt.java         \
		  AstPrinter.java   \
		  Parser.java       \
		  RuntimeError.java \
		  Interpreter.java  \
		  Lox.java

default: compile

compile: $(CLASSES)
	@javac $(CLASSES)
	@echo Compilation finished.

clean:
	@rm -rf *.class