---
status: accepted
---

# The Runtime carries neutral signals that one Pillar raises and another prices

Every Strike should cost Sanity where A Touch of Madness is loaded, on every difficulty. On
Peaceful a Strike deals nothing, and vanilla's `Player.hurtServer` returns before any damage event,
so no vanilla state records that the player was struck. ADR-0002 leaves such an interaction to an
ADR of its own.

**Decision.**

- **The Runtime carries neutral signals.** A signal says what happened to a player, such as "this
  player was struck by the Dark", and nothing about what it costs. One Pillar raises it; any Pillar
  may listen. Neither knows the other is there, so each still plays alone, and the Runtime holds no
  mechanic (ADR-0002).
- **The first signal is the Strike.** A Touch of Darkness raises it on every Strike, Peaceful
  included, whether or not the Strike dealt damage.
- **What a signal costs belongs to the listener.** A Touch of Madness takes 10 Sanity for each
  Strike, on every difficulty, and the amount is in Madness's config (#4).
- **A new signal is added to the Runtime, never to a Pillar,** and stays backward compatible within
  the Runtime's major version, as its meters do (ADR-0002).

**Considered.** Madness reading the Strike's damage event: there is none on Peaceful. Darkness
lowering Sanity itself: it would have to know Madness, and the cost would live in the wrong
Pillar. A published Darkness API that Madness soft-depends on: a contract per pair of Pillars,
where one neutral signal serves any listener.
