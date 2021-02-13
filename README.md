# SimpleLisp
## Description
A simple language based on Scheme that is meant to fix how Scheme deals with
block structure and be easier to implement and compile.

## Q&A
Q: What does this application do?

A: Currently this application implements a REPL(Read Eval Print Loop) for a
programming based on Scheme.

Q: Who will use it?

A: Anyone who is interested in language design. The code that implements the
s-expression types and eval methods comes in at just under 600 lines of code
(at the time of writing). This makes it easy to modify or read to gain a deeper
understanding of how languages are implemented. Also the code that is not in the
ui package really implements a language for writing lisps in and then the ui package
uses this language to provide a definition of Simple Lisp. This makes this project
a nice way of quickly implementing a new language design.

Q: Why is this project interesting to me?

A: For the past year I have been fascinated by the design and implementation of
programming languages. The language I have implemented here is very similar to
the language I am writing a compiler for at the moment and so it is interesting
to see it come to life in an interpreter.

## Language
An overview of the language presented as a REPL session.
### Numeric Operators
```
> (+ 1 1)
2
> (- 1 10)
-9
> (* 3.5 2.0)
7.0
> (/ 10.0 2.0)
5.0
> (> 10 5)
true
> (< 10 5)
false
> (= 3 3)
true
> (>= 3 1)
true
> (<= 3 1)
false
```
### Pair Operators
```
> (cons 1 2)
(1 . 2)
> (cons 3 (cons 1 (cons 4 ())))
(3 1 4)
> (car (cons 2 7))
2
> (cdr (cons 2 7))
7
```
### Type Predicates
```
> (string? "Hello, World!")
true
> (string? 10)
false
> (bool? (string? 10))
true
```
### If Statements
```
> (if (> 1 2) "true" "false")
"false"
> (if (null? ()) "true" "false")
"true"
```
### Definitions
```
> (def x 10)
()
> x
10
```
### Begin Statement
```
> (def x 10)
()
> (begin (def x 20) x)
20
> x
10
```
### Lambdas
```
> (def succ (lambda (n) (+ n 1)))
()
> (succ 1)
2
```
### Recursion
```
> (def fac (lambda (n) (if (< n 1) 1 (* n (fac (- n 1))))))
()
> (fac 10)
3628800
```
### Quoting
```
> '1
1
> '(3 1 4 1)
(3 1 4 1)
> ''1
(quote 1)
> '(+ 1 2)
(+ 1 2)
```
### Set Statements
```
> (def x 10)
()
> (set! x 20)
()
> x
20
> (set! x (cons 1 2))
()
> (set-car! x 3)
()
> (set-cdr! x 4)
()
> x
(3 . 4)
```
### Closures
```
> (def const (lambda (n) (lambda () n)))
()
> (def ten (const 10))
()
> (ten)
10
```
### Simple Equality
```
> (eq? "Don't Panic" "Don't Panic")
true
> (eq? (cons 1 2) (cons 1 2))
false
```
