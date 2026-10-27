# A Touch Of

A suite of Minecraft 26.1.2 / NeoForge survival mods that brings Don't Starve's pressure (darkness,
madness and wickedness) into an ordinary world, with an occult touch: what threatens the player and what
helps them against it is bound, summoned or kindled more than machined. Don't Starve is the
reference for feel, never for numbers, names or art.

## The suite

**Runtime**:
The small shared mod every Pillar nests, which arranges and draws the Pillars' meters, carries
their Signals, and holds no mechanic of its own (ADR-0002).
_Avoid_: Core, base mod

**Meter**:
What the Runtime draws for a Pillar's stat: a row of ten icons in half steps, in the sprites of the
state the Pillar sets, with chevrons for its Trend Band. The stat stays the Pillar's (ADR-0005).
_Avoid_: bar, gauge

**Trend Band**:
How fast a Meter is moving, as its Pillar works it out from its own rate: −3 to +3, drawn as that
many chevrons, up or down, and none at 0.

**Signal**:
What the Runtime carries from one Pillar to any other: that something happened to a player, such
as a Strike, and nothing about what it costs, which each listener decides (ADR-0004).
_Avoid_: hook, callback

**Pillar**:
One of the suite's mods, each owning one survival danger and playable alone in vanilla: A Touch of
Darkness, A Touch of Madness and A Touch of Wickedness. A Touch of Cold is shelved, and cold and
heat are left to Tough As Nails (ADR-0001).
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

**Unsettling**:
What drains Sanity by being near: `#atouchofmadness:unsettling`, which another Pillar may add its
own creatures to, optionally (ADR-0002).

**Sanity Modifier**:
The amount a food adds to or takes from Sanity when eaten; every food has one, zero by default.
_Avoid_: Sanity value

## Darkness

**Dark**:
The state of a player whose position is not lit by enough vanilla light, below 5 (ADR-0003). Past
a grace period the Babau strikes for heavy damage, again and again until the player reaches light,
in caves as on the surface; a full moon counts as lit.
_Avoid_: Darkness (vanilla's Warden status effect)

**Babau**:
The one being of Italian folklore that lives in the Dark and strikes whoever stays there too long;
never seen, only felt.
_Avoid_: Bogeyman, Charlie (Klei's name), grue, the Dark (the state it lives in), monster

**Strike**:
One blow from the Babau, repeated while the player stays in the Dark. It raises the Runtime's
Strike signal on every difficulty, Peaceful included, so another Pillar can price it (ADR-0004).
_Avoid_: attack, hit

**Light Source**:
A block or carried item that lights its surroundings for the Dark. Every one runs on fuel or
energy and goes out when it runs dry (ADR-0003).
_Avoid_: Lamp (one kind of Light Source)

**Spent Torch**:
A torch that has burnt out, or a world-generated torch once broken; it can be relit.

## Wickedness

**Wickedness**:
A per-player amount of harm done to creatures that posed no threat, never shown as a meter.
_Avoid_: Naughtiness (Klei's name), karma

**Bailiff**:
The creature that comes for a player whose Wickedness has crossed their Threshold, to seize their
goods rather than to kill them.
_Avoid_: Krampus (Klei's name), punisher

**Seal**:
What a killed Bailiff leaves, and the material A Touch of Wickedness crafts from, so wickedness is
sometimes chosen.

## Cold and heat

Left to Tough As Nails (ADR-0001). The one term the suite keeps:

**Freezing**:
Vanilla's own cold state, the one powder snow causes, Tough As Nails drives at its coldest, and
leather armour wards off; A Touch of Madness reads it (ADR-0002).
