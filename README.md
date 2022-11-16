[![][1]][3] [![][2]][3] [![][4]][5]

[中文](README_CN.md)

# Just Enough Calculation Mod

Hey, this is a small add-on to **NEI** (Not Enough Items), in
order to help you calculate the amount of resources to craft a specific amount of item. .

This might be a little confusing, here's an example:

- you want to know how many wood logs do you need to craft 40 stairs, simply load the recipe you want to use, and it will
  tell you 13 logs should be enough, that's all.

This mod is focused on calculation, so you might find it powerful and high performance in some complicated cases. Have fun!

For all the release files, description or videos, refer to
the [release page](https://minecraft.curseforge.com/projects/just-enough-calculation).

## Features

- **Client only, no default keybinding. Set it up according to yourself.**
- Support get catalyst in [NEI-GTNH-2.1](https://github.com/GTNewHorizons/NotEnoughItems)
- Infinite inputs, outputs and catalysts of recipe
- Math calculation
- Recipe can be transferred from NEI recipe overlay gui (`shift` + click `?` button)
  - [x] Any 3x3 or 2x2 recipe in crafting table
  - [x] Gregtech
  - [x] Gregtech6
  - [x] Forestry (`?` button may overlap with the fluid tank. Click carefully)
  - [x] Avaritia (`?` button may be covered by the NEI search bar)
  - [x] GT++ (support GT++ 1.7.05.68 (GTNH 2.1.0.0), have not tested old versions)
  - [x] Thaumcraft (no aspect)
  - [ ] AE2 (recipe in `Quartz Grindstone` or others)
- [x] Item from NEI item panel
- [ ] Fluid from NEI item panel (Only support GT & GT6, others need to get from labels picker)
- [x] OreDictionary recognize


## Usage

Tutorial links:
- [mcmod.cn](https://www.mcmod.cn/post/1650.html) [Chinese] by @全麦
- general usage: #85

### **Setup keybinding**
![setup keybinding](docs/setup_keybinding.png)

### Main page
![main page](docs/main_page.png)


## TODO

- [ ] More friendly fluid amount tooltip (144x??+?? and 1000x??+??)
- ~~add new button to transfer the recipe.~~

## Known Issues

- [x] <del>**May crash** if you open the gui immediately after entering the world. Please wait for the NEI to load completely.</del>
- [ ] Fluid from NEI ItemPanel will be recognized as item
- [ ] Some OreDict items renders wrong with overlay (Now removed the overlay)
- [ ] GUI will cover the NEI's GUI like tooltip on item panel
- [ ] Fluid from recipe needs lots of adapters, see [Adapter](./src/main/java/me/towdium/jecalculation/nei/Adapter.java).

## Development

~~For run `gradle runClient`, you need copy the mods from `dev-mods/` to `run/mods`.~~

[1]: http://cf.way2muchnoise.eu/full_just-enough-calculation_downloads.svg

[2]: http://cf.way2muchnoise.eu/versions/just-enough-calculation.svg

[3]: https://minecraft.curseforge.com/projects/just-enough-calculation

[4]: https://img.shields.io/discord/517485644163973120.svg?logo=discord

[5]: https://discord.gg/M3fNfTW

