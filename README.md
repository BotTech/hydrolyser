# Hydrolyser

Hydrolyser is a lexer for [Reactive Streams](http://www.reactive-streams.org/) (RS).

## Background

This project started out with the need to take some arbitrarily long JSON text and to extract some arbitrarily long
values. In this case JSON was possibly not the best format however it seemed an interesting problem and so Hydrolyser
was born.

### My Story

During my investigation of this problem I tried several different existing JSON parsers but none of them really did what
I want. My search was far from exhaustive and was complicated by the fact that there is already a concept called
[JSON Streaming](https://en.wikipedia.org/wiki/JSON_Streaming) which has a slightly different objective. JSON Streaming
is intended for an arbitrary number of JSON values where each value is a finite size.

I did not have any prior knowledge of language theory although I have worked quite a bit with various parsers in the
past and there is something I find interesting about parsing. So I used this project as a way to learn more and
hopefully get something out of it that is useful. At the very least I wanted to parse the large JSON.

I decided to play around with writing a simple JSON "parser" using
[Akka Streams](http://doc.akka.io/docs/akka/current/scala/index.html). The idea was pretty simple, I just took the
[ABNF](https://en.wikipedia.org/wiki/Augmented_Backus%E2%80%93Naur_form) from the
[JSON specification](https://tools.ietf.org/html/rfc7159) and looked at the first character to determine what the token
was and consumed all characters for that token and then repeated this.

Later on realised that what I was writing was really just a
[Scanner](https://en.wikipedia.org/wiki/Lexical_analysis#Scanner) with the
[Maximal Munch](https://en.wikipedia.org/wiki/Maximal_munch) principal and not a
[Parser](https://en.wikipedia.org/wiki/Parsing#Parser).

There was no buffering of input within a state except for literals (`true`, `false`, `null`) as they only require
constant space. This meant that values such as a string would actually be emitted as multiple string tokens which was
what allowed for unbounded values. It didn't care about validation such as balanced parenthesis and brackets, nor did it
worry about escape sequences or valid number formats. The thought at the time was that using an RS approach this could
all be taken care of downstream.

It was effectively a [Finite-State Machine](https://en.wikipedia.org/wiki/Finite-state_machine) where each state had a
function that took the next input and returned an optional token and the next state. The "buffering" was equivalent to
a finite number of states for each character. There was no "stack" since I had made the closing brackets and braces
optional.

For each new state I realised there was a bunch of common logic with the previous one and so kept refactoring this out.
Once I had implemented all the rules the code was pretty straightforward however I wondered whether it would be possible
to have a macro that I could just paste the ABNF and have it create these rules for me.

I was aware of the [Scala Standard Parser Combinator Library](https://github.com/scala/scala-parser-combinators). A
quick search showed many other implementations for generating code from an ABNF although none that allowed me to use my
own token classes. I stumbled across [parseback](https://github.com/djspiewak/parseback) and then the very interesting
paper that this was based on [Parsing with Derivatives](http://matt.might.net/papers/might2011derivatives.pdf) (PWD).

How was it that my naive implementation was able to handle this large JSON when, as far as I could tell, JSON was not a
[Regular Language](https://en.wikipedia.org/wiki/Regular_language)? Well, that was because I had unknowingly changed the
grammar from a [Context-free Grammar](https://en.wikipedia.org/wiki/Context-free_grammar) into a
[Regular Grammar](https://en.wikipedia.org/wiki/Regular_grammar), but yet it still did what I needed it to do. The
interesting thing for me was that I hadn't really changed the JSON language but instead used a more restricted grammar
to impose fewer restrictions on the language and make it easier to work with.

My goal was to explore this tradeoff and to create a tool that favours the practical choice.

## Principals

Favour practice over theory.
Assume users do not know any esoteric Functional Programming concepts.
- Ok: Immutability, Pure Functions, Higher-Order Functions, Apply, Map, Flatmap
- Not Ok: Monad, Functor, Applicative, Combinator, Lense
Require no knowledge of third party libraries (except of course in those modules).

## Requirements

Reactive Streams
Asynchronous
Non-blocking
Composable
Transformations
Lossless
Extractions

## Case Studies

### JSON

Intuition tells me that JSON is a context free language but not a regular language.

Consider the (slightly modified) `object` rule:
```$abnf
object       = begin-object [ members ] end-object
members      = member *( "," member )
begin-object = ws "{" ws
end-object   = ws "}" ws
ws           = " "
```

If we remove `ws` we end up with
```$abnf
object       = "{" [ members ] "}"
```

The `member` rule indirectly includes a `value` which can be an `object`. An `object` therefore is recursive and each
object must be between a pair of balanced braces. Anyone familiar with JSON will already be aware this. Objects are
nested and all the left braces must match up to a right brace at appropriate level. The same rule applies to `array`.

## Questions

* Is it not possible to show that the Dyck Language is not a regular language? How could there possibly be a
[Deterministic Finite Automata](TODO) (DFA) that describes this?
* Are there any rules which consist of three or more terms where one of the middle terms is a recursive non-terminal
which describe a regular language? E.g. `S = "x" S "x" | ɛ` (FIXME: Is term the correct word?)

## Theory

### Gribach's theorem

TODO: link

It is undecidable whether a given context free grammar describes a regular language.

## Observations

### Nesting

State tracking is required to go infinitely recursive/deep but that seems unlikely in practice. It seems far more
practical to go infinitely long/wide. Even if the data structure is conceptually infinitely recursive its
representation can often be flattened with a reference to its parent(s) and/or children.

Consider XML and JSON which can in theory be nested infinitely deep, in practice this is very unlikely to happen. First,
of all the program that is generating the string would suffer the same problems. Second, there is often a need to be
able to uniquely refer to a "thing" (FIXME) within the [Abstract Syntax Tree](TODO) such as by XPath or JSONPath. By
definition these references or paths must be finite and so there is already a limitation on the length of these. If
there is a limitation on the path then we can apply the same limitation to the level of recursion.

### Language Origin

While there are lots of difficult problems to solve with general languages most languages are written with a particular
uses in mind and these specifications are usually accompanied with reference implementations. Even though the
theoretical limits are interesting we can take a pragmatic approach and can impose some limitations on the language and
still be useful.

## Other Projects

I had heard there were a whole bunch of JSON libraries in Scala but I never realised the extent of the situation until I
started doing research for this project. On one hand I was hoping there was something that already did what I wanted but
on the other I was hoping there was a gap that I could contribute to. Not to mention that my intention was not just to
parse JSON but multiple languages.

There are JSON parsers which take some JSON text and give back some JSON AST. This is great but pretty boring. It also
means that the entire AST must be read into memory.

There are others that use fancy Functional Programming concepts and sound completely alien to a layperson and in my
opinion should not but the burden of theses details on their user. While they contain some really awesome ideas, which I
intend to copy, my goal is different.

TODO: Describe asyn vs blocking implementations.

### List (TODO: Rename)

TODO: Compare

https://github.com/circe/circe
https://github.com/circe/circe-fs2
https://github.com/knutwalker/akka-stream-json
https://github.com/scodec/scodec
https://github.com/scodec/scodec-stream
https://github.com/krasserm/streamz
https://github.com/functional-streams-for-scala/fs2
https://github.com/non/jawn
https://github.com/argonaut-io/argonaut
