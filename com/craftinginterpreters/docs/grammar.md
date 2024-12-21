```BNF
program     -> declaration* EOF ;

declaration -> varDecl
             | statement;

varDecl     -> "var" IDENTIFIER ( "=" expression )? ";";

statement   -> exprStmt
             | printStmt ;

primary     -> "true" | "false" | "nil"
             | "NUMBER" | STRING
             | "(" expression ")"
             | IDENTIFIER ;
```