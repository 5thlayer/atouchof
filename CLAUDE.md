## Agent skills

The skills are mattpocock/skills, a git submodule at `.agents/mattpocock-skills` pinned to a release
tag. Upstream nests them by category, and Claude Code reads only `.claude/skills/<name>/SKILL.md`.
So `.agents/skills/<name>` is a symlink into the submodule for each skill the repo uses, and
`.claude/skills` is a symlink to `.agents/skills`. A fresh clone needs `git submodule update --init`,
or the links dangle. Every session runs it from `.claude/hooks/session-start.sh`. To take a new
release, check out its tag in the submodule and commit it. Then check that every
`.claude/skills/*/SKILL.md` still resolves, because a skill moved upstream breaks its link.

### Code review

Review code only with the mattpocock `code-review` skill (`.claude/skills/code-review/`). Never use
Claude Code's built-in `/code-review`, `/review`, `/security-review` or `/simplify` in its place.

### Issue tracker

Issues live in this repo's GitHub Issues. See `docs/agents/issue-tracker.md`.

### Triage labels

The five canonical triage roles, used verbatim as label strings. See `docs/agents/triage-labels.md`.

### Rejected scope

`.out-of-scope/` holds one file per rejected enhancement. See `.out-of-scope/README.md`.

### Domain docs

Single-context: `GLOSSARY.md` and `docs/adr/` at the repo root. See `docs/agents/domain.md`.

### Testing policy

Which check a feature warrants is decided by the claim it makes. See `docs/testing/what-to-check.md`.

### Code comments

A comment explains why the code is not the obvious code: a trap, a constraint from outside the
code, or a choice a reader would otherwise undo. Write the minimum that explains it, then name the
ADR or ticket that holds the reasoning, e.g. `(ADR-0002)`. Obvious code gets no comment. Keep
history, rejected alternatives, how a test was verified, and restatements of the code out of
comments.

### Don't Starve

Don't Starve is the reference for feel, never for numbers, names or art (ADR-0001). Do not look up,
copy or cite Klei's data files, stat values or asset names. Describe a mechanic in the suite's own
words and give it the suite's own numbers.
