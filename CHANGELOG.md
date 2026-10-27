# Changelog

Written for the Pillars: what a Pillar nesting the Runtime can use, or will see change. A published version never changes: a fix is the next patch (`docs/agents/releases.md`).

## Unreleased

- The Runtime's signals, starting with the Strike: this player was struck by the Dark, and nothing about what it costs. A Pillar raises it with `Strike.raise(player)` for a `ServerPlayer` on the server thread, and it is posted on NeoForge's game bus as the event `io.github._5thlayer.atouchof.signal.Strike`, whose `player()` is the player struck; a Pillar listens the usual way. Each listener hears it once per raise, on every difficulty, and with no listener it does nothing (#10).

## 0.1.0

- The Runtime, mod `atouchof`, which a Pillar nests jar-in-jar, and its meters. A Pillar registers a meter from its mod constructor with `Meter.register(id, max, states...)`, each `MeterState` naming the Pillar's own full, half and empty GUI sprites, and sets it each tick per player with `meter.set(player, value, state, trend)`, the trend band from −3 to +3. The server tells only that player's client, in the `atouchof:meter` payload, which carries the half steps the row fills rather than the value, and only when the row, the state or the band changes. The client draws each meter as a row of ten icons in half steps, like vanilla's food bar: on the right, above the food bar and below the air bubbles, stacked in the order of the meters' ids, with one to three chevrons right of the row, up or down, for the trend. A meter shows only when vanilla's survival bars do (#9).
