[![][1]][3] [![][2]][3] [![][4]][5]

# Just Enough Calculation Mod

Hey, this is a small add-on to [NEI-GTNH-2.1](https://github.com/GTNewHorizons/NotEnoughItems) (Not Enough Items), in
order to help you calculate the amount of resources to craft a specific amount of item. .

This might be a little confusing, here's an example:

- you want to know how many wood logs do you need to craft 40 stairs, simply load the recipe you want to use, and it will
tell you 13 logs should be enough, that's all. 

This mod is focused on calculation, so you might find it powerful and high performance in some complicated cases. Have fun!

For all the release files, description or videos, refer to
the [release page](https://minecraft.curseforge.com/projects/just-enough-calculation).

## Features

- Infinite inputs, outputs and catalysts of recipe
- Recipe from NEI recipe handler (recipe gui)
  - [x] vanilla
  - [x] gregtech
  - [x] Forestry
  - [ ] AE2
  - [ ] Others
- [x] Item from NEI item panel
- [ ] Fluid from NEI item panel (Need to get fluid from labels)
- [x] OreDictionary recognize

## TODO

- Handle nei key binding (`R` to open recipe and `U` to open usage)

## Known Issues

- [ ] **May crash** if you open the gui immediately after entering the world
- [ ] **Client only**
- [ ] Fluid from NEI ItemPanel will be recognized as item
- [ ] Some OreDict items renders wrong with overlay (Now removed the overlay)
- [ ] GUI will cover the NEI's GUI like tooltip on item panel
- [ ] Check the nei version and adapter the old version.
- [ ] Fluid from recipe needs lots of adapters, see [Adapter](./src/main/java/me/towdium/jecalculation/nei/Adapter.java).


[1]: http://cf.way2muchnoise.eu/full_just-enough-calculation_downloads.svg

[2]: http://cf.way2muchnoise.eu/versions/just-enough-calculation.svg

[3]: https://minecraft.curseforge.com/projects/just-enough-calculation

[4]: https://img.shields.io/discord/517485644163973120.svg?logo=discord

[5]: https://discord.gg/M3fNfTW

