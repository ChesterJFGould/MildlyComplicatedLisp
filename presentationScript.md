Hello my name is Chester an I'll be presenting my project
"Mildly Complicated Lisp".

This project consists of an implementation of a lisp dialect, for those of you
who took CS 110 BSL was a lisp dialect, along with full environment serialization
(that includes bindings, procedures, closures, and all that fun stuff),
as well as a GUI for runtime modification of the environment.

Now I'll show you a quick demo of what I built.

It might no be entirely obvious what we are looking at right now but at the
bottom here we have a terminal to a REPL session. So we can do something simple
like 1 + 1 and we get our answer 2. Since this is a full programming environment
we can try something slightly more complicated such as defining a factorial
function. We'll just use the standard non tail-recursive definition.
Now this is where the top part of the interface comes in. As you can
see our factorial function shows up here, however lets define something
that has a better visual representation. We'll just define x to be 10 and now
you can see that this also shows up in the list. Now the fun part is that we can
mess with our environment from the interface. If we double click x we can input
a new value, instead of a number lets try something a bit more exotic like a
cons pair. As you can see the value of x has been updated. If we then right
click x we can rename the variable, lets try pair. Now to fully demonstrate the
serialization capabilities of this project I'll define a new function called
const. Const will take in some value and return a closure that returns that
value when called. Now we can use const to define a new value ten which when called will
return 10. To save the current environment we call save with a file name to save the current
environment to a file, and if we just quickly close and re-open the program and
call load on that same file we will see that everything we previously defined
will be back and will work properly.

There are a lot of things we didn't have time show in the demo, but I will just
quickly go through a few of them. Everything is a first class value so we can
do something like map "if" over three lists to make a choice between the last
two based on the first. This also means we can rename anything which is shown by this example.
As well as lambdas we also have macros, the basics of which can be seen in this
definition of a short circuiting "or" procedure. Macros can also be used to
implement the much coveted "eval" function as shown here as well as the apply
function whose implementation is a bit more involved so it is not shown here.

I really enjoyed writing this project as it was basically what I was planning
to do on my own time during this term anyways, however there are a few things
that I would change if I started over again.

Number one is that I would save the current continuation instead of the current
environment. This would also have the side-effect of making the tail call
optimization easy to implement as well as introducing the ability for first
class continuations.

Number two would be to use Monads. This would have been much cleaner than without
them and would have almost certainly reduced the amount of code.

Thank you for time, I hope you enjoyed this presentation.
