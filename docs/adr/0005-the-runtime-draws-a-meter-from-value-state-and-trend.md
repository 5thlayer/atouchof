---
status: provisional
---

# The Runtime draws a meter from a value, a state and a trend

A Touch of Madness is the first Pillar with a meter, and it can't ship without one. ADR-0002 gives
the Runtime the meters' layout and drawing, and no mechanic, so the Runtime needs a contract a
Pillar can build against.

**Decision.**

- **A Pillar registers a meter** with an id, its own icon sprites for each state, a maximum, and
  its states. Madness has three: Sane, Uneasy and Insane.
- **Each tick, for each player, the Pillar sets** the current value, the state index, and a trend
  band from −3 to +3, which it works out from its own net rate.
- **The stat stays the Pillar's.** Sanity is Madness's own player attachment; the Runtime receives
  only what the meter shows, so a Pillar's mechanics can be built and tested before the Runtime
  exists (#4).
- **The Runtime syncs these to the client and draws them**: a row of ten icons in half steps, like
  vanilla's hunger, with chevrons for the trend. It arranges several Pillars' meters, and it
  computes nothing.
- **A lone Pillar's meter looks as if the Runtime weren't there.**
- **The Runtime's meter part is built first, as its own ticket,** since Madness nests the Runtime
  from its first release.

**Considered.** The Runtime holding the stat and its sync, as ADR-0002 first said: the Runtime would
then own state a mechanic changes, and Madness could not be built before it. A Pillar drawing its
own meter: several Pillars' meters would overlap, and each would repeat the layout.
