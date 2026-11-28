# ai/

Everything AI-related in this template lives here. **If you don't use AI tools, you can ignore or delete this folder**, along with the root `AGENTS.md` symlink. Nothing else depends on them.

| File | What it is |
|---|---|
| `AGENTS.md` | Instructions that make an agent act as a tutor: it answers questions and gives hints, and doesn't change your code. |
| `skills/` | Optional skills for agents that support them. |

## How agents find it

Agents look for instruction files at the repository root. The root `AGENTS.md` is a symlink to `ai/AGENTS.md`.

| Agent | What it reads |
|---|---|
| Claude Code | `AGENTS.md`, when there's no `CLAUDE.md` (Claude Code v2.1.277 or later; [docs](https://code.claude.com/docs/en/memory)). On older versions: `ln -s ai/AGENTS.md CLAUDE.md` |
| Codex and other agents that read `AGENTS.md` | `AGENTS.md` |
| GitHub Copilot | If yours doesn't read `AGENTS.md`: `mkdir -p .github && ln -s ../ai/AGENTS.md .github/copilot-instructions.md` |

On Windows without symlink support, git checks out `AGENTS.md` as a one-line text file that contains the path `ai/AGENTS.md`.

These instructions are a request to the agent, not a lock. An agent that can run code in your REPL can still write files.
