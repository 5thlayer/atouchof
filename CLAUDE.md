# A Touch Of

The suite's home (see `README.md`), and the Runtime's: the small mod `atouchof` every Pillar nests jar-in-jar, which draws the Pillars' meters and holds no mechanic (ADR-0002, ADR-0005). Its mod project is the repo root, built with the libworks convention plugin like a Pillar.

## Workflow

Commit on the current branch; open a feature branch only when the user asks for one. Nothing is pushed without the user's word.

A change to the Runtime's behaviour gets a review with the `code-review` skill (below). Doc and plumbing changes skip it: `CLAUDE.md`, `GLOSSARY.md`, ADRs, `docs/`, `.claude/`, submodule bumps, and tooling or CI config.

## Commits

Conventional commits: `<type>(<optional scope>): <summary>`, with the summary in the imperative and lower case. The types in use are `feat`, `fix`, `refactor`, `test`, `docs`, `build`, `ci` and `chore`. A breaking change marks its type with `!`, and its release bumps the minor (libworks ADR 0001). A commit that closes an issue ends its body with `Closes #<n>`.

## Testing

- `sh ./gradlew build` runs the JUnit tests, on a plain JVM with no Minecraft.
- `sh ./gradlew runGameTestServer` runs the game tests headless, a real player on a real server, and names each one it ran; it fails if it ran none.
- CI (`.github/workflows/ci.yml`) runs both through libworks' `module-ci.yml` on every push and never publishes.
- Everything that exists only for the Runtime's own runs lives in the `gametest` source set, which never ships (libworks ADRs 0004 and 0011). `ATouchOfGameTests` hooks itself in from `FMLConstructModEvent`, and `src/main` never names game-test code; where shipped code must report to a test, `main` holds a hook with no test vocabulary, such as `MeterSync.onSend`.
- A new game test class is registered by a line in `ATouchOfGameTests.registerTests`, and its tests stand on the `gametest/platform` structure that the plugin's `buildGametestStructures` task writes.
- Neither run loads a client, so a change to how a meter is drawn isn't done when the tests pass: list it for the user to check in the dev client, where `/atouchof_meters` turns on the two dev meters of `DevMeters`.
- Vanilla's code, with NeoForge's patches, is in `build/moddev/artifacts/minecraft-patched-*-sources.jar`; check a claim about vanilla or NeoForge behaviour there before stating it.

## Releases

A change a Pillar can use or will notice adds its line under `## Unreleased` in `CHANGELOG.md` as it lands. Before bumping `mod_version`, publishing to `~/.m2` or tagging a release, read `docs/agents/releases.md`: releases go through `sh ./gradlew release`, and a published version never changes. The Runtime stays backward compatible within a major version, since jar-in-jar loads the highest nested copy (ADR-0002).

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
