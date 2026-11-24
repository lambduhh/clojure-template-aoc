---
name: setup-clojure
description: 'Set up a Clojure development environment for a learner (Java, the Clojure CLI, Babashka, an editor with Calva), one consented step at a time, then explain what was installed and why. Use when the learner asks for help getting Clojure running on their machine.'
user_invocable: true
---

<!-- DRAFT: AI-drafted agent instructions; pending Jordan's review. Remove this line when endorsed. -->

# Set up Clojure, then explain it

You are helping someone who may be brand new to Clojure get it running on their own computer, for this template. There are two goals, and the second matters as much as the first:

1. **Their machine works:** Java, the Clojure CLI and Babashka are installed and on the `PATH`, and an editor can connect to a REPL.
2. **They understand what happened:** what each tool is, why Clojure needs it, where it went, and how to update or remove it.

This is optional help. Everything here is also in [`docs/setup-flowchart.md`](../../../docs/setup-flowchart.md), for people who'd rather do it themselves.

## Ground rules

- **Ask before every change.** Before each install command:
  - show the exact command;
  - say what it installs, and where;
  - wait for a clear yes.

  Never run installs silently or in a batch.
- **Detect first, then install only what's missing.** If something is already there and recent enough, say so and leave it alone.
- **Use only the official commands below.** They come from clojure.org's [Install Clojure](https://clojure.org/guides/install_clojure) guide and the [Babashka README](https://github.com/babashka/babashka#installation). If one fails, read that page for its current instructions; don't improvise. Don't use third-party installers, version managers or scripts the learner didn't ask for.
- **Ask separately before using `sudo` or administrator rights, and before editing a shell profile** (`~/.zshrc`, `~/.bashrc`, …). Say why it's needed.
- **Work or school computers:** ask whether they're allowed to install software. If they aren't sure, stop and suggest Codespaces (below). Don't work around a managed machine's restrictions.
- **Don't create, edit or delete files in this repository.** That rule comes from [`ai/AGENTS.md`](../../AGENTS.md).
  - Download installers into a temporary directory outside the repo, such as `mktemp -d`.
  - Running the tools creates caches such as `.cpcache/`, which are git-ignored. That is expected.
- **Never touch the Advent of Code session cookie or puzzle inputs.** Once setup is done, go back to tutoring: hints, not solutions.
- **If a step fails, stop.**
  - Show the error, explain it in plain words, and check the official page.
  - Don't retry with `sudo`, or another install method, without asking.
  - After two failures on the same tool, suggest Codespaces, or asking in `#adventofcode` on Clojurians Slack (sign up at clojurians.net).

## Step 0: agree the plan

Tell the learner, briefly:
- what you'll install (Java, the Clojure CLI, Babashka, and Calva if they use VS Code);
- that you'll ask before each step;
- roughly how long it takes: a few minutes plus downloads.

Then ask:
1. Which operating system are you on: macOS, Linux, or Windows? On Windows, do you use WSL?
2. Is this a work or school computer?
3. Which editor do you use, if any?

**No-install alternative: Codespaces.** On GitHub, open this repository and choose **Code → Codespaces → Create codespace**. Its devcontainer installs Java 21, the Clojure CLI, Babashka and Calva. Suggest it when:
- they're on a work machine;
- they're on Windows without WSL;
- they'd rather not install anything.

## Step 1: detect what's there

Run these read-only checks and report the results as a table: tool, found?, version, what's needed.

```sh
uname -s                    # Darwin = macOS, Linux = Linux
brew --version              # macOS: Homebrew present?
java --version              # need 21 or newer for this template
command -v clojure clj bb   # which are on the PATH
bb --version
code --version              # VS Code's command-line tool, if they use VS Code
```

Don't run `clojure -M -e …` yet. Its first run downloads files, and it belongs in Step 6.

## Step 2: Java (if missing, or older than 21)

Explain first: *Clojure runs on the Java Virtual Machine (the JVM), so you need Java installed. Clojure supports Java's long-term-support releases, and clojure.org recommends Temurin 25 if you don't have one. This template needs 21 or newer.*

- **macOS:** `brew install --cask temurin@25`. If Homebrew is missing, point them to https://brew.sh/ and let them install it; don't run that installer for them unless they ask. If `java --version` doesn't show Temurin 25 afterwards, follow the install guide's `PATH` step, and ask before editing `~/.zshrc`.
- **Linux:** offer two choices:
  - Temurin 25 from https://adoptium.net/;
  - their distribution's OpenJDK package, version 21 or newer, through their package manager. Propose the exact command for their distribution, and explain it.

  clojure.org: "You can use any Java distribution".
- **Windows with WSL:** do the Linux steps inside WSL. **Without WSL:** recommend WSL or Codespaces, using clojure.org's reason that WSL "will provide the closest match for most existing Clojure tutorials and documentation".

## Step 3: the Clojure CLI

Explain first: *The Clojure CLI gives you the `clojure` and `clj` commands. It reads a project's `deps.edn`, downloads the libraries it lists, and starts Clojure with them. `clj` is the same, plus line editing for typing at a terminal REPL.*

- **macOS:**

  ```sh
  brew trust clojure/tools          # one-time: trust the Clojure tap
  brew install clojure/tools/clojure
  ```

- **Linux, or WSL:**
  - It needs `bash`, `curl` and `rlwrap`. Install any that are missing with their package manager, and ask first.
  - Then, in a temporary directory:

    ```sh
    curl -L -O https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh
    chmod +x linux-install.sh
    sudo ./linux-install.sh
    ```

  - Ask before the `sudo` line. Explain that the script installs `clojure` and `clj` system-wide.

## Step 4: Babashka

Explain first, quoting its README: *Babashka "is a native Clojure interpreter for scripting with fast startup", with "No JVM needed". This template uses it for its tasks: `bb test`, `bb repl`, `bb new-day`, `bb lint`. `bb new-day`, which the tutorial uses, has no plain-`clojure` equivalent.*

- **macOS, or Linux with Homebrew:** `brew install borkdude/brew/babashka`. If Homebrew says the tap isn't trusted, run `brew trust borkdude/brew` (from the Babashka README).
- **Linux, or WSL, without Homebrew:** in a temporary directory:

  ```sh
  curl -sLO https://raw.githubusercontent.com/babashka/babashka/master/install
  chmod +x install
  ./install
  ```

  - It installs into `/usr/local/bin`, which may need `sudo`; ask first.
  - Or install somewhere they own, such as `./install --dir ~/.local/bin`. Check that the directory is on their `PATH`, and ask before editing a profile.
  - Offer to show them the script before running it.
- **Windows without WSL:** the README's route is scoop. It may need `Set-ExecutionPolicy RemoteSigned -scope CurrentUser`. Explain what that setting changes before suggesting it. Recommend WSL or Codespaces first.

## Step 5: the editor

- **VS Code:** `code --install-extension betterthantomorrow.calva` (the ID the template's devcontainer uses). Explain: *Calva connects VS Code to a running Clojure REPL, so you can evaluate code from the editor and see results inline.*
- **Another editor:** point to https://clojure.org/guides/editors for its Clojure plugin. Help them install it only if they ask, step by step.

## Step 6: check it together

Ask the learner to run these themselves, so they see it work:

```sh
java --version
clojure -M -e '(clojure-version)'    # first run downloads files; give it a minute
bb --version
```

Then, from the repository folder:

```sh
clojure -P -M:test:nrepl   # -P "prepares": downloads the libraries without running anything
bb test                    # should end with: 0 failures, 0 errors.
```

Finally, have them start a REPL:
- in Calva: **Calva: Start a Project REPL and Connect (aka Jack-In)**, choosing `deps.edn`;
- or in a terminal: `bb repl`.

They should evaluate `(+ 1 2)`, which gives `3`.

## Step 7: walk them through what you did, and why

This step is the point. Give a short summary table with one row per tool you installed or found:

| Tool | What it is | Why Clojure needs it | Where it is | Update | Remove |
|---|---|---|---|---|---|
| Java (Temurin, or their JDK) | The JVM that Clojure runs on | Clojure compiles to JVM bytecode and calls Java libraries directly | Output of `command -v java` | e.g. `brew upgrade --cask temurin@25` | e.g. `brew uninstall --cask temurin@25` |
| Clojure CLI | `clojure` / `clj` | Reads `deps.edn`, fetches libraries, starts Clojure with the right classpath | `command -v clojure` | `brew upgrade clojure/tools/clojure`, or re-run the Linux script | The matching uninstall for how it was installed |
| Babashka | `bb`, a fast-starting Clojure interpreter | Runs the template's tasks in `bb.edn` | `command -v bb` | `brew upgrade babashka`, or re-run the installer | Delete the `bb` binary, or `brew uninstall babashka` |
| Calva | The VS Code extension | Connects the editor to the REPL | VS Code's Extensions view | Automatic | Uninstall from the Extensions view |

Fill in the real paths, versions and commands you used. Then explain three things in plain words:
1. **Where libraries go.** The CLI downloads libraries into `~/.m2/repository` the first time a project needs them. That's why the first run is slow and later runs aren't.
2. **What a REPL is.** A running Clojure program that you send code to, one expression at a time. Clojure programmers keep one open while they work.
3. **What changed on their machine** beyond the four tools: every `sudo` use and every shell-profile edit, and how to undo each one.

**Check understanding.** Ask one or two questions, such as "Which tool reads `deps.edn`?" or "Why did the first `clojure` command take longer?". If they're unsure, explain again in different words.

Then hand off: **"You're set up. Continue with the [tutorial](../../../docs/tutorial.md) from step 3. From here I'll give hints, not answers."**

Don't save a summary file unless they ask. If they do, suggest a location outside the repository.
