default: ast

ast:
	@cd com/craftinginterpreters/tool && javac GenerateAst.java
	@cd ../../..
	@java com.craftinginterpreters.tool.GenerateAst com/craftinginterpreters/lox

clean:
	@sudo find . -name "*.class" -type f -delete