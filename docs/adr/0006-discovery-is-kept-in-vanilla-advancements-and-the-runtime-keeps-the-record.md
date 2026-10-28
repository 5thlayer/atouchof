---
status: provisional
---

# Discovery is kept in vanilla advancements, and the Runtime keeps the record

A Touch of Madness shows nothing of what a thing does to Sanity until the player has met it, and A
Touch of Darkness wants the same for its own things (atouchofdarkness#33). Something has to
remember, per player, what each one has discovered, and each Pillar must still play alone
(ADR-0001). This takes over A Touch of Madness's ADR 0006, which decided it for Madness alone.

**Decision.**

- **Each discoverable thing is one vanilla advancement in its Pillar's own tab, and that
  advancement being done *is* the Discovery**: there is no second record. Vanilla already saves it
  per player, keeps it through death, shows it on the advancement screen with no other mod, and
  announces it with a toast. Every check of "has this player discovered X", a Pillar's recipe-viewer
  pages and tooltips among them, reads that advancement.
- **Each lives at a fixed id, `<pillar>:discovery/<id>`, under the root `<pillar>:discovery/root`**,
  `<pillar>` being the Pillar's mod id and `<id>` the name the Pillar gives the thing. Packs rely on
  these ids.
- **A removed advancement counts as known**, so a pack that removes one never locks a page for
  good. A pack marks a thing known from the start by overriding its advancement to fire on
  `minecraft:tick`.
- **The server sends each player's client its own known list**, so the client never reads vanilla's
  advancement progress, and never learns another player's.
- **A Pillar may switch its Discovery off**, from its own config. Then every one of its things
  counts as known, and its client is told so.
- **The Runtime keeps the record, and each Pillar keeps the rest.** A Pillar registers its namespace,
  its things' ids and whether its Discovery is on; the Runtime reads those advancements as the
  record, tells each player's client its known list, and answers the client's "is this known". What
  discovers a thing, being its criteria, triggers and advancements, and the tab and the pages it
  gates stay in the Pillar. The record decides nothing about play, so it is plumbing, like the
  meters' sync, and ADR-0002 still holds: the Runtime owns no mechanic.

**Considered.** A player attachment per Pillar, with a journal item to show it: a second store and a
screen to build, for what vanilla already keeps and draws. A guide-book mod, Patchouli or
Modonomicon: either could only be an optional dependency, so it can't be the record of a Pillar
playing alone, though it can later lock its entries on these same advancements. Each Pillar keeping
its own copy of the record, as Madness did: the known-list sync and the client's check would be
written twice, and drift.
