# 乱界核心（Chaos World Core）

**Minecraft 1.21.1 · NeoForge · 以 AE2 为核心的终局科技扩展**

乱界核心（Chaos World Core）是 ChaosWorld 整合包的核心模组，也是 **UFO Future** 的扩展层：UFO 提供量子多方块、恒星物理、星体物质与巨型存储的底座，本模组在其上补出金属 / 科技材料线、三条 Mekanism 机器线（电路组装机、龙魂锻炉、虚脉钻探机）、自有存储矩阵，以及一批跨模组联动（AE2LT 闪电、Botania 魔力、FTB Quests、Jade、JEI、KubeJS、MMCR）。从第一台次元物质装配器，到最终极的恒星模拟平台，构成一条能一路走到「无限」的产线。

> 模组 ID `chaosworld_core`　·　版本 `1.0.1`　·　依赖见 [依赖](#依赖) 一节　·　更完整的盘点与上手路线见 [docs/模组介绍.md](docs/模组介绍.md)

---

## 主要内容

### 电路组装、龙魂锻造与虚脉钻探〔本模组〕

- **电路组装机（Mechanical Assembler）**：以能量与化学物把材料组装成电路，是整条科技线的起点；配套 **8 级工厂**——基础 / 高级 / 精英 / 终极 / 绝对 / 至尊 / 寰宇支配 / 悖论无限，一次并行处理 **3 / 5 / 7 / 9 / 11 / 13 / 15 / 17** 份配方
- **龙魂锻炉（Dragon Soul Forge）**：熔炼龙魂、锻造龙魂之物的冶金机器，同样带 8 级工厂
- **虚脉钻探机（Vein Drill）**：配合 **虚拟矿脉核心**，从「虚拟行星矿脉」中抽取矿物。矿脉分 **7 级**（陨石碎块 → 陨石 → 小行星 → 行星 → 恒星 → 星云 → 星系），等级越高，可抽到的矿物越好、质量越大
- 钻头进阶：**64 个 N 阶锭 → N+1 阶钻头**；九阶锭加 9 级钻头可做出 **无限钻头**
- 钻探机自动扫描新矿脉、自动修复、自动拉取 / 弹出与维护；JEI 中可查每级矿脉的矿物池、抽取概率与最低钻头等级

### 材料体系〔本模组〕

- **11 种金属**：每种都是锭 / 粒 / 板 / 粉 / 齿轮 / 棒 六形态 + 存储块

| 金属 | ID | 金属 | ID |
| --- | --- | --- | --- |
| 冰雪 | `ice` | 神龙金属 | `draconic_metal` |
| 暮色合金 | `twilight_alloy` | 飞龙金属 | `wyvern_metal` |
| 巫神 | `lich` | 全能合金 | `atmium` |
| 混沌金属 | `chaotic_metal` | 量子 | `quantum` |
| 不锈钢 | `stainless_steel` | 炫彩 | `xuancai` |
| 圣金源 | `holy_gold` | | |

- **9 阶科技材料**：一阶 ~ 九阶科技锭 / 粒 / 板 / 粉 / 齿轮 / 棒，各带块，是机器与电路体系的主要原料
- 所有材料自动带 `c:` 通用标签，便于与其它模组互通；科技材料的颜色可在配置项 `techColor_1` ~ `techColor_9` 中逐档调整

### 量子多方块机械〔UFO Future 提供，本模组增强〕

- **次元物质装配器（DMA）**：入门级先进机器，带热量系统与催化剂插槽，可制作处理器、催化剂、星体物质与多种流体
- **量子物质制造机（QMF）**：DMA 的多方块进化版，面向 AE2 自动化与大批量合成
- **量子切片机**：批量加工印刷处理器部件；**量子处理工厂**：批量精加工处理器；**量子低温锻造炉**：生产稳定冷却剂等高级流体
- **量子样板供应器**：支持 **72 组样板**的 AE2 样板仓，配合本系列多方块使用
- **MK1 / MK2 / MK3 分级机制**：更高等级解锁更高级配方，并自动加速旧配方
- 本模组在控制器侧补了闪电需求映射、样板缓存与 Jade / JEI 展示等细节

### Stellar Nexus 恒星模拟平台〔UFO Future 提供〕

终局级多方块，模拟真实恒星物理过程进行大规模生产：

- 模拟配方：红巨星坍缩、铁核聚变、钻石高压、中子轰击、超新星收割、恒星合成……
- **Stellar Field Generator Mk.I~III**：提供场强等级，越高越强
- 燃料、冷却剂、热量管理，支持**安全模式**（过热自动停机）与**超频模式**（10 倍能量、5 倍热量 / 速度）
- 热量失控会导致灾难性爆炸——请务必打开安全模式
- 直接接入 ME 网络供料与输出

### 巨型存储与量子全能矩阵〔本模组扩展〕

三级恒星存储梯子，每级五档：

| 系列 | 容量档位 |
| --- | --- |
| 白矮星 | 1G / 4G / 16G / 64G / 256G |
| 中子星 | 1T / 4T / 16T / 64T / 256T |
| 脉冲星 | 1P / 4P / 16P / 64P / 256P（256 PiB） |

- 元件分 **Echo / Beacon / Nexus / Core / Singularity** 五档，与三级系列组合成完整梯子
- 安装 AE2 OmniCells 后，它们以 **ECO - LE9 存储矩阵** 的形式出现，可插入 ME 驱动器与 NeoEcoAE 的 ECO 驱动器，并附能耗显示
- **量子全能矩阵**：在 OmniCells 全能后端上扩展 16G ~ 256P 共 13 档，支持物品 / 流体 / 能量等全部键类型、类型数无限
- 另有无限存储组件、40M ~ Infinity 存储组件与能量单元（Event Horizon / Quantum Energy Cell）

### 星体物质进阶线〔UFO Future 提供〕

Proto Matter → Corporeal Matter → 白矮星物质 → 中子星物质 → 脉冲星物质 → 暗物质 → 不稳定白洞物质

Nuclear Star → Neutronium Sphere → Enriched Neutronium Sphere → Charged Enriched Neutronium Sphere

- 星体碎片：白矮星 / 脉冲星 / 中子星（锭、粉、粒、棒、流体）
- 催化剂：Matterflow / Chrono / Overflux / Quantum（T1~T3）——改变合成速度、能耗、失败率与产物加成

### UFO 套装与装备〔UFO Future 提供〕

- **UFO 工具**：剑（灵魂收割，击杀成长）、镐（自动熔炼、进度时运）、锤、巨剑、法杖、弓、钓鱼竿等
- **UFO 盔甲**：头盔、胸甲、护腿、靴子
- **Astral Nexus 套装**：无限护甲、绝对免伤、100 万倍伤害反射、创造飞行、辐射免疫
- **Reality Ripper**：无限伤害，甚至能击杀创造模式玩家
- **Thermal Resistor 套装**：热阻护甲

### 流体与安全收容

- 流体：UU 物质、UU 增幅剂、寒冰之尘 / 凝胶寒冰、液体星光、原初物质、原始恒星等离子体、超越物质、时空流体、稳定冷却剂等
- **Aether Containment Capsule（ACC）**：安全储存危险流体
- **Safe Containment Matter（SCM）**：收容危险物品
- 凋灵召唤重构〔本模组〕：搭建结构并手持苦难核心（Cryptid Core）右键，可召唤 4000 血的强化凋灵

### 实用工具

- **Structure Terminal**〔UFO〕：扫描 / 自动建造 / 拆解多方块结构，支持 AE 材料取用、幻影全息预览
- **人造恒星**〔本模组 + KubeJS〕：由 KubeJS 脚本注册的 MMCR 多方块，本模组为其提供恒星球体渲染
- 内置 AE2 Guide 图文教程（游戏中按 Guide 键打开）

### 与其他模组的联动

**Applied Energistics 2（AE2）**〔必需〕
- 所有量子多方块直接接入 ME 网络：样板、自动供料、产物直接输出
- 量子样板供应器：72 组样板的 AE2 样板仓
- 恒星存储单元为 AE2 存储单元格式，可插入 ME 驱动器，配套 40M~Infinity 存储组件
- Structure Terminal：从 ME 网络取材料自动建造，绑定无线接入点（WAP）远距离取料
- Stellar Nexus 从 ME 网络抽取能量与燃料；恒星模拟可批量合成 AE2 奇点

**Mekanism**〔必需〕
- 电路组装机 / 龙魂锻炉 / 虚脉钻探机三条线本身就是 Mekanism 机器，兼容其能量、化学品与工厂并行
- 量子机械仓口可直接存取化学物质，化学物质也可进入 ME 网络
- 恒星模拟配方：冶金激增、乙烯富集、裂变堆工艺（以乙烯为燃料）
- Astral Nexus 套装免疫 Mekanism 辐射

**ExtendedAE+（扩展AE）**〔必需〕
- 优化其 IO 端口（ExIOPort）批量处理与大容量存取
- 恒星模拟可批量合成 Entro 水晶

**AE2 Lightning Tech（AE2LT 闪电科技）**〔必需〕
- 深度联动其 IO 端口、存储与 Tick 速率系统，支持大容量高速流转
- 把 AE2LT 过载配方的闪电消耗映射进量子处理工厂（网络供电，取消时归还）

**Thunderbolt Core（AE2LT 底层库）**〔必需〕
- 使用其快速自动合成规划器，优化大规模合成任务

**NeoEcoAE**〔必需〕
- ECO 驱动器存储子系统联动：量子全能矩阵可装入 ECO 驱动器，附能耗显示
- 与 AE2 OmniCells 同时安装后，解锁完整 **ECO - LE9 存储矩阵**等级体系：NeoEcoAE 提供基础等级（如 256m = 4 GiB），本模组扩展出 16G ~ 256T ~ 256P 的更高系列

**AE2 OmniCells**〔必需〕
- 量子全能矩阵基于其**全能存储后端**：支持全部键类型（物品 / 流体 / 能量等）、类型数无限，容量 16G ~ 256P（16,384 MiB ~ 256 PiB，共 13 档）

**Botania**〔可选〕
- 新增方块 **福鲁伊克斯魔力池（Fluix Mana Pool）**
- 注册 AE2 键类型 **Mana**：魔力可以像物品、流体一样进出 ME 网络
- 提供魔力的容器物品 / 导入 / 导出 / 外部存储策略，并给带通用内部存储的 AE2 方块挂 `ManaReceiver`

**FTB Quests**〔可选〕
- 自定义任务类型：**AE 资源任务**（按 ME 网络中的键与数量判定）、**化学任务**（Mekanism 化学品）
- **任务检测卡**：在 AE 网络中扫描 / 记录键，客户端提供 AE 键选择界面

**Jade（WAILA）**〔必需〕
- 多方块结构状态、量子样板供应器缓存、矿脉等级与钻头耐久显示

**JEI**〔可选〕
- 配方分类：电路组装、龙魂锻造、虚脉钻探矿物池、钻头修复、凋灵召唤、聚变转换
- 点击控制器可查看 3D 多方块结构预览

**KubeJS**〔可选〕
- 注册与编辑恒星模拟等自定义配方；人造恒星多方块也由 KubeJS 脚本定义

**MMCR**〔可选〕
- 为人造恒星多方块提供控制器渲染

**AppFlux**〔可选〕
- 从 AppFlux 无线能量网络提取 FE，为 AE 储能物品与能量仓口充电

**Advanced AE**〔可选〕
- 恒星模拟可合成高级量子产物（advancedae_quantum）

**ProjectE / Project Expansion**〔可选〕
- 炼金术转换接口与 ME 存储互通

**GuideME**〔可选〕
- 游戏内图文教程

**GeckoLib**〔可选〕
- UFO 套装与实体动画渲染

---

## 依赖

必需：

| 模组 | 版本 |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.264+ |
| UFO Future（`ufo`） | 3.0.0-beta.6+ |
| RaishX Core（`raishxcore`） | 0.2+ |
| Applied Energistics 2（`ae2`） | 19.2.17 ~ 19.2.x |
| AE2 Lightning Tech（`ae2lt`） | 2.1.0-beta.5+ |
| ExtendedAE+（`extendedae_plus`） | 1.5.5+ |
| NeoEcoAE（`neoecoae`） | 21.2.10+ |
| AE2 OmniCells（`ae2omnicells`） | 1.1.6+ |
| Mekanism（`mekanism`） | 10.7.19+ |
| Jade（`jade`） | 15.10.6+ |

可选：Botania（`botania` 454+）、FTB Quests（`ftbquests` 2101.1.0+）、KubeJS、MMCR、JEI、AppFlux、Advanced AE、ProjectE / Project Expansion、GuideME、GeckoLib、Twilight Forest、Mekanism Extras、MekMM

---

## 从源码构建

需要 **JDK 21**（Windows 下命令换成 `gradlew.bat`）：

```bash
./gradlew build          # 产物在 build/libs/
./gradlew runClient      # 启动开发客户端
./gradlew runServer      # 启动开发服务端
```

运行期依赖（UFO Future、AE2 等）放在 `run/mods/`；编译期用的 UFO jar 放在 `libs/`。

---

## 许可

本项目采用与 AE2 及其附属模组一致的双许可模式（详见 [LICENSE.md](LICENSE.md)）：

- **源代码**：GNU LGPL v3.0 或更高版本（LGPLv3+）
- **美术、纹理、模型、音效等素材**：Creative Commons Attribution-NonCommercial-ShareAlike 3.0（CC BY-NC-SA 3.0）

源码仓库 / Source repository：https://github.com/yongaishide/chaosworld-core （LGPLv3 要求的对应源码即此仓库）

### 致谢

`com.yongaishide.chaosworld.botania` 包内的魔力集成代码移植自 [Applied Botanics](https://github.com/ramidzkh/Applied-Botanics)（ramidzkh 及贡献者，LGPL-3.0-or-later），`fluix_mana_pool` 系列贴图与方块模型沿用自该模组（CC BY-NC-SA 3.0）。完整的出处、修改日期与文件清单见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)。

---

---

# Chaos World Core

**Minecraft 1.21.1 · NeoForge · An AE2 endgame tech expansion**

Chaos World Core (乱界核心) is the core mod of the ChaosWorld modpack and an addon for **UFO Future**: UFO provides the quantum multiblocks, stellar physics, stellar matter and mega storage, while this mod adds the metal / tech material line, three Mekanism machine lines (Mechanical Assembler, Dragon Soul Forge, Vein Drill), its own storage matrices and a set of cross-mod integrations (AE2LT lightning, Botania mana, FTB Quests, Jade, JEI, KubeJS, MMCR). From your first Dimensional Matter Assembler all the way to the ultimate stellar simulation platform.

> Mod ID `chaosworld_core` · Version `1.0.1` · Dependencies: see [Dependencies](#dependencies) · Fuller feature list: [docs/模组介绍.md](docs/模组介绍.md)

## Features

### Assembling, Forging & Vein Drilling 〔this mod〕

- **Mechanical Assembler**: turns energy and chemicals into circuits — the entry point of the tech line; comes with **8 factory tiers** (Basic / Advanced / Elite / Ultimate / Absolute / Supreme / Cosmic / Infinite) processing **3 / 5 / 7 / 9 / 11 / 13 / 15 / 17** recipes in parallel
- **Dragon Soul Forge**: metallurgic machine for smelting dragon souls and forging dragon-tier gear, with the same 8 factory tiers
- **Vein Drill**: extracts ores from "virtual planetary veins" through a **Vein Core**. Veins come in **7 tiers** (Meteorite Fragment → Meteorite → Asteroid → Planet → Star → Nebula → Galaxy); higher tiers yield better ores and more mass
- Drill progression: **64 ingots of tier N → tier N+1 drill**; nine-tier ingots plus a 9-tier drill make the **Infinity Drill**
- The drill auto-scans new veins, auto-repairs, auto-pulls / ejects and maintains itself; JEI lists each vein tier's ore pool, weights and minimum drill level

### Materials 〔this mod〕

- **11 metals**, each in six forms (ingot / nugget / plate / dust / gear / rod) plus a storage block: `ice`, `twilight_alloy`, `lich`, `chaotic_metal`, `draconic_metal`, `wyvern_metal`, `atmium`, `quantum`, `stainless_steel`, `xuancai`, `holy_gold`
- **9 tech materials** (tech 1–9), each with ingot / nugget / plate / dust / gear / rod and a block — the main feedstock of the machine and circuit lines
- All materials carry the common `c:` tags for interoperability; tech material colors are configurable per tier through `techColor_1`–`techColor_9`

### Quantum Multiblocks 〔UFO Future, enhanced here〕

- **Dimensional Matter Assembler (DMA)**: entry-level advanced machine with a heat system and catalyst slots — processors, catalysts, stellar matter and fluids
- **Quantum Matter Fabricator (QMF)**: the multiblock evolution of the DMA for AE2 automation and heavy bulk jobs
- **Quantum Slicer** (bulk printed parts), **Quantum Processing Factory** (bulk processor finishing), **Quantum Cryoforge** (Stable Coolant and advanced fluids)
- **Quantum Pattern Hatch**: a **72-pattern** AE2 pattern provider for the multiblock line
- **MK1 / MK2 / MK3 tiering**: higher tiers unlock better recipes and automatically accelerate older ones
- This mod adds lightning requirements, pattern caching and Jade / JEI readouts on the controllers

### Stellar Nexus 〔UFO Future〕

The endgame multiblock that simulates real stellar physics for extreme-scale production:

- Simulations: Red Giant Collapse, Iron Core Fusion, Diamond Pressure, Neutron Bombardment, Supernova Harvest, Stellar Synthesis and more
- **Stellar Field Generators Mk.I–III** provide the field strength
- Fuel, coolant and heat management with **Safe Mode** (auto-shutdown on overheat) and **Overclocking** (10x energy, 5x heat/speed)
- Thermal failure causes a catastrophic explosion — keep Safe Mode on!
- Feeds from and outputs directly into your ME network

### Mega Storage & Quantum Omni Matrices 〔this mod〕

Three stellar ladders, five tiers each:

| Series | Capacities |
| --- | --- |
| White Dwarf | 1G / 4G / 16G / 64G / 256G |
| Neutron Star | 1T / 4T / 16T / 64T / 256T |
| Pulsar | 1P / 4P / 16P / 64P / 256P (256 PiB) |

- Components come in **Echo / Beacon / Nexus / Core / Singularity** tiers, combined with the three series into a full ladder
- With AE2 OmniCells installed they appear as **ECO - LE9 storage matrices**, fitting both ME drives and NeoEcoAE ECO drives, with power consumption display
- **Quantum Omni matrices**: 13 tiers from 16G to 256P on the OmniCells universal backend — every key type (items / fluids / energy…), unlimited types
- Plus the Infinity Storage Component, 40M–Infinity components and energy cells (Event Horizon / Quantum Energy Cell)

### Stellar Matter Progression 〔UFO Future〕

Proto Matter → Corporeal Matter → White Dwarf Matter → Neutron Star Matter → Pulsar Matter → Dark Matter → Unstable White Hole Matter

Nuclear Star → Neutronium Sphere → Enriched Neutronium Sphere → Charged Enriched Neutronium Sphere

- Star fragments: white dwarf / pulsar / neutron star (ingots, dust, nuggets, rods, fluids)
- Catalysts: Matterflow / Chrono / Overflux / Quantum (T1–T3) — alter speed, energy cost, failure chance and bonus output

### UFO Gear 〔UFO Future〕

- **UFO tools**: sword (Soul Harvest that grows with kills), pickaxe (Auto-Smelt, progressive Fortune), hammer, greatsword, staff, bow, fishing rod and more
- **UFO armor**: helmet, chestplate, leggings, boots
- **Astral Nexus set**: infinite armor, absolute immunity, 1,000,000x damage reflect, creative flight, radiation immunity
- **Reality Ripper**: infinite damage — can even kill creative players
- **Thermal Resistor suit**: heat-resistant armor

### Fluids & Containment

- Fluids: UU Matter, UU Amplifier, Cryotheum Dust / Gelid Cryotheum, Liquid Starlight, Primordial Matter, Raw Star Matter Plasma, Transcending Matter, Spatial/Temporal Fluid, Stable Coolant and more
- **Aether Containment Capsule (ACC)**: safely store hazardous fluids
- **Safe Containment Matter (SCM)**: contain hazardous items
- Wither summoning rework 〔this mod〕: build the structure and right-click with a Cryptid Core (苦难核心) to summon a 4000 HP empowered Wither

### Utilities

- **Structure Terminal** 〔UFO〕: scan, auto-build and dismantle multiblocks, pull materials from AE, ghost hologram preview
- **Artificial Star** 〔this mod + KubeJS〕: an MMCR multiblock registered by KubeJS, with a star-sphere renderer provided by this mod
- Built-in AE2 Guide tutorials (open with the Guide key in-game)

### Mod Integrations

**Applied Energistics 2 (AE2)** 〔required〕
- All quantum multiblocks hook directly into the ME network: patterns, automatic feeding, direct output
- Quantum Pattern Hatch: a 72-pattern AE2 pattern provider
- Stellar storage cells are AE2 cell formats for ME drives, with 40M–Infinity storage components
- Structure Terminal auto-builds from ME materials and can pull from any distance via a bound wireless access point
- Stellar Nexus draws energy and fuel from the ME network; stellar simulations can mass-produce AE2 Singularities

**Mekanism** 〔required〕
- The Assembler / Dragon Soul Forge / Vein Drill lines are Mekanism machines, compatible with its energy, chemicals and factory parallelism
- Quantum machine hatches store chemicals directly, and chemicals are accessible through the ME network
- Stellar simulations: Metallurgic Surge, Ethylene Enrichment, Fission Reactor Process (ethylene fuel)
- Astral Nexus armor grants Mekanism radiation immunity

**ExtendedAE+** 〔required〕
- Optimizes its IO port (ExIOPort) batch processing and bulk access
- Stellar simulations can mass-produce Entro Crystals

**AE2 Lightning Tech (AE2LT)** 〔required〕
- Deep integration with its IO ports, storage and tick-rate systems for high-volume throughput
- Maps AE2LT overload-recipe lightning costs into the Quantum Processing Factory (drawn from the ME network, refunded on cancel)

**Thunderbolt Core (AE2LT infrastructure)** 〔required〕
- Uses its fast autocrafting planner for large-scale crafting tasks

**NeoEcoAE** 〔required〕
- ECO Drive storage subsystem integration: Quantum Omni matrices fit in ECO drives, with consumption display
- Together with AE2 OmniCells, unlocks the full **ECO - LE9 storage matrix** ladder: NeoEcoAE provides the base tiers (e.g. 256m = 4 GiB), this mod extends them with the 16G–256T–256P series

**AE2 OmniCells** 〔required〕
- Quantum Omni matrices are built on its **universal storage backend**: every key type (items/fluids/energy...), unlimited types, 16G–256P capacity (16,384 MiB to 256 PiB, 13 tiers)

**Botania** 〔optional〕
- Adds the **Fluix Mana Pool** block
- Registers **Mana** as an AE2 key type, so mana moves through the ME network like items or fluids
- Provides mana container-item / import / export / external-storage strategies and attaches a `ManaReceiver` to AE2 blocks with generic internal storage

**FTB Quests** 〔optional〕
- Custom task types: **AE resource tasks** (match keys and amounts in the ME network) and **chemical tasks** (Mekanism chemicals)
- **Quest Detection Card**: scans the AE network and records keys, with a client-side key picker

**Jade (WAILA)** 〔required〕
- Multiblock status, Quantum Pattern Hatch cache, vein tier and drill durability display

**JEI** 〔optional〕
- Recipe categories: assembling, dragon soul forging, vein drill ore pools, drill repair, Wither summoning, fusion conversion
- Click a controller to view the 3D multiblock structure preview

**KubeJS** 〔optional〕
- Register and edit custom recipes, including stellar simulations; the Artificial Star multiblock is defined by KubeJS too

**MMCR** 〔optional〕
- Controller rendering for the Artificial Star multiblock

**AppFlux** 〔optional〕
- Draws FE from the AppFlux wireless energy network to recharge AE power items and hatches

**Advanced AE** 〔optional〕
- Stellar simulation for advanced quantum products

**ProjectE / Project Expansion** 〔optional〕
- Transmutation interfaces interoperate with ME storage

**GuideME** 〔optional〕
- In-game illustrated tutorials

**GeckoLib** 〔optional〕
- UFO armor and entity animations

## Dependencies

Required: Minecraft 1.21.1 · NeoForge 21.1.264+ · UFO Future (`ufo`) 3.0.0-beta.6+ · RaishX Core (`raishxcore`) 0.2+ · AE2 (`ae2`) 19.2.17–19.2.x · AE2LT (`ae2lt`) 2.1.0-beta.5+ · ExtendedAE+ (`extendedae_plus`) 1.5.5+ · NeoEcoAE (`neoecoae`) 21.2.10+ · AE2 OmniCells (`ae2omnicells`) 1.1.6+ · Mekanism (`mekanism`) 10.7.19+ · Jade (`jade`) 15.10.6+

Optional: Botania (`botania` 454+), FTB Quests (`ftbquests` 2101.1.0+), KubeJS, MMCR, JEI, AppFlux, Advanced AE, ProjectE / Project Expansion, GuideME, GeckoLib, Twilight Forest, Mekanism Extras, MekMM

## Building from source

Requires **JDK 21** (use `gradlew.bat` on Windows):

```bash
./gradlew build          # output in build/libs/
./gradlew runClient      # launch the dev client
./gradlew runServer      # launch the dev server
```

Runtime dependencies (UFO Future, AE2, ...) live in `run/mods/`; compile-time UFO jars live in `libs/`.

## License

This project follows the same dual-license model as AE2 and its addons (see [LICENSE.md](LICENSE.md)):

- **Source code**: GNU LGPL v3.0 or later (LGPLv3+)
- **Art, textures, models, sounds and other assets**: Creative Commons Attribution-NonCommercial-ShareAlike 3.0 (CC BY-NC-SA 3.0)

Source repository: https://github.com/yongaishide/chaosworld-core (this repository is the Corresponding Source required by LGPLv3)

### Credits

The mana integration in `com.yongaishide.chaosworld.botania` is ported from [Applied Botanics](https://github.com/ramidzkh/Applied-Botanics) by ramidzkh and contributors (LGPL-3.0-or-later); the `fluix_mana_pool` textures and block model come from that mod (CC BY-NC-SA 3.0). See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for the full file list and modification record.

