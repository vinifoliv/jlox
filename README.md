# jlox
jlox is an interpreter for the Lox language, as designed and described by Robert Nystrom in Crafting Interpreters. This is not an implementation of my own but a reimplementation of Nystrom's one, according to what he presented along the book. 

# On the interpreter and its languages
jlox is built with Java and is not to be thought of as a fast and efficient interpreter. It accomplishes its job, and that is exactly the author's intention. As mentioned before, it runs Lox code. Lox was created by Nystrom himself for the purpose of the book and aims at being as featureful, as would the author say, as the book size allowed. Because of it, Lox presents both the functional and object-oriented paradigms, even though it can not be considered a complete object-oriented language. In terms of syntax, it resembles JavaScript, and the author mentions other languages as inspiration too.

# Example

```
var x = 1;

fun sum(x) {
    if (x < 5) {
        print x + 5; // => 6
    }
    else {
        innerSum();
    }

    fun innerSum() {
        x = x + 5;
        sum(x);
    }
}

class Car < Vehicle {
    init(model, year, color) {
        this.model = model;
        this.year = year;
        this.color = color;
    }

    drive() { // Notice that methods are not preceded by "fun"
        // code goes here...
    }
}

var fiesta = Car("GLi 2024", 2023, "Black"); // No keyword new is used
print fiesta.color; // => Black
fiesta.drive();
```

# Running the project
__Requirements__
- JDK;
- `make`

This project was built on a WSL Ubuntu environment, but you should be able to run it on Windows, provided that you compile all of the project classes by hand or install `make` on Windows. If you prefer to install `make`, change the `clean` rule in the Makefile to `Get-ChildItem -Recurse -Filter *.class | Remove-Item -Force` so as to run it on PowerShell.

__Steps__
1. Clone this repo

```shell
$ git clone git@github.com:vinifoliv/jlox.git
```

2. Run `make` inside the root directory
```shell
$ make
```

If you do not have `make` installed, compile it manually:

```shell
$ javac TokenType.java    \
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
        Interpreter.java  \
        Resolver.java     \
        Lox.java
```

3. Run it

__REPL__
```shell
$ java com.craftinginterpreters.lox.Lox
```

__Script__
```shell
$ java com.craftinginterpreters.lox.Lox path/to/script
```