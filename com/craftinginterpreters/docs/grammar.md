```BNF
program     -> declaration* EOF ;

declaration -> varDecl
             | statement;

varDecl     -> "var" IDENTIFIER ( "=" expression )? ";";

statement   -> printStmt
             | exprStmt ;

printStmt   -> "print" expression ";" ;

exprStmt    -> expression ";" ;

expression  -> equality ;

equality    -> comparison ( ( "!=" | "==" ) comparison )* ;

comparison  -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;

term        -> factor ( ( "+" |  "-" ) factor )* ;

factor      -> unary ( ( "*" | "/" ) unary )* ;

unary       -> ( "!" | "-" ) unary | primary ;

primary     -> "true" | "false" | "nil"
             | "NUMBER" | STRING
             | "(" expression ")"
             | IDENTIFIER ;
```