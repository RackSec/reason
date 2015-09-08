# reason

[![Build Status](https://travis-ci.org/RackSec/reason.svg?branch=master)](https://travis-ci.org/RackSec/reason)

> "I'm sure they'll listen to reason."
>
> (Fisheye, *Snow Crash* by Neal Stephenson)

A library for producing simple predicate functions from textual user input.

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

## Reporting bugs/development

> "Fine," Hiro says. "Now, what about Reason?"
>
> Ng mumbles something and a card appears in his hand. "Here's a new
> version of the system software," he says. "It should be a little
> less buggy."
>
> "A little less?"
>
> "No piece of software is ever bug free," Ng says.
>
> (*Snow Crash*, Neal Stephenson)

Please submit issues and pull requests [on Github][gh].

[gh]: https://github.com/RackSec/reason

## License

Copyright © 2015 Laurens Van Houtven

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
