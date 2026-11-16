# A Touch Of

The Runtime: the small mod `atouchof` every Pillar nests jar-in-jar, which draws the Pillars' meters and holds no mechanic (ADR-0002, ADR-0005). Its mod project is the repo root, built with the libworks convention plugin like a Pillar.

## Workflow

Commit on the current branch; open a feature branch only when the user asks for one. Nothing is pushed without the user's word.

A change to the Runtime's behaviour gets a review with the `code-review` skill (below). Doc and plumbing changes skip it: `CLAUDE.md`, `GLOSSARY.md`, ADRs, `docs/`, `.claude/`, and tooling or CI config.

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

The skills are mattpocock/skills, as the `mattpocock-skills` plugin from upstream's own marketplace,
which follows its latest release, and the skillworks plugin. `.claude/settings.json` enables both, so
a session here, in a worktree or in the cloud gets them with nothing vendored. The plugin also brings
`implement` and `diagnosing-bugs`: use skillworks' ones, `skillworks:implement` and
`skillworks:diagnosing-bugs`.

### Code review

Review code only with the mattpocock `code-review` skill (`/mattpocock-skills:code-review`). Never use
Claude Code's built-in `/code-review`, `/review`, `/security-review` or `/simplify` in its place.

### Issue tracker

Issues live in this repo's GitHub Issues. See `docs/agents/issue-tracker.md`.

### Triage labels

The five canonical triage roles, used verbatim as label strings. See `docs/agents/triage-labels.md`.

### Domain docs

The suite's glossary, its ADRs (cited as "A Touch Of ADR-NNNN", or "ADR-NNNN" here) and its rejected scope are in the private Home Repo, `~/5thlayer/home/atouchof/`, with the suite's rules in its `AGENTS.md`. Read them there; this repo has no `GLOSSARY.md` or `docs/adr/` of its own.

### Testing policy

Which check a feature warrants is decided by the claim it makes. See `docs/testing/what-to-check.md`.

### Code comments

A comment explains why the code is not the obvious code: a trap, a constraint from outside the
code, or a choice a reader would otherwise undo. Write the minimum that explains it, then name the
ADR or ticket that holds the reasoning, e.g. `(ADR-0002)`. Obvious code gets no comment. Keep
history, rejected alternatives, how a test was verified, and restatements of the code out of
comments.
