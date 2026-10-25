# Out-of-scope knowledge base

There is one file per **rejected concept**. That way a `wontfix` keeps its reasoning after the issue scrolls
away, and a request that has already been settled is recognised instead of argued again. `/triage` reads
every file here and matches by concept rather than by keyword.

## What belongs here

**A rejected enhancement, and nothing else.** Not bugs, not things already built, and not
deferrals: "not now" and "after Darkness ships" are scheduling, not scope. A decision with a considered
alternative belongs in `docs/adr/`.

## File format

Name each file `kebab-case-concept.md`. It is a short design document: what was asked, why it is out of
scope, and a `## Prior requests` list of every issue that asked. When a second issue asks the same
thing, add it to that list.

## Changing your mind

Delete the file. Old issues stay closed, and the new request goes through normal triage.
