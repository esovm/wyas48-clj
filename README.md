# wyas48-clj ![Build badge](https://travis-ci.org/cfdrake/wyas48-clj.svg?branch=master)

A work-in-progress port of [Write Yourself a Scheme in 48 Hours](https://en.wikibooks.org/wiki/Write_Yourself_a_Scheme_in_48_Hours) to Clojure.

## Usage

### Running the REPL

To run the project, execute:

    $ lein run

This will place you in an interactive [REPL](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop):

```
Scheme>>> (+ 3 4)
7
Scheme>>> (if (&& #t #f)
    ... >   "yep"
    ... >   "nope")
"nope"
Scheme>>> ...
```

Press <kbd>C-c</kbd> or type `quit` to exit.

### Built-ins

`if` and `quote` are implemented as special forms.

In addition, the following function primitives are also implemented:

`||` `&&` `*` `+` `-` `/` `/=` `<` `<=` `=` `>` `>=` `car` `cdr` `cons` `eq?`
`eqv?` `equal?` `mod` `number?` `quotient` `remainder` `string->symbol` `string<=?`
`string<?` `string=?` `string>=?` `string>?` `string?` `symbol->string` `symbol?`

## Status

### Tutorial Chapters Completed

- [x] Parsing
- [x] Evaluation, Part 1
- [x] Error Checking and Exceptions
- [ ] Evaluation, Part 2
- [x] Building a REPL
- [ ] Adding Variables and Assignment
- [ ] Defining Scheme Functions: Closures and Environments
- [ ] Creating IO Primitives
- [ ] Towards a Standard Library: Fold and Unfold

### Differences in Approach

- Instead of using a parser combinator library, I used [Instaparse](https://github.com/Engelberg/instaparse) and wrote an [Extended Backus–Naur Form](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_Form) grammar.
- I took a test-driven approach for developing this. Obviously, this wasn't the focus (or a fault) of the original tutorial.

### Enhancements

- [x] Readline-like experience in the REPL (via [JLine](http://jline.sourceforge.net/))
- [x] Barebones command completion in the REPL
- [ ] Allow comments in grammar
- [ ] Standard library

## License

Copyright © 2016 Colin Drake

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
