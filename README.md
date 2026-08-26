# Chaos World Core（乱界核心）

**ChaosWorld 整合包的核心模组，为整合包提供大量注册内容。**

Chaos World Core 是 ChaosWorld 整合包的核心模组，注册了大量物品、方块、金属系列、科技组件等，为整合包提供丰富的合成路线与游戏内容。所有物品均通过延迟注册（DeferredRegister）统一管理，支持动态配置。

---

## 主要内容

- **9 种金属系列**：冰雪、暮色合金、巫神、混沌、神龙、飞龙、全能合金、量子、不锈钢 — 每种包含锭、粒、板、粉、齿轮、棒
- **9 阶科技系列**：从基础到终极的科技组件（锭、粒、板、粉、齿轮、棒）
- **80+ 独立物品**：水晶核心、处理器、集成电路、宝石、催化剂等
- **自定义配置**：通过 `config/chaosworld_core.toml` 可配置物品列表
- **退出确认界面**：防止误触关闭游戏
- **无线 Flux 能量仓**（Wireless Flux Energy Hatch）：Modern Industrialization 多方块结构与 Flux Networks 的无线能量桥接

### 无线 Flux 能量仓

提供**输入仓**（`wireless_flux_energy_input_hatch`）和**输出仓**（`wireless_flux_energy_output_hatch`），可作为 MI 多方块的能量仓口使用。

- 输入仓：直接从 Flux 网络抽取能量供给多方块
- 输出仓：将多方块产生的能量发送到 Flux 网络
- 右键打开 Flux GUI 配置网络
- 机壳颜色跟随多方块控制器
- 能量显示无上限，直接读取网络可用电量

**依赖：** Modern Industrialization、Flux Networks、More Flux Storage、Grand Power

## 许可

本项目采用与 AE2 及其附属模组一致的双许可模式（详见 [LICENSE.md](LICENSE.md)）：

- **源代码**：GNU LGPL v3.0 或更高版本（LGPLv3+）
- **美术、纹理、模型、音效等素材**：Creative Commons Attribution-NonCommercial-ShareAlike 3.0（CC BY-NC-SA 3.0）

---

---

# Chaos World Core

**The core mod of the ChaosWorld modpack, registering a vast amount of content for the pack.**

Chaos World Core is the central mod of the ChaosWorld modpack, providing a large registry of items, blocks, metal series, tech components, and more. It serves as the content foundation for the pack's crafting progression and gameplay. All registrations use DeferredRegister for consistency and support dynamic configuration.

## Features

- **9 Metal Series**: Ice, Twilight Alloy, Lich, Chaotic Metal, Draconic Metal, Wyvern Metal, Atmium, Quantum, Stainless Steel — each with ingot, nugget, plate, dust, gear, and rod
- **9 Tech Tiers**: Tech components from basic to ultimate (ingot, nugget, plate, dust, gear, rod)
- **80+ Unique Items**: Crystal Cores, processors, integrated circuits, gemstones, catalysts, and more
- **Custom Config**: Item list configurable via `config/chaosworld_core.toml`
- **Quit Confirmation Screen**: Prevents accidental game closure
- **Wireless Flux Energy Hatch**: Bridges Modern Industrialization multiblocks with Flux Networks for wireless energy transfer

### Wireless Flux Energy Hatch

Provides **input hatch** (`wireless_flux_energy_input_hatch`) and **output hatch** (`wireless_flux_energy_output_hatch`), usable as MI multiblock energy hatches.

- Input hatch: directly pulls energy from the Flux network to power multiblocks
- Output hatch: sends multiblock-generated energy to the Flux network
- Right-click to open Flux GUI for network configuration
- Casing color follows the multiblock controller
- No energy capacity limit — reads network available energy directly

**Dependencies:** Modern Industrialization, Flux Networks, More Flux Storage, Grand Power

## License

This project follows the same dual-license model as AE2 and its addons (see [LICENSE.md](LICENSE.md)):

- **Source code**: GNU LGPL v3.0 or later (LGPLv3+)
- **Art, textures, models, sounds and other assets**: Creative Commons Attribution-NonCommercial-ShareAlike 3.0 (CC BY-NC-SA 3.0)
