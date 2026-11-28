<!-- DRAFT: tutorial pending Jordan's review. Remove this line when endorsed. -->

# Tutorial: from zero to your first passing test

This tutorial takes you from an empty machine to a REPL connected to your editor. Then you'll read a worked example, make a test go from red to green, handle a part 2 that changes the question, and take a quick tour of the how-to examples. It takes about an hour.

Along the way, the tutorial aims to show you three things Clojure gives you. **Focus**: you work inside a running program instead of building, running and waiting. **Power**: a change stays where you make it instead of rippling through what already works. **Joy**: what the first two add up to, when you can stay on the problem and trust what you've built.

You don't need to know any Clojure yet. When a command or a setting isn't explained here, the [README](../README.md) has the full reference.

## 1. Get a working environment

Pick **one** of these. If you're new to installing developer tools, the [setup flowchart](setup-flowchart.md) walks through each option step by step.

**A. No installs (Codespaces).** On GitHub, open this repository and choose **Use this template → Open in a codespace**. A codespace made this way isn't saved to a repository until you publish it. Or click **Use this template** to make your own copy first, then on your copy choose **Code → Codespaces → Create codespace**. Wait for the setup to finish. It installs Java, the Clojure CLI, Babashka, and the [Calva](https://calva.io) editor extension. Then skip to [step 2](#2-start-a-repl-and-connect-your-editor).

**B. Install locally.** Install:

1. Java 21 or newer.
2. The [Clojure CLI](https://clojure.org/guides/install_clojure).
3. [Babashka](https://babashka.org). This tutorial uses its `bb` commands, and `bb new-day` has no plain-`clojure` equivalent.
4. An editor with Clojure support. If you use VS Code, install [Calva](https://calva.io).

Check that everything works by running this from the repo root:

```sh
bb test
```

The first run downloads the libraries, so it can take a minute. It ends with a count of tests run, then:

```
0 failures, 0 errors.
```

If you don't have Babashka, `clojure -X:test` does the same thing.

## 2. Start a REPL and connect your editor

The **REPL** (Read–Eval–Print Loop) is a running Clojure program you send code to, one expression at a time. Clojure programmers keep one open the whole time they work. Most of this tutorial happens there.

Instead of writing a whole program, running it and waiting to find out what happened, you talk to the program while it runs: change one piece, evaluate it, and see the result straight away. That's where the focus comes from.

In Calva, open the Command Palette and run **Calva: Start a Project REPL and Connect (aka Jack-In)**. Choose the `deps.edn` project type. Calva then asks you to "Pick any aliases to launch with": leave them all unticked and press <kbd>Enter</kbd>. Some aliases, such as `:test`, start another program, like the test runner, instead of a REPL. Calva remembers what you pick for next time. Calva starts the REPL and connects to it.

If you'd rather start the REPL yourself, or you use another editor:

```sh
bb repl
```

This starts a REPL in the terminal, plus an nREPL server for your editor. It writes the server's port to `.nrepl-port`. Then use your editor's "connect to a running REPL" command. In Calva, that's **Calva: Connect to a Running REPL Server in the Project**.

Check it's working: type `(+ 1 2)` in any `.clj` file, put your cursor right after the closing `)`, and evaluate it. In Calva, press <kbd>Ctrl</kbd>+<kbd>Enter</kbd>. You should see `3`.

## 3. Read the example day

Open [`src/aoc/day00.clj`](../src/aoc/day00.clj). It solves a made-up puzzle (it isn't from Advent of Code) and shows the shape every day in this template takes:

| Name | What it is |
|---|---|
| `example` | The small sample input from the puzzle text, as a string |
| `parse` | Turns the input string into Clojure data |
| `part-1`, `part-2` | Each takes the input string and returns an answer |
| `(comment ...)` | A block the compiler ignores, holding expressions for you to evaluate one at a time |

First load the file into the REPL. In Calva, run **Calva: Load/Evaluate Current File and its Requires/Dependencies**. Then go to the `comment` block at the bottom and evaluate the first three expressions in it:

```clojure
(parse example)
;=> ({:name "alice", :score 3} {:name "bob", :score 5} {:name "alice", :score 4} {:name "carol", :score 1})

(part-1 example) ;=> 13
(part-2 example) ;=> "alice"
```

The last expression in the block, `(part-1 (input/slurp-day 0))`, reads `inputs/day00.txt`. The template doesn't include that file, so it throws `No input at inputs/day00.txt`. It's there to show how a real day reads its input, which [step 5](#5-use-your-real-puzzle-input) covers.

You can also call the helper functions `part-1` and `part-2` use. Type this into the `comment` block and evaluate it:

```clojure
(totals (parse example)) ;=> {"alice" 7, "bob" 5, "carol" 1}
```

That's the core habit: **build up an answer one small expression at a time, checking each result as you go.** If a function you haven't seen before comes up, such as `fnil` or `max-key`, look it up on [ClojureDocs](https://clojuredocs.org).

Each result is plain data, maps and lists you can read, so there's no hidden state to piece together. When the data gets too big to read in the editor, the `:portal` alias opens it in a browser (see the [README](../README.md#aliases)).

If the parentheses are the first thing you notice, keep going. Each pair marks an expression you can evaluate on its own, and that's what makes this habit work.

## 4. Make a test go from red to green

Every day comes with a test, and the test checks your code against the example answer from the puzzle text. Now create a day of your own:

```sh
bb new-day 42
```

This creates `src/aoc/day42.clj` and `test/aoc/day42_test.clj`. (Advent of Code has no day 42, so this practice day won't get in the way of a real one.) Run the tests:

```sh
bb test
```

You should see a failure. That's expected:

```
FAIL in (example) (day42_test.clj:7)
expected: (= :answer-from-the-puzzle (day/part-1 day/example))
  actual: (not (= :answer-from-the-puzzle nil))
```

`part-1` is empty, so it returns `nil`. And the test is still waiting for you to fill in the real expected answer.

Day 42 is a practice day, so solve this made-up warm-up:

> Each line of the input is one word. **Part 1:** how many words are longer than 3 letters?
>
> Example input:
> ```
> cat
> horse
> ox
> zebra
> ```
> The answer for the example is `2`.

To do it:

1. In `src/aoc/day42.clj`, set `example` to the example input above, one word per line inside the string.
2. In `test/aoc/day42_test.clj`, replace `:answer-from-the-puzzle` with `2`.
3. Write `part-1` in the REPL first. Load the new file into the REPL: in Calva, run **Calva: Load/Evaluate Current File and its Requires/Dependencies**. Each time you change `example`, evaluate it again so the REPL sees the new value. Evaluate `(parse example)` to see what you're working with, then build up the answer one step at a time. You'll probably want `filter` and `count`.
4. Run `bb test` again until it reports `0 failures, 0 errors`.

<details>
<summary>Stuck? One possible answer</summary>

```clojure
(defn part-1 [s]
  (count (filter (fn [word] (> (count word) 3)) (parse s))))
```

`(fn [word] ...)` makes a function without a name. `filter` calls it on each word and keeps the words it returns true for.
</details>

### Part 2 changes the question

Once part 1 passes, Advent of Code shows you part 2. It usually asks something new about the same input. That's the moment code either builds on what it has or starts fighting it. Here's the warm-up's part 2:

> **Part 2:** what's the total number of letters in the words longer than 3 letters? For the example, that's `10` (`horse` and `zebra`).

1. In `test/aoc/day42_test.clj`, add a second check inside `deftest example`: `(is (= 10 (day/part-2 day/example)))`. Run `bb test`: it's red again.
2. Both parts need the same rule: a word is long if it has more than 3 letters. Move that rule out of `part-1` into a function with a name, `long-word?`, and use it in both parts.
3. Run `bb test` until both checks pass.

<details>
<summary>Stuck? One possible answer</summary>

```clojure
(defn long-word? [word]
  (> (count word) 3))

(defn part-1 [s]
  (count (filter long-word? (parse s))))

(defn part-2 [s]
  (reduce + (map count (filter long-word? (parse s)))))
```

The `?` at the end of `long-word?` is a Clojure convention for a function that returns true or false.
</details>

Here's everything part 2 changed:

- **`long-word?`**: new. It holds the rule both parts share.
- **`part-1`**: one line changed. `(fn [word] (> (count word) 3))` became `long-word?`.
- **`part-2`**: filled in.
- **The test**: one new check, `(is (= 10 (day/part-2 day/example)))`.

`parse`, `example` and part 1's check didn't change, and the next `bb test` told you that part 1 still held. Now that's what I call power (puzzle-sized). Real puzzles are bigger, but the habit carries over: keep `parse` returning plain data, and give the parts small functions they can share.

That red → green loop is how every day goes: paste the example, write the expected answer into the test, then work in the REPL until the test passes.

## 5. Use your real puzzle input

When a real puzzle comes out, say Day 1, run `bb new-day 1` and follow the same loop. Once the example test passes, run your code on your own input. Save it as `inputs/day01.txt` and evaluate this in `src/aoc/day01.clj`:

```clojure
(part-1 (input/slurp-day 1))
```

If the file isn't there, you'll get `No input at inputs/day01.txt. Save your puzzle input there.`

`inputs/` is git-ignored. Advent of Code asks that you don't publish your inputs ([FAQ](https://adventofcode.com/2026/about)).

## 6. Tour the how-tos

[`src/howto/`](../src/howto/) holds short, working examples of things puzzles tend to need. Each one is a namespace with a `comment` block, just like `day00`. Open a file, load it, and evaluate the expressions. The `;=>` comments show the results you should get, and [`test/howto/examples_test.clj`](../test/howto/examples_test.clj) checks that they're correct.

| File | Shows you how to | Reach for it when a puzzle says... |
|---|---|---|
| [`json_csv.clj`](../src/howto/json_csv.clj) | Pull numbers out of a line with a regex, read comma-separated lines as data, and read JSON and real CSV | ...each line is a list of numbers, or the input is JSON |
| [`parsing.clj`](../src/howto/parsing.clj) | Parse structured lines with an [instaparse](https://github.com/Engelberg/instaparse) grammar | ...the input is a list of instructions in a small made-up language |
| [`combinatorics.clj`](../src/howto/combinatorics.clj) | Generate combinations, permutations, Cartesian products, and subsets | ..."every pair", "every ordering", or "try all choices" |
| [`graphs.clj`](../src/howto/graphs.clj) | Do a breadth-first search by hand, then use [loom](https://github.com/clj-commons/loom) for paths, connected components, and topological sort | ..."fewest steps", "which groups are connected", or "A must happen before B" |
| [`shortest_path.clj`](../src/howto/shortest_path.clj) | Find the cheapest path with Dijkstra's algorithm and a priority map | ..."lowest risk" or "cheapest route", where steps cost different amounts |
| [`hashing.clj`](../src/howto/hashing.clj) | Compute MD5 and SHA-256 hashes, with a library and with plain Java | ...find a hash that starts with some zeros |
| [`channels.clj`](../src/howto/channels.clj) | Chain processes together with core.async channels | ...machines that feed their output into each other |
| [`errors.clj`](../src/howto/errors.clj) | Read an error message and trace it to the line that caused it | ...your code throws an error and you're not sure why |
| [`datomic.clj`](../src/howto/datomic.clj) | Load data into an in-memory Datomic database and query it with Datalog | ...you want to ask questions of your data, like a database (optional; most puzzles don't need it) |

One more how-to, `core.async.flow`, lives in `flow/` because it needs the `:flow` alias. The [README](../README.md#aliases) explains how to use it.

Try changing an example and seeing what happens. For instance, in `shortest_path.clj`, change `example-grid`, evaluate it again, then re-evaluate the `(dijkstra ...)` call in the `comment` block. You can't break anything that `git checkout` can't fix.

## Where to go next

- [ClojureDocs](https://clojuredocs.org) has community examples for every core function.
- The [clojure.org guides](https://clojure.org/guides/getting_started) cover the language and its tools. [Programming at the REPL](https://clojure.org/guides/repl/introduction) and the [Deps and CLI guide](https://clojure.org/guides/deps_and_cli) go deeper on the REPL and the `clojure` command.
- Calva's [Evaluate Clojure Code](https://calva.io/eval-tips/) page shows more ways to evaluate from the editor.
- The Babashka book's [task runner](https://book.babashka.org/#tasks) chapter explains how `bb.edn` tasks work.
- The [README](../README.md) lists every command, alias, and library in this template.
- If you use an AI assistant, [`AGENTS.md`](../AGENTS.md) asks it to act as a tutor: it should give hints instead of solving the puzzle for you.
