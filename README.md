<!-- DRAFT: AI-drafted reference; pending Jordan's review (target L4). Remove this line when endorsed. -->

# clojure-template-aoc

A starter Clojure project with a REPL, tests, and the libraries puzzle solving usually needs. It's built for [Advent of Code](https://adventofcode.com/) but works for any small Clojure project.

This README is a **reference**. For a guided start, see the tutorials on the hub `[link TBD]`.

## Requirements

| Tool | Version | Notes |
|---|---|---|
| Java | 21 or newer | |
| [Clojure CLI](https://clojure.org/guides/install_clojure) | 1.12 or newer | |
| [Babashka](https://babashka.org) | any recent | Optional; runs the `bb` tasks below |
| Editor | any | [Calva](https://calva.io) for VS Code is preinstalled in the devcontainer |

Or skip installing: open the repo in a [devcontainer or GitHub Codespace](#devcontainer).

## Commands

Run from the repo root. Each `bb` task has a plain `clojure` equivalent.

| Command | Equivalent | What it does |
|---|---|---|
| `bb repl` | `clojure -M:nrepl` | Start a REPL with an nREPL server that editors can connect to |
| `bb test` | `clojure -X:test` | Run all tests |
| `bb lint` | `clojure -M:lint` | Lint `src/` and `test/` with clj-kondo |
| `bb new-day 1` | | Create `src/aoc/day01.clj` and a test that fails until you fill in the example's answer |
| `bb tasks` | | List the `bb` tasks |

Other commands:

| Command | What it does |
|---|---|
| `clojure -M:1.13:nrepl` | Start a REPL on the Clojure 1.13 alpha |
| `clojure -M -e '(clojure-version)'` | Print the Clojure version |

## Aliases

Defined in [`deps.edn`](deps.edn).

| Alias | Adds | Use for |
|---|---|---|
| `:test` | `test/` on the classpath, [test-runner](https://github.com/cognitect-labs/test-runner), and [test.check](https://github.com/clojure/test.check) | Running tests, including property-based tests |
| `:nrepl` | `test/` on the classpath, and [nREPL](https://nrepl.org) | A terminal REPL your editor can connect to |
| `:1.13` | Clojure 1.13.0-alpha11 instead of 1.12.6 | Trying 1.13 features. Combine with other aliases, e.g. `-M:1.13:nrepl` |
| `:lint` | [clj-kondo](https://github.com/clj-kondo/clj-kondo) 2026.10.26 | Linting |
| `:portal` | [Portal](https://github.com/djblue/portal) 0.68.1 | Inspecting data in a browser, e.g. `-M:portal:nrepl` |
| `:bench` | [Criterium](https://github.com/hugoduncan/criterium) 0.4.6 | Benchmarking, e.g. `(criterium.core/quick-bench ...)` |
| `:flow` | core.async 1.10.874-alpha3 instead of 1.9.865, plus `flow/` and `flow-test/` | `core.async.flow`, which ships only in core.async alphas. Run its test with `clojure -M:flow:test -d flow-test` |
| `:clerk` | [Clerk](https://github.com/nextjournal/clerk) 0.18.1158 | Notebooks, e.g. `(nextjournal.clerk/serve! {:browse true})` |

Aliases combine, e.g. `clojure -M:portal:clerk:nrepl`.

## Dependencies

| Library | Version | Namespace | For |
|---|---|---|---|
| [Clojure](https://clojure.org) | 1.12.6 | `clojure.core` | The language |
| [data.json](https://github.com/clojure/data.json) | 2.5.2 | `clojure.data.json` | Reading and writing JSON |
| [data.csv](https://github.com/clojure/data.csv) | 1.1.1 | `clojure.data.csv` | Reading and writing real CSV, e.g. to and from spreadsheets. Puzzle input is rarely CSV; `src/howto/json_csv.clj` shows the idioms that fit it |
| [math.combinatorics](https://github.com/clojure/math.combinatorics) | 0.3.2 | `clojure.math.combinatorics` | Combinations, permutations, products, subsets |
| [math.numeric-tower](https://github.com/clojure/math.numeric-tower) | 0.1.1 | `clojure.math.numeric-tower` | `gcd`, `lcm`, exact `expt` and `sqrt` |
| [data.priority-map](https://github.com/clojure/data.priority-map) | 1.2.1 | `clojure.data.priority-map` | Priority queues, e.g. for shortest paths |
| [core.match](https://github.com/clojure/core.match) | 1.1.1 | `clojure.core.match` | Pattern matching |
| [Medley](https://github.com/weavejester/medley) | 1.10.0 | `medley.core` | Small utility functions missing from `clojure.core` |
| [instaparse](https://github.com/Engelberg/instaparse) | 1.5.0 | `instaparse.core` | Parsing input with a grammar |
| [core.async](https://github.com/clojure/core.async) | 1.9.865 | `clojure.core.async` | Channels and processes. `core.async.flow` needs the `:flow` alias |
| [loom](https://github.com/aysylu/loom) | 1.2.4 | `loom.graph`, `loom.alg` | Graph algorithms: paths, connected components, topological sort, cliques, max flow. Excludes `tailrecursion/cljs-priority-map`, which only ClojureScript needs. `src/howto/graphs.clj` also shows BFS by hand |
| [digest](https://github.com/clj-commons/digest) | 1.4.100 | `clj-commons.digest` | MD5, SHA and other hashes as plain functions. Java's built-in `java.security.MessageDigest` does the same with interop; `src/howto/hashing.clj` shows both |
| [Datomic Local](https://docs.datomic.com/datomic-local.html) | 1.0.301 | `datomic.client.api` | A Datalog database, in memory or on disk. Free; binaries are Apache 2.0 |

## Layout

```
deps.edn               dependencies and aliases
bb.edn                 babashka tasks
inputs/                your puzzle inputs (git-ignored; see below)
src/aoc/input.clj      reads inputs/dayNN.txt
src/aoc/day00.clj      a made-up example day: example, parse, part-1, part-2
src/howto/             one namespace per how-to (JSON and CSV, combinatorics, shortest paths, Datomic,
                       parsing with instaparse, hashing, core.async channels, graphs)
flow/, flow-test/      the core.async.flow how-to and its test (:flow alias only)
test/                  tests; test/howto/ checks every result shown in src/howto/
.devcontainer/         devcontainer and Codespaces setup
ai/                    optional AI tooling; safe to delete (see ai/README.md)
AGENTS.md              symlink to ai/AGENTS.md, so agents find it
```

## Inputs

Save each day's input as `inputs/dayNN.txt`, e.g. `inputs/day01.txt`. Read it with `(aoc.input/slurp-day 1)`.

`inputs/` is git-ignored except for `.gitkeep`. Advent of Code asks that repositories not include puzzle text or inputs ([FAQ](https://adventofcode.com/2025/about)).

## Devcontainer

[`.devcontainer/devcontainer.json`](.devcontainer/devcontainer.json) uses the `mcr.microsoft.com/devcontainers/java:1-21` image, installs the Clojure CLI and Babashka, downloads the dependencies, and adds Calva. In GitHub, use **Code → Codespaces → Create codespace**.

## Known warnings

On newer JVMs, Datomic Local prints `WARNING: A terminally deprecated method in sun.misc.Unsafe has been called`. It comes from one of its dependencies (caffeine) and is harmless.

## AI

AI tooling is optional and lives in [`ai/`](ai/README.md). The root `AGENTS.md` is a symlink to `ai/AGENTS.md`, so agents find it. The instructions ask agents to act as a tutor: answer questions and give hints, but not change your code. To opt out, delete `ai/` and `AGENTS.md`.

## License

[MIT](LICENSE).
