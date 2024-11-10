ROOT_DIR = ../../..
PACKAGE = com/craftinginterpreters/lox
CLASSES = TokenType.java  \
		  Token.java      \
		  Scanner.java    \
		  Expr.java       \
		  AstPrinter.java \
		  Parser.java     \
		  Lox.java

default: compile

compile: $(CLASSES)
	@javac $(CLASSES) ../build
	@echo Compilation finished.

clean:
	rm -rf *.class