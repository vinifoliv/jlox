ROOT_DIR = ../../..
PACKAGE = com/craftinginterpreters/lox
CLASSES = TokenType.java  \
		  Token.java      \
		  Scanner.java    \
		  Expr.java       \
		  AstPrinter.java \
		  Lox.java

default: compile

compile: $(CLASSES)
	@javac $(CLASSES)
	@echo Compilation finished.

clean:
	rm -rf *.class