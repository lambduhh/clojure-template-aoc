# ai/skills/

| Skill | Purpose |
|---|---|
| `grill-me` | Talks a problem through with you before you write code. |
| `setup-clojure` | Sets up Java, the Clojure CLI, Babashka and an editor, asking before each step, then explains what it installed and why. |
| `handoff` | Saves where you left off in `notes/handoffs/`, so a new session can pick up. |
| `clojure-tutor` | Planned: a Socratic tutor built from [ClojureBridge](https://github.com/ClojureBridge/curriculum) (CC BY 4.0) and Clojure Camp material. |

To use them with Claude Code: `mkdir -p .claude && ln -s ../ai/skills .claude/skills`.
