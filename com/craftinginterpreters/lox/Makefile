# Variables
JC = javac
JVM = java
JFLAGS = -g # generates debug info
MAIN = Lox

.SUFFIXES: .java .class

# Compiles every .java file into .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = TokenType.java Token.java Scanner.java Lox.java

default: classes

# String replacement
classes: $(CLASSES:.java=.class)

# Running
run: $(MAIN).class
	$(JVM) $(MAIN)

# Cleaning
clean:
	rm -f *.class