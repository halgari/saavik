# saavik

Saavik is a simple but useful Prolog interpreter for Clojure. Unlike minikanren or Datomic flavoured Datalog, Saavik
aims to be fully homoiconic and mostly compatible with Prolog. The syntax has changed but aside from that we aim to be a
full featured Prolog.

## Usage

### Repl

Simply run `lein run` inside the project directory to start a repl. From there it is easy to interact
with the Saavik database:


    Saavik REPL 0.1
    ---------------
    >>> (parent :jill :jane)
    >>> (parent :jane :sally)

    >>> ((grandparent grandparent grandchild)
          (parent grandparent parent)
          (parent parent grandchild))


    >>> ? (grandparent X Y)
    "Elapsed time: 3.409023 msecs"

    |     X |      Y |
    |-------+--------|
    | :jill | :sally |

    done

    >>> :exit

## License

Copyright © 2015 Timothy Baldridge

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
