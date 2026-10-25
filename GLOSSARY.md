# A Touch Of

A suite of Minecraft 26.1.2 / NeoForge survival mods that brings Don't Starve's pressure (darkness,
madness and cold) into an ordinary world. Don't Starve is the reference for feel, never
for numbers, names or art.

## The suite

**Runtime**:
The small shared mod every Pillar nests, which arranges and draws the Pillars' meters and holds no
mechanic of its own (ADR-0002).
_Avoid_: Core, base mod

**Pillar**:
One of the suite's mods, each owning one survival danger and playable alone in vanilla: A Touch of
Darkness, A Touch of Madness and A Touch of Cold (ADR-0001).
_Avoid_: Module (a FactoryWorks mod), addon

## Madness

**Sanity**:
A per-player meter of how firmly the player holds on to the world; it falls under pressure and
recovers with comfort.
_Avoid_: Insanity (the low state, not the meter), mind

**Shadow Creature**:
A hostile that exists only for a player whose Sanity is low, and becomes real enough to attack
as it falls further.
_Avoid_: Hallucination (the harmless effects)

**Umbra**:
What a Shadow Creature leaves behind, and the material A Touch of Madness crafts from, so going
mad is sometimes chosen.
_Avoid_: Nightmare Fuel (Klei's name), shadow material

**Sanity Modifier**:
The amount a food adds to or takes from Sanity when eaten; every food has one, zero by default.
_Avoid_: Sanity value

## Darkness

**Dark**:
The state of a player whose position is not lit by enough vanilla light. Past a grace period
something unseen strikes for heavy damage, again and again until the player reaches light, in
caves as on the surface; a full moon counts as lit.
_Avoid_: Darkness (vanilla's Warden status effect)

**Light Source**:
A block or carried item that lights its surroundings for the Dark. Every one runs on fuel or
energy and goes out when it runs dry (ADR-0003).
_Avoid_: Lamp (one kind of Light Source)

**Spent Torch**:
A torch that has burnt out, or a world-generated torch once broken; it can be relit.

## Cold

**Body Temperature**:
A per-player measure running from freezing to overheating, driven by climate and gear; both ends
harm the player.

**Cold**:
The low end of Body Temperature, and the danger A Touch of Cold is named for, as the Dark is for A
Touch of Darkness.

**Freezing**:
Vanilla's own cold state, the one powder snow causes and leather armour wards off; Cold drives it,
so whatever reads Freezing reacts to Cold.

**Overheating**:
The high end of Body Temperature, harmful as Cold is.
