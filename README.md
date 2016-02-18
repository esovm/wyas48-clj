# wyas48-clj

A work-in-progress port of [Write Yourself a Scheme in 48 Hours](https://en.wikibooks.org/wiki/Write_Yourself_a_Scheme_in_48_Hours) to Clojure.

## Usage

To run the project, execute:

    $ lein run

This will place you in an interactive [REPL](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop):

    Scheme>>> (+ 3 4)
    7
    Scheme>>> ...

Press <kbd>C-c</kdb> to exit.

## Chapters Completed

- [x] Parsing
- [ ] Evaluation, Part 1
- [ ] Error Checking and Exceptions
- [ ] Evaluation, Part 2
- [ ] Building a REPL
- [ ] Adding Variables and Assignment
- [ ] Defining Scheme Functions: Closures and Environments
- [ ] Creating IO Primitives
- [ ] Towards a Standard Library: Fold and Unfold

## Differences in Approach

- Instead of using a parser combinator library, I used [Instaparse](https://github.com/Engelberg/instaparse) and wrote an [Extended Backus–Naur Form](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_Form) grammar.

## Planned Enhancements

- [ ] REPL Readline support
- [ ] Allow comments in grammar
- [ ] "Standard library"

## License

Copyright © 2016 Colin Drake

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
