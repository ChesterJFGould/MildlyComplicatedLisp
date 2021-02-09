# SimpleLisp
## BNF-ish
`
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
`
