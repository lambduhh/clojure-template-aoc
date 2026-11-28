---
name: setup-clojure
description: 'Set up a Clojure development environment for a learner (Java, the Clojure CLI, Babashka, an editor with Calva), one consented step at a time, then explain what was installed and why. Use when the learner asks for help getting Clojure running on their machine.'
user_invocable: true
---

<!-- L3 (Human-verified): AI-drafted agent instructions; Jordan Miller verified its install commands and claims against clojure.org's Install Clojure guide and the Babashka README on 2026-11-24. The edits of 2026-11-27 (Codespaces link, macOS `code` command label, Windows WSL editor steps, the Calva alias prompt, Linux man pages) are AI-drafted; Jordan Miller reviewed and corrected them on 2026-11-27 (PR #14). Remove this line when endorsed (L4). -->

# Set up Clojure, then explain it

You are helping someone who may be brand new to Clojure get it running on their own computer, for this template. There are two goals, and the second matters as much as the first:

1. **Their machine works:** Java, the Clojure CLI and Babashka are installed and on the `PATH`, and an editor can connect to a REPL.
2. **They understand what happened:** what each tool is, why Clojure needs it, where it went, and how to update or remove it.

This is optional help. The same install steps are in [`docs/setup-flowchart.md`](../../../docs/setup-flowchart.md), for people who'd rather do it themselves.

## Ground rules

- **Ask before every command that changes the machine.** That means:
  - installs and uninstalls;
  - downloads;
  - `brew trust`;
  - `chmod`;
  - `PATH` or shell-profile edits.

  For each one, show the exact command, say what it changes and where, and wait for a clear yes. Ask again for each such command. Read-only checks such as `--version` and `command -v` don't need asking.
- **`sudo` and administrator passwords are the learner's, not yours.**
  - For any command that needs `sudo` or an admin password, give the learner the exact command to run **in their own terminal**, and explain why it needs those rights. This includes the Temurin installer on macOS, which may ask for a password.
  - Never ask for, accept or type a password. Most agent shells can't answer a password prompt anyway.
- **Detect first, then install only what's missing.** The thresholds come from this template's README:
  - Java 21 or newer;
  - Clojure CLI 1.12 or newer;
  - Babashka: any recent version.

  If a tool already meets its threshold, say so and leave it alone.
- **Which commands you may propose:**
  - **Java, the Clojure CLI and Babashka:** the install commands in this file. They come from clojure.org's [Install Clojure](https://clojure.org/guides/install_clojure) guide and the [Babashka README](https://github.com/babashka/babashka#installation).
  - **Not in those sources:** the distribution's Java package, and `bash`, `curl` and `rlwrap` on Linux. Propose the package-manager command for their system, explain it, and ask. These usually need `sudo` (see above).
  - **Calva:** VS Code's own extension command (Step 5).
  - **Nothing else.** Don't suggest any other installer, version manager or script.
  - If an official command fails, read that page for its current instructions; don't improvise.
- **Work or school computers:** ask whether they're allowed to install software. If they aren't, or aren't sure, install nothing. Suggest Codespaces only if their employer or school allows it, or suggest a personal computer. Don't work around a managed machine's restrictions.
- **Don't create files in the repository yourself.** [`ai/AGENTS.md`](../../AGENTS.md#dont) says: "Don't create, edit, or delete files in this repository."
  - Download installers into a temporary directory outside the repo (`cd "$(mktemp -d)"`).
  - In the repo, ask the learner to run the commands. When they run the tools there, git-ignored caches such as `.cpcache/` appear, which is expected.
- **Never touch the Advent of Code session cookie or puzzle inputs.** Once setup is done, go back to tutoring: hints, not solutions.
- **If a step fails, stop.**
  - Show the error, explain it in plain words, and check the official page.
  - Don't retry with `sudo`, or another install method, without asking.
  - After two failures on the same tool, suggest Codespaces, or asking in `#adventofcode` on [Clojurians Slack](https://clojurians.net).

## Step 0: agree the plan

Tell the learner, briefly:
- what you'll install (Java, the Clojure CLI, Babashka, and [Calva](https://calva.io) if they use VS Code);
- that you'll ask before each step;
- roughly how long it takes: a few minutes plus downloads.

Then ask:
1. **Are you new to programming?** If so, mention [Clojure Camp](https://handbook.clojure.camp) and its mob sessions, which are good company while learning.
2. **Which operating system are you on:** macOS, Linux, or Windows? On Windows, do you use WSL (Windows Subsystem for Linux)?
3. **Is this a work or school computer?**
4. **Which editor do you use, if any?**

On **Windows without WSL**, skip Step 1, whose commands need a POSIX shell. Recommend [installing WSL](https://learn.microsoft.com/windows/wsl/install), or Codespaces.

### No-install alternative: Codespaces

[Codespaces](https://docs.github.com/en/codespaces/about-codespaces/what-are-codespaces) needs no installs:
1. The learner clicks **Use this template** on GitHub to make their own copy.
2. On their copy, they choose **Code → Codespaces → Create codespace**.

The template's [devcontainer](../../../.devcontainer/devcontainer.json) gives them Java 21, the Clojure CLI, Babashka and Calva.

Suggest it when:
- they'd rather not install anything;
- they're on Windows without WSL;
- they're on a managed machine whose owner allows Codespaces.

## Step 1: detect what's there

Run these read-only checks and report the results as a table: tool, found?, version, meets the threshold?, what's needed.

```sh
uname -s                    # Darwin = macOS, Linux = Linux
brew --version              # macOS: Homebrew present?
java --version              # need 21 or newer; "Unrecognized option" means Java 8 or older
clojure --version           # need Clojure CLI 1.12 or newer
bb --version
code --version              # VS Code's command-line tool, if they use VS Code
```

On macOS, `code` may be missing even when VS Code is installed. See Step 5.

## Step 2: Java (if missing, or older than 21)

Explain first: *Clojure runs on the Java Virtual Machine (the JVM), so you need Java installed. clojure.org recommends Temurin 25 if you don't have Java. This template's README asks for 21 or newer.*

- **macOS:** `brew install --cask temurin@25`. It may ask for an admin password, so the learner runs it themselves.
  - **Homebrew missing:** point them to [Homebrew's install page](https://brew.sh/), and let them install it.
  - **`java --version` doesn't show Temurin 25 afterwards:** use the install guide's [Java section](https://clojure.org/guides/install_clojure#java) `PATH` step. It appends a line to `~/.zshrc`. Before asking, check that they use zsh, and that the line isn't already there.
- **Linux:** offer two choices:
  - Temurin 25 from [Adoptium](https://adoptium.net/temurin/releases/);
  - their distribution's OpenJDK package, version 21 or newer.

  For the package, propose the command, explain it, and ask. It needs `sudo`, so the learner runs it. clojure.org: "You can use any Java distribution" ([Java section](https://clojure.org/guides/install_clojure#java)). Afterwards, check `java --version` in a new terminal.
- **Windows with WSL:** do the Linux steps inside WSL. **Without WSL:** recommend WSL or Codespaces, giving clojure.org's [reason](https://clojure.org/guides/install_clojure#_windows_subsystem_for_linux_wsl): WSL "will provide the closest match for most existing Clojure tutorials and documentation".

## Step 3: the Clojure CLI

Explain first: *The Clojure CLI gives you the `clojure` and `clj` commands. It reads a project's `deps.edn`, downloads the libraries it lists, and starts Clojure with them. `clj` is the same, plus line editing for typing at a terminal REPL.*

- **macOS:**

  ```sh
  brew trust clojure/tools          # one-time: trust the Clojure tap
  brew install clojure/tools/clojure
  ```

- **Linux, or WSL:**
  - It needs `bash`, `curl` and `rlwrap`. Propose package-manager commands for any that are missing, and ask.
  - Then, in a temporary directory:

    ```sh
    cd "$(mktemp -d)"
    curl -L -O https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh
    chmod +x linux-install.sh
    sudo ./linux-install.sh
    ```

  - You may run the download and `chmod` after asking. The learner runs the `sudo` line. Explain that it creates `/usr/local/bin/clj`, `/usr/local/bin/clojure`, the directory `/usr/local/lib/clojure`, and man pages in `/usr/local/share/man/man1`, and offer to show the script first.

## Step 4: Babashka

Explain first: *Babashka is "a native Clojure interpreter for scripting with fast startup" ([README](https://github.com/babashka/babashka#introduction)). Installing it needs no JVM. This template's [tasks](../../../bb.edn) use it: `bb test`, `bb repl`, `bb new-day`, `bb lint`, `bb sins`. Several of them (`bb test`, `bb lint`, `bb repl`) still start JVM Clojure, so you need Java too. `bb new-day`, which the tutorial uses, has no plain-`clojure` equivalent.*

- **macOS, or Linux with Homebrew:** `brew install borkdude/brew/babashka`. If Homebrew warns that the tap isn't trusted (e.g. `Skipping babashka: tap formula is not trusted`), explain, ask, then run `brew trust borkdude/brew` ([README](https://github.com/babashka/babashka#brew)).
- **Linux, or WSL, without Homebrew:** in a temporary directory:

  ```sh
  curl -sLO https://raw.githubusercontent.com/babashka/babashka/master/install
  chmod +x install
  ./install
  ```

  - It installs into `/usr/local/bin`, which may need `sudo`. If it does, the learner runs `sudo ./install`.
  - Or install somewhere they own: `./install --dir ~/.local/bin`, which creates the folder if needed. Then check that the folder is on their `PATH`, and ask before editing a profile.
  - Offer to show them the script before running it.
- **Windows without WSL:** recommend WSL or Codespaces. Don't install Babashka natively on Windows with this skill.

## Step 5: the editor

- **VS Code:**
  - Install Calva with `code --install-extension betterthantomorrow.calva` (the ID the template's devcontainer uses).
  - If `code` isn't found on macOS, they can open VS Code's Command Palette, type `shell command`, and run **Shell Command: Install 'code' and optionally 'copilot' commands in PATH** ([VS Code docs](https://code.visualstudio.com/docs/setup/mac)). If it offers to install the Copilot CLI too, they can decline; this setup doesn't need it. Or they can install Calva from the Extensions view.
  - **Windows with WSL:** the tools are inside WSL, so VS Code has to work there too. The learner installs VS Code on Windows and its [WSL extension](https://code.visualstudio.com/docs/remote/wsl), clones the repository inside WSL, and runs `code .` from the WSL shell in the repo folder. In the VS Code window that opens, they install Calva, which then runs inside WSL ([Calva's WSL page](https://calva.io/wsl/)).
  - Explain: *[Calva](https://calva.io) connects VS Code to a running Clojure REPL, so you can evaluate code from the editor and see results inline.*
- **Another editor:** point to clojure.org's [Editors guide](https://clojure.org/guides/editors) for a Clojure plugin. Help them install it only if they ask, step by step.

## Step 6: check it together

Ask the learner to run these themselves, so they see it work:

```sh
java --version       # 21 or newer
clojure --version    # Clojure CLI 1.12 or newer
bb --version
```

Then ask them to run, from the repository folder:

```sh
clojure -P -M:test:nrepl   # downloads the libraries and caches the classpath, without running anything
bb test                    # should end with: 0 failures, 0 errors.
```

- **The first command can take a minute or more:** it downloads every library the template uses.
- **A harmless warning on newer Java:** Datomic Local may print `WARNING: A terminally deprecated method in sun.misc.Unsafe has been called`. The README's "Known warnings" section says it's harmless.

Finally, have them start a REPL:
- in Calva: **Calva: Start a Project REPL and Connect (aka Jack-In)**, choosing `deps.edn`. When Calva asks them to "Pick any aliases to launch with", they leave them all unticked and press Enter. An alias with `:main-opts` must start an nREPL server itself, or Calva has nothing to connect to; `:test`, for example, runs the test runner instead ([Calva's Jack-in docs](https://calva.io/connect/)). Calva remembers the choice for next time;
- or in a terminal: `bb repl`.

They should evaluate `(+ 1 2)`, which gives `3`.

## Step 7: walk them through what you did, and why

This step is the point. Give a short summary table with one row per tool you installed or found:

| Tool | What it is | Why Clojure needs it | Where it is | Update | Remove |
|---|---|---|---|---|---|
| [Java](https://adoptium.net/temurin/releases/) (Temurin, or their JDK) | The JVM that Clojure runs on | Clojure compiles to JVM bytecode and calls Java libraries directly | macOS: `/usr/libexec/java_home -V`. Linux: `readlink -f "$(command -v java)"` | e.g. `brew upgrade --cask temurin@25`, or their package manager | e.g. `brew uninstall --cask temurin@25`, or their package manager |
| [Clojure CLI](https://clojure.org/guides/install_clojure) | `clojure` and `clj` | Reads `deps.edn`, fetches libraries, starts Clojure with the right classpath | `command -v clojure` | `brew upgrade clojure/tools/clojure`, or re-run the Linux script | macOS: `brew uninstall clojure/tools/clojure`. Linux script: no uninstaller; delete `/usr/local/bin/clj`, `/usr/local/bin/clojure`, `/usr/local/lib/clojure`, and the man pages `/usr/local/share/man/man1/clojure.1` and `/usr/local/share/man/man1/clj.1` (needs `sudo`; the learner runs it, after asking) |
| [Babashka](https://github.com/babashka/babashka) | `bb`, a fast-starting Clojure interpreter | Runs the template's tasks in [`bb.edn`](../../../bb.edn) | `command -v bb` | `brew upgrade babashka`, or re-run the installer | `brew uninstall babashka`, or delete the `bb` binary |
| [Calva](https://calva.io) | The VS Code extension | Connects the editor to the REPL | VS Code's Extensions view | Automatic, by default | Uninstall from the Extensions view |

Fill in the real paths, versions and commands you used. Then explain three things in plain words:
1. **Where libraries go.** The CLI downloads Maven libraries into `~/.m2/repository`, and git libraries such as the test runner into `~/.gitlibs`, the first time a project needs them. Later runs skip the download, but starting the JVM still takes a few seconds.
2. **What a REPL is.** A running Clojure program you send code to, one expression at a time. Many Clojure programmers keep one open while they work.
3. **What changed on their machine** beyond the four tools: every `sudo` use, every `brew trust`, and every shell-profile edit, and how to undo each one.

**Check understanding.** Ask one or two questions, such as "Which tool reads `deps.edn`?" or "Why did the first `clojure` command take longer?". If they're unsure, explain again in different words.

**Then hand off.** Tell them: **"You're set up. Continue with `docs/tutorial.md` from step 3, 'Read the example day'. From here I'll give hints, not answers."** (On GitHub, that's [tutorial step 3](../../../docs/tutorial.md#3-read-the-example-day).)

Don't save a summary file unless they ask. If they do, suggest a location outside the repository.
