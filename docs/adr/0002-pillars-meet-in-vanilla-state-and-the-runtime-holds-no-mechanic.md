---
status: provisional
---

# Pillars meet in vanilla state, and the Runtime holds no mechanic

The Pillars are meant to interact: the Dark should drain Sanity, and so should freezing.
Each Pillar must still play alone (ADR-0001), so none can call another.

**Decision.**

- **A Pillar reads and writes only vanilla state.** That means light level, time of day and moon
  phase, weather, biome temperature, vanilla's Freezing counter, villager gossip, and vanilla items
  and tags. A Touch of Madness drains Sanity on low light without knowing A Touch of Darkness
  exists, and on Freezing without knowing what froze the player: powder snow, or Tough As Nails at
  its coldest. An interaction that vanilla state cannot carry needs an ADR of its own before it
  gets a published API (ADR-0004).
- **A Pillar may add entries to another Pillar's tag, and only optionally.** It ships a vanilla tag
  file under the other Pillar's namespace with `"replace": false` and every entry
  `"required": false`. Without the other Pillar the tag is never read, so neither requires the
  other. This adds data, not a call. The first use: A Touch of Wickedness adds its Bailiff to
  `#atouchofmadness:unsettling`, so a Bailiff nearby drains Sanity like any hostile mob (#6).
- **A Pillar may call Tough As Nails, through its public API only.** That is
  `toughasnails.api.temperature.TemperatureHelper`, as an optional `compileOnly` dependency whose
  code runs only when `toughasnails` is loaded, and lives in the Pillar's own compat package, not
  the Runtime. No mixins into Tough As Nails, and nothing copied from it (it is All Rights
  Reserved). The Pillar still plays alone. The first use: A Touch of Darkness's Campfire warms and
  its soul Campfire cools while they burn (#5, atouchofdarkness#13).
- **A Pillar's recipes name only vanilla items, tags with a vanilla member, or the Pillar's own
  items, and they use only vanilla recipe types** (FactoryWorks ADR-0127). A recipe that removes a vanilla one is an
  exception and needs an ADR (ADR-0003).
- **The Runtime, `atouchof`, is a small mod a Pillar nests jar-in-jar**, so players install
  nothing extra. It owns the HUD layout of the Pillars' meters, the display data each Pillar hands
  it and that data's sync, the neutral signals (ADR-0004), and shared config plumbing. **It never
  owns a mechanic, nor a stat:** a Pillar's stat, such as Sanity, is the Pillar's own player
  attachment. A Pillar registers its meter and sets what it shows (ADR-0005), and the Runtime only
  arranges and draws it, so a lone Pillar looks as if the Runtime were not there. **A Pillar nests
  the Runtime only once it uses it**: A Touch of Wickedness draws no meter and raises no signal, so
  it doesn't nest it yet (atouchofwickedness ADR 0002).
- **The Runtime stays backward compatible within a major version.** Jar-in-jar loads the highest
  nested version, so an older Pillar runs against the newest Runtime in the pack.
- **The Runtime and the Pillars build with the libworks plugin**, but they do not nest
  `libworks-runtime`, which is FactoryWorks-shaped (the Works tab, machine screens, Jade). The
  plugin's plays-alone scan holds a Module list per suite and checks each Pillar against the other
  Pillars (libworks ADR 0005, ADR 0013), allowing the optional tag entries above.

**Considered.** A published API jar that every Pillar soft-depends on, for each interaction: it
is a contract to version for interactions vanilla state already carries. A required base mod
(the Groundworks pattern): that is the base that FactoryWorks ADR-0127 dissolved. Hosting the HUD in
`libworks-runtime`: every FactoryWorks Module would nest survival code it never uses, and the two
suites would share a release line.

**Amended** (2026-10-27). Freezing is driven by powder snow and Tough As Nails, not by a Cold
Pillar, and Tough As Nails' API is the one named exception to vanilla state (#5). A Pillar may add
optional entries to another's tag, and nests the Runtime only once it uses it (#6). The Runtime owns
a meter's display data, not the stat (#4).
