# SimpleLisp
## BNF-ish
```
number := \d+(.\d+)?
symbol := [^\s]*
string := "([^"\\]|\\.)*"
char := '.'
expr :=
	| <number>
	| <symbol>
	| <string>
	| <char>
	| (<expr> <expr>*)
	| (quote <expr>)
	| (if <expr> <expr> <expr>)
	| (lambda (<symbol>*) <expr>)
	| (set! <symbol> <expr>)
	| (begin <statement>+)

statement :=
	| <expr>
	| (def ((<symbol> <expr)+))
	| (rec ((<symbol> <expr)+))
```
## Reasons why Java is incompetent as a language
For my own anger management.
+ Generics can't take in primitives
+ All default ways of nicely reading input are a complete mess and practically useless
+ ~~First class functions~~
+ Object systems make terrible type systems
+ Exceptions are clunky
+ Programming in Java feels like smashing your head into a bureaucratic wall.
