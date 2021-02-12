# SimpleLisp
## Description
A simple language based on Scheme that is meant to fix how Scheme deals with
block structure and be easier to implement and compile. This version has some
special powers that will not work in my other implementations of this language.


## Language
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
### Definitions
```
> (def x 10)
()
> x
10
```
