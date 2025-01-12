ROOT_DIR=../../..
LOX_DIR  = com/craftinginterpreters/lox
TOOL_DIR = com/craftinginterpreters/tool

LOX_PACKAGE  = com.craftinginterpreters.lox
TOOL_PACKAGE = com.craftinginterpreters.tool

CLASSES = TokenType.java    \
		  Token.java        \
		  Environment.java  \
		  Return.java       \
		  Scanner.java      \
		  Expr.java         \
		  Stmt.java         \
		  Parser.java       \
		  RuntimeError.java \
		  LoxCallable.java  \
		  LoxFunction.java  \
		  LoxClass.java     \
		  LoxInstance.java  \
		  Interpreter.java  \
		  Resolver.java     \
		  Lox.java

default: lox

lox:
	@cd $(LOX_DIR) && javac $(CLASSES)

repl:
	@java $(LOX_PACKAGE).Lox 

ast:
	@cd $(TOOL_DIR) && javac GenerateAst.java
	@cd $(ROOT_DIR)
	@java $(TOOL_PACKAGE).GenerateAst $(LOX_DIR)

clean:
	@find . -name "*.class" -type f -delete