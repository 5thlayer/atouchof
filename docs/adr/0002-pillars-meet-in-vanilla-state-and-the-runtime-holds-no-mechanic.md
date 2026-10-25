---
status: provisional
---

# Pillars meet in vanilla state, and the Runtime holds no mechanic

The Pillars are meant to interact: the Dark should drain Sanity, and Cold should drain Sanity too.
Each Pillar must still play alone (ADR-0001), so none can call another.

**Decision.**

- **A Pillar reads and writes only vanilla state.** That means light level, time of day and moon
  phase, weather, biome temperature, vanilla's Freezing counter, and vanilla items and tags.
  A Touch of Madness drains Sanity on low light without knowing A Touch of Darkness exists.
  A Touch of Cold drives vanilla's Freezing, so leather armour, powder snow and any other mod
  that reads Freezing react to Cold. An interaction that vanilla state cannot carry needs an ADR
  of its own before it gets a published API.
- **A Pillar's recipes name only vanilla items, tags with a vanilla member, or the Pillar's own
  items, and they use only vanilla recipe types** (FactoryWorks ADR-0127). A recipe that removes a vanilla one is an
  exception and needs an ADR (ADR-0003).
- **The Runtime, `atouchof`, is a small mod each Pillar nests jar-in-jar**, so players install
  nothing extra. It owns the HUD layout of the Pillars' meters, per-player stat attachments and their
  sync, and shared config plumbing. **It never owns a mechanic.** A Pillar registers its meter, and
  the Runtime only arranges and draws it, so a lone Pillar looks as if the Runtime were not there.
- **The Runtime stays backward compatible within a major version.** Jar-in-jar loads the highest
  nested version, so an older Pillar runs against the newest Runtime in the pack.
- **The Runtime and the Pillars build with the libworks plugin**, but they do not nest
  `libworks-runtime`, which is FactoryWorks-shaped (the Works tab, machine screens, Jade). The
  plugin's plays-alone scan needs a Module list per suite, so it checks each Pillar against the
  other Pillars (libworks ADR 0005).

**Considered.** A published API jar that every Pillar soft-depends on, for each interaction: it
is a contract to version for interactions vanilla state already carries. A required base mod
(the Groundworks pattern): that is the base that FactoryWorks ADR-0127 dissolved. Hosting the HUD in
`libworks-runtime`: every FactoryWorks Module would nest survival code it never uses, and the two
suites would share a release line.
