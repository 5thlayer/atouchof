---
status: provisional
---

# Vanilla light loses its recipes, and every Light Source burns fuel or energy

The Dark is only a pressure if light runs out. Vanilla's light is permanent and cheap, so a
player who can craft vanilla torches never meets the Dark. Removing vanilla recipes breaks
ADR-0002's rule, deliberately, just as Craftworks removed vanilla's 2x2 crafting grid
(FactoryWorks ADR-0066, ADR-0089).

**Decision.**

- **A Touch of Darkness removes the recipe of every vanilla block that emits light** (torches,
  lanterns, jack o'lanterns, candles, end rods, sea lanterns, redstone lamps, crafted glowstone
  and the like). A config restores them for a pack that wants them back. The Darkness ticket
  holds the exact list.
- **Every Light Source runs on fuel or FE and goes out when it runs dry.** The Pillar provides its own
  torches, lanterns and lamps. A torch burns out into a Spent Torch and cannot be refuelled, only
  relit. Campfires and lanterns take any fuel at its vanilla burn time. An FE lamp also has a fuel
  variant, so a vanilla-only game is never left without a lamp.
- **A Light Source burns by game time, loaded or not.** It records when it was lit, and on load it
  works out whether it has burnt out. A base the player leaves goes dark while they are away.
- **A world-generated torch drops a Spent Torch**, so mineshafts and villages are not a stock of
  permanent light.
- **The Dark reads vanilla light level (ADR-0002).** Natural light (lava, gathered glowstone,
  shroomlight, froglights) costs a trip, a farm or a risk, and stays as the earned exception. The
  cheap leaks, such as glow lichen spread with bone meal, are closed by the lit threshold or a
  tag. Another mod's lamp counts as lit unless a pack tags it `atouchofdarkness:unlit`.

**Considered.** Leaving vanilla torches craftable but not counting them as lit: the Dark would
then need its own light engine instead of vanilla's light level. Making only carried light burn:
the player would beat the Dark by placing torches everywhere.
