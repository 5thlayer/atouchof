---
status: provisional
---

# A Touch Of is a suite of standalone Pillars

A Touch Of brings Don't Starve's survival pressure into an ordinary Minecraft 26.1.2 / NeoForge
world. FactoryWorks reached its shape only after starting as an overhaul pack and splitting it
(FactoryWorks ADR-0101, ADR-0115, ADR-0127, ADR-0128). This suite starts from where FactoryWorks ended up.

**Decision.**

- **A Touch Of is a suite of Pillars.** Each Pillar is a mod that owns one danger and plays alone in vanilla:
  A Touch of Darkness (`atouchofdarkness`), A Touch of Madness (`atouchofmadness`) and A Touch of
  Wickedness (`atouchofwickedness`). No Pillar requires another. A Pillar's mod id is `atouchof`
  followed by its danger. Darkness ships first.
- **Cold and heat are left to Tough As Nails.** It already does on Minecraft 26.1.2 / NeoForge
  nearly all that A Touch of Cold (`atouchofcold`) was to do, so that Pillar is shelved, not dropped
  (atouchofcold ADR 0002). It is revisited if Tough As Nails is abandoned, or if upstream turns down
  a change the suite needs. The suite recommends Tough As Nails for cold and heat, and no Pillar
  requires it.
- **`5thlayer/atouchof` is the suite's home.** It holds the glossary, the ADRs that touch more than one Pillar,
  and the parent issue of every cross-Pillar topic. Each Pillar has its own repo, created from
  the libworks template, with its own glossary and ADRs for what only it touches.
- **Don't Starve is the reference for feel, never for numbers, names or art.** No repo holds or
  reads Klei's data, there is no corpus, and nothing carries a Klei name, even one Klei took from
  older folklore: Wickedness's punisher is the Bailiff, not Krampus. Every number is the
  suite's own and is tuned in play.
- **Licences are per file, as in FactoryWorks (ADR-0102 there).** Code, and the files a program
  reads as instructions, are LGPL-3.0-only. The suite's own prose and art are CC BY 4.0. Vendored
  material keeps its licence. `REUSE.toml` maps each file to its licence.
- **An ADR is `provisional` until its Pillar has been played and the decision has held up** (FactoryWorks
  ADR-0042). It is then promoted to `accepted`, with the play-test named.

**Considered.** An overhaul pack that is split up later: FactoryWorks paid for that path across
five ADRs. Don't Starve Together mods in Lua, and a standalone game: neither could use libworks,
NeoForge capabilities or the GameTest harness, and those are the experience this suite reuses. A
private corpus of Klei's data: FactoryWorks built one and then had to move it out of every public
repo (FactoryWorks ADR-0103, ADR-0126).

**Amended** (2026-10-27). A Touch of Cold is shelved in favour of Tough As Nails (#5), and A Touch
of Wickedness joins as the third Pillar (#6).
