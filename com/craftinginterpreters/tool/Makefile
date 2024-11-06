ROOT_DIR = ../../..
PACKAGE_PATH = com/craftinginterpreters/tool
CLASS_PATH = $(PACKAGE_PATH)/GenerateAst
OUTPUT_DIR = com/craftinginterpreters/lox

runw:
	@javac $(ROOT_DIR)/$(CLASS_PATH).java
	@cd "$(ROOT_DIR)" && java $(CLASS_PATH) $(OUTPUT_DIR)
	@del *.class

runl:
	@javac $(ROOT_DIR)/$(CLASS_PATH).java
	@cd "$(ROOT_DIR)" && java $(CLASS_PATH) $(OUTPUT_DIR)
	@rm *.class