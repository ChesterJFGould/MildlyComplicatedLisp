# SimpleLisp
## Description
A simple language based on Scheme that is meant to fix how Scheme deals with
block structure and be easier to implement and compile.

## Table of Contents
+ [Q&A](#QA)
+ [Language](#Language)
+ [User Stories](#User-Stories)
+ [TODO](#TODO)

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
```scheme
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
```scheme
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
```scheme
> (string? "Hello, World!")
true
> (string? 10)
false
> (bool? (string? 10))
true
```
### If Statements
```scheme
> (if (> 1 2) "true" "false")
"false"
> (if (null? ()) "true" "false")
"true"
```
### Definitions
```scheme
> (def x 10)
()
> x
10
```
### Begin Statement
```scheme
> (def x 10)
()
> (begin (def x 20) x)
20
> x
10
```
### Lambdas
```scheme
> (def succ (lambda (n) (+ n 1)))
()
> (succ 1)
2
```
### Recursion
```scheme
> (def fac (lambda (n) (if (< n 1) 1 (* n (fac (- n 1))))))
()
> (fac 10)
3628800
```
### Quoting
```scheme
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
```scheme
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
```scheme
> (def const (lambda (n) (lambda () n)))
()
> (def ten (const 10))
()
> (ten)
10
```
### Simple Equality
```scheme
> (eq? "Don't Panic" "Don't Panic")
true
> (eq? (cons 1 2) (cons 1 2))
false
```
### Macros
```scheme
> (def list (lambda a a))
()
> (def defun
       (macro (args body)
              (list 'def (car args) (list 'lambda (cdr args) body))))
> (defun (add a b) (+ a b))
()
> (add 1 2)
3
```
### Save and Load
"save" saves the environment it is called in to the given slo file in the data directory.
"load" merges the environment saved in the given slo file with the current environment.
Because these can be called anywhere they probably can do some really cool things but may
also break some stuff.
```scheme
> (def x 10)
()
> x
10
> (save "xSave")
()
```
```scheme
> x
Error : undefined variable x
> (load "xSave")
()
> x
10
```
## User Stories
+ As a user, I want to be able to `cons` mulitple elements onto a list.
```scheme
> (def append (lambda (lst1 lst2)
                      (if (null? lst1)
                          lst2
                          (cons (car lst1)
                                (append (cdr lst1) lst2)))))
()
> (def multi-cons (lambda (lst . vals)
                          (append vals lst)))
()
> (multi-cons '(4 5 6) 1 2 3)
(1 2 3 4 5 6)
```
+ As a user, I want to be able to create a to-do list and then add a task to it.
```scheme
> (def to-do ())
()
> (set! to-do (cons "Create a to-do list" to-do))
()
> to-do
("Create a to-do list")
```
+ As a user, I want to compute 10!.
```scheme
> (def !
       (lambda (n)
               (if (< n 1)
                   1
                   (* n (! (- n 1))))))
()
> (! 10)
3628800
```
+ As a user, I want the `+` operator to be curried.
```scheme
> (def sl-+ +)
()
> (def +
       (lambda (a) (lambda (b) (sl-+ a b))))
> (def succ (+ 1))
()
> (succ 2)
3
```
+ As a user, I want to add a lazy form to the language.
```scheme
> (def list (lambda a a))
()
> (def lazy (macro (arg) (list 'lambda () arg)))
()
> (def force (lambda (f) (f)))
()
> (def lazy-ten (lazy 10))
()
> (force lazy-ten)
10
```
+ As a user, I want to use `define` instead of `def` to define variables so my Scheme
syntax highlighter will highlight my define forms.
```scheme
> (def define def)
()
> (define foo 'bar)
()
> foo
bar
```
+ As a user, I want to be able to save my important fibonacci calculator to the
fibCalc.slo file in the data directory.
```scheme
> (def fib (lambda (n) (if (< n 2) 1 (+ (fib (- n 1)) (fib (- n 2))))))
()
> (fib 10)
89
> (save "fibCalc")
()
```
+ As a user, I want to be able to load my previously saved fibonacci calculator.
```scheme
> (fib 10)
Error : Undefined variable fib
> (load "fibCalc")
()
> (fib 10)
89
```

## TODO
+ Abstract Lambda and Macro.
+ Rewrite eval in the State monad.
+ Rewrite the parser in the Parser monad.
+ Rewrite methods that could fail in the Maybe or Either monad.
+ Sexpr could probably become generic and then most subclasses could be deleted.
+ Implement a Saveable interface for JSON save/load.
