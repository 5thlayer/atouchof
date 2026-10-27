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
- **Every Light Source burns and goes out when it runs dry.** The Pillar provides its own Torch,
  Lamp and Campfire. There is no FE lamp for now: the Lamp is the Pillar's lantern, fuelled, carried
  or placed, and toggled by hand.
- **Fuel burns at ten times its vanilla burn time, up to a cap per Light Source,** and overflow is
  lost. The Lamp holds one in-game day and the Campfire half a day.
- **A Torch is crafted already burning, one per recipe, and is never placed.** It burns only while
  held, is paused anywhere else, and goes out underwater. It burns out into a Spent Torch, which
  cannot be refuelled, only relit; a relit Torch burns a quarter as long, then burns away to
  nothing.
- **A Light Source burns by the level's time of day, loaded or not.** It records when it was lit
  and, on load, works out whether it has burnt out, so sleeping and `/time set` burn fuel too. A
  base the player leaves goes dark while they are away.
- **A world-generated torch drops a Spent Torch**, so mineshafts and villages are not a stock of
  permanent light. A vanilla torch that arrives as loot or in a trade arrives as a burning Torch, and
  a vanilla lantern as a fully fuelled Lamp.
- **The Dark reads vanilla light level (ADR-0002), and a position is lit at 5 or above**
  (atouchofdarkness#12). Every open-sky night, which reads 4, is then Dark unless the moon is full,
  as A Touch of Madness's low light is (atouchofmadness ADR 0003). Natural light (lava, gathered
  glowstone, shroomlight, froglights) costs a trip, a farm or a risk, and stays as the earned
  exception.
- **A block tagged `atouchofdarkness:unlit` gives no light at all, for everyone**
  (atouchofdarkness ADR 0003). That closes the lights whose recipes can't go, such as redstone
  torches, enchanting tables and ender chests, and another mod's lamp once a pack tags it.
- **Carried light is real vanilla light, placed by an invisible block** (atouchofdarkness ADR 0002),
  so any Pillar that reads light level, Madness included, sees it.

**Considered.** Leaving vanilla torches craftable but not counting them as lit: the Dark would
then need its own light engine instead of vanilla's light level. Making only carried light burn:
the player would beat the Dark by placing torches everywhere.

**Amended** (2026-10-27), from the Darkness grilling (#3, atouchofdarkness#1): the burn clock is the
level's time of day, fuel is multiplied and capped, Torches are carried only, the FE lamp is
deferred, the `unlit` tag removes light outright, and carried light is real light. The lit
threshold is 5, not the 3 the grilling first chose (atouchofdarkness#12).
