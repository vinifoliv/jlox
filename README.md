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

# Running
You will need JDK to run this project. After installing and setting it up, clone this repo:

```shell
$ git clone git@github.com:vinifoliv/jlox.git
```

After it, go to the lox directory:
```shell
$ cd ./com/craftinginterpreters/lox
```

If you are on Linux and have make installed, just run `make`. Otherwise, compile it manually (the .java files order are explicit in the Makefile):

```shell
$ javac file1.java file2.java # Example
```

Running the project is as simple as getting back to the root directory and typing:

```shell
$ java com.craftinginterpreters.lox.Lox
```