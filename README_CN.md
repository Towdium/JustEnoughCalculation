[![][1]][3] [![][2]][3] [![][4]][5]

# Just Enough Calculation Mod

Hey, this is a small add-on to **NEI** (Not Enough Items), in
order to help you calculate the amount of resources to craft a specific amount of item. .

This might be a little confusing, here's an example:

- you want to know how many wood logs do you need to craft 40 stairs, simply load the recipe you want to use, and it will
  tell you 13 logs should be enough, that's all.

This mod is focused on calculation, so you might find it powerful and high performance in some complicated cases. Have fun!

---

查看所有的 Release 文件, 描述或者视频, 请查看 [CurseForge release 页面](https://minecraft.curseforge.com/projects/just-enough-calculation) 或 [GitHub release](https://github.com/Towdium/JustEnoughCalculation/releases).

## 功能

- **仅客户端，无默认按键绑定，请根据个人喜好[设置](#设置按键绑定)**
- 支持 [NEI-GTNH](https://github.com/GTNewHorizons/NotEnoughItems) 的催化剂（不消耗的合成参与者，如工作台，各种机器之类的）
- 合成表的输入、输出和催化剂 无数量限制
- 数学计算器
- 可以从 NEI 合成表 gui 页面直接获取合成表 (`shift` + 单击 `?` 按钮)
  - [x] 原生MC
  - [x] gregtech
  - [x] gregtech6
  - [x] Forestry (`?` 按钮可能与流体重叠，不是很好点到)
  - [x] Avaritia (`?` 按钮可能会被NEI搜索栏遮盖)
  - [x] GT++ (支持 GT++ 1.7.05.68 (GTNH 2.1.0.0)，老版本没有测试过)
  - [x] Thaumcraft (不包括要素)
  - [ ] AE2（目前没办法，需要 AE2 作者支持
- [x] 从 NEI 物品列表抓取物品
- [ ] 从 NEI 物品列表抓取流体 (仅支持 GT & GT6, 其余流体请从 **计算器内部流体标签** 抓取)
- [x] 矿物词典支持

## 使用方法

教程链接:
- [mcmod.cn](https://www.mcmod.cn/post/1650.html) by @全麦
- [一般用法](https://github.com/Towdium/JustEnoughCalculation/issues/85)


### **设置按键绑定**
![setup keybinding](docs/setup_keybinding.png)

### 主页面
![main page](docs/main_page.png)


## TODO

- [ ] 更友好的流体数量 tooltip 显示 (144x??+?? and 1000x??+??)

## 已知问题

- [ ] 从 NEI 物品列表抓取的 流体 会被识别为 物体
- [ ] 由于矿物词典标签的渲染存在问题，目前暂时不显示覆盖的标识
- [ ] 计算器 gui 会覆盖 NEI 的 tooltip
- [ ] 流体需要针对 mod 进行适配, 开发人员可以查看 [Adapter](./src/main/java/me/towdium/jecalculation/nei/Adapter.java)

[1]: http://cf.way2muchnoise.eu/full_242223_downloads.svg

[2]: http://cf.way2muchnoise.eu/versions/242223.svg

[3]: https://minecraft.curseforge.com/projects/just-enough-calculation

[4]: https://img.shields.io/discord/517485644163973120.svg?logo=discord

[5]: https://discord.gg/M3fNfTW

[6]: https://github.com/GTNewHorizons/NotEnoughItems