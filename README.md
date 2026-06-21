# ZyData

Server-side Fabric mod for Minecraft 26.1.1.

## Commands

* `/zydata open`
* `/zydata open <page>`

The command requires permission level 2. It reads the Vanilla+ item loot tables
under `moldomre:items`, generates one preview stack from each, and opens a
six-row chest catalog. Taking an item copies it into the player's inventory.

## Build

Requires JDK 25.

```bash
./gradlew build
```

The remapped mod JAR is created in `build/libs`.

## Install

Place the built JAR in the server `mods` directory. Players do not need the
mod, but they still need the matching resource pack to see custom models.
