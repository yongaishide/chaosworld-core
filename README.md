# 乱界核心（Chaos World Core）

**Minecraft 1.21.1 · NeoForge · 以 AE2 为核心的终局扩展模组**

乱界核心（Chaos World Core）是 ChaosWorld 整合包的核心模组，整合了 UFO Future 模组的全部内容，为 AE2 的终局玩法提供热力学机械、恒星物质、巨型存储与多方块自动化。从第一台 Dimensional Matter Assembler，到最终极的 Stellar Nexus 恒星模拟平台，构建属于你的星际工业线。

---

## 主要内容

### 量子多方块机械

- **Dimensional Matter Assembler（DMA）**：入门级先进机器，拥有热量系统与催化剂插槽，可制作处理器、催化剂、星体物质与多种流体
- **Quantum Matter Fabricator（QMF）**：DMA 的多方块进化版，面向 AE2 自动化与大批量合成
- **Quantum Slicer**：批量加工印刷处理器部件
- **Quantum Processing Factory**：批量精加工处理器
- **Quantum Cryoforge**：低温锻炉，生产稳定冷却剂等高级流体
- **Quantum Pattern Hatch**：支持 **72 组样板** 的 AE2 样板仓，配合同系列多方块使用
- **MK1 / MK2 / MK3 分级机制**：更高等级解锁更高级配方，并自动加速旧配方

### Stellar Nexus 恒星模拟平台

终局级多方块，模拟真实恒星物理过程进行大规模生产：

- 模拟配方：红巨星坍缩、铁核聚变、钻石高压、中子轰击、超新星收割、恒星合成……
- **Stellar Field Generator Mk.I~III**：提供场强等级，越高越强
- 燃料、冷却剂、热量管理系统，支持**安全模式**（过热自动停机）与**超频模式**（10 倍能量、5 倍热量/速度）
- 热量失控会导致灾难性爆炸——请务必打开安全模式
- 直接接入 ME 网络供料与输出

### 巨型存储

- **白矮星 / 脉冲星 / 中子星存储单元**：容量从 1G 到 **256T**，另有带前缀的 Echo / Beacon / Nexus / Core / Singularity 系列
- 无限存储组件、ECO - LE9 量子全能存储矩阵等 AE2 附属格式
- 能量单元：Event Horizon Energy Cell、Quantum Energy Cell

### 星体物质进阶线

Proto Matter → Corporeal Matter → 白矮星物质 → 中子星物质 → 脉冲星物质 → 暗物质 → 不稳定白洞物质

Nuclear Star → Neutronium Sphere → Enriched Neutronium Sphere → Charged Enriched Neutronium Sphere

- 星体碎片：白矮星 / 脉冲星 / 中子星（锭、粉、粒、棒、流体）
- 催化剂：Matterflow / Chrono / Overflux / Quantum（T1~T3）——改变合成速度、能耗、失败率与产物加成

### UFO 套装与装备

- **UFO 工具**：剑（灵魂收割，击杀成长）、镐（自动熔炼、进度时运）、锤、巨剑、法杖、弓、钓鱼竿等
- **UFO 盔甲**：头盔、胸甲、护腿、靴子
- **Astral Nexus 套装**：无限护甲、绝对免伤、100 万倍伤害反射、创造飞行、辐射免疫
- **Reality Ripper**：无限伤害，甚至能击杀创造模式玩家
- **Thermal Resistor 套装**：热阻护甲

### 流体与安全收容

- 流体：UU 物质、UU 增幅剂、寒冰之尘 / 凝胶寒冰、液体星光、原初物质、原始恒星等离子体、超越物质、时空流体、稳定冷却剂等
- **Aether Containment Capsule（ACC）**：安全储存危险流体
- **Safe Containment Matter（SCM）**：收容危险物品
- 凋灵召唤重构：搭建结构并手持苦难核心（Cryptid Core）右键，可召唤 4000 血的强化凋灵

### 实用工具

- **Structure Terminal**：扫描 / 自动建造 / 拆解多方块结构，支持 AE 材料取用、幻影全息预览
- 内置 AE2 Guide 图文教程（游戏中按 Guide 键打开）

### 与其他模组的联动

**Applied Energistics 2（AE2）**〔必须依赖〕
- 所有量子多方块直接接入 ME 网络：样板、自动供料、产物直接输出
- Quantum Pattern Hatch / Quantum Pattern Provider：72 组样板的 AE2 样板仓
- 巨型存储单元（白矮星 / 脉冲星 / 中子星）为 AE2 存储单元格式，可插入 ME 驱动器，配套 40M~Infinity 存储组件
- Structure Terminal：从 ME 网络取材料自动建造、绑定无线接入点（WAP）远距离取料
- Stellar Nexus 从 ME 网络抽取能量与燃料
- 恒星模拟可批量合成 AE2 奇点

**ExtendedAE+（扩展AE）**〔必须依赖〕
- 优化其 IO 端口（ExIOPort）批量处理与大容量存取
- 恒星模拟可批量合成 Entro 水晶

**AE2 Lightning Tech（AE2LT 闪电科技）**〔必须依赖〕
- 深度联动其 IO 端口、存储服务与 Tick 速率系统，支持大容量高速流转

**Thunderbolt Core（AE2LT 底层库）**〔必须依赖〕
- 使用其快速自动合成规划器，优化大规模合成任务

**NeoEcoAE**〔必须依赖〕
- ECO 驱动器（ECO Drive）存储子系统联动与能耗显示
- Quantum Omni 存储矩阵系列使用 ECO 存储子系统格式，详见下方与 AE2 OmniCells 的共同联动

**AE2 OmniCells**〔必须依赖〕
- Quantum Omni 存储矩阵（ECO - LE9）系列：与 **NeoEcoAE + AE2 OmniCells 共同联动**——使用 NeoEcoAE 的 ECO 存储子系统（ECO Drive 容器格式）搭配 AE2 OmniCells 的全能存储后端（全键类型、无限类型数），容量 1M~274G MiB

**AppFlux**〔可选〕
- 从 AppFlux 无线能量网络提取 FE 能量，为 AE 储能物品（如能量单元）与能量仓口充电

**Mekanism**〔可选〕
- 量子机械仓口可直接存取化学物质（化学品处理）
- 化学物质可进入 ME 网络存取（Mekanism 化学物质键）
- 恒星模拟配方：冶金激增、乙烯富集、裂变堆工艺（以乙烯为燃料）
- Astral Nexus 套装免疫 Mekanism 辐射

**Advanced AE**〔可选〕
- 恒星模拟可合成高级量子产物（advancedae_quantum）

**KubeJS**〔可选〕
- 通过 KubeJS 注册与编辑恒星模拟等自定义配方

**JEI**〔可选〕
- 全部配方分类：DMA、量子多方块、恒星模拟、凋灵召唤
- 点击控制器可查看 3D 多方块结构预览

**Jade（WAILA）**〔必须依赖〕
- 多方块结构状态与 Quantum Pattern Hatch 缓存信息显示

**ProjectE / Project Expansion**〔可选〕
- 炼金术转换接口与 ME 存储互通：可经由转换接口存取 ME 网络物品

**GuideME**〔可选〕
- 游戏内图文教程（按 Guide 键打开）

**GeckoLib**〔可选〕
- UFO 套装与实体动画渲染

---

## 依赖

- NeoForge **21.1.x**
- Minecraft **1.21.1**
- Applied Energistics 2（AE2）`19.2.17+`
- ExtendedAE+ `1.5.5+`、Jade、AE2 Lightning Tech（AE2LT）`2.0.9+`、Thunderbolt Core `1.0.1+`、NeoEcoAE、AE2 OmniCells

---

## 许可

本项目采用与 AE2 及其附属模组一致的双许可模式（详见 [LICENSE.md](LICENSE.md)）：

- **源代码**：GNU LGPL v3.0 或更高版本（LGPLv3+）
- **美术、纹理、模型、音效等素材**：Creative Commons Attribution-NonCommercial-ShareAlike 3.0（CC BY-NC-SA 3.0）

---

---

# Chaos World Core

**Minecraft 1.21.1 · NeoForge · An AE2 endgame expansion**

Chaos World Core (乱界核心) is the core mod of the ChaosWorld modpack, incorporating all content from the UFO Future mod. It extends the AE2 endgame with thermal machines, stellar matter, mega storage and multiblock automation — from your first Dimensional Matter Assembler all the way to the ultimate Stellar Nexus simulation platform.

## Features

### Quantum Multiblocks

- **Dimensional Matter Assembler (DMA)**: entry-level advanced machine with a heat system and catalyst slots — processors, catalysts, stellar matter and fluids
- **Quantum Matter Fabricator (QMF)**: the multiblock evolution of the DMA for AE2 automation and heavy bulk jobs
- **Quantum Slicer**: bulk printed-part preparation
- **Quantum Processing Factory**: bulk processor finishing
- **Quantum Cryoforge**: low-temperature forge for Stable Coolant and advanced fluids
- **Quantum Pattern Hatch**: a **72-pattern** AE2 pattern provider for the multiblock line
- **MK1 / MK2 / MK3 tiering**: higher tiers unlock better recipes and automatically accelerate older ones

### Stellar Nexus

The endgame multiblock that simulates real stellar physics for extreme-scale production:

- Simulations: Red Giant Collapse, Iron Core Fusion, Diamond Pressure, Neutron Bombardment, Supernova Harvest, Stellar Synthesis and more
- **Stellar Field Generators Mk.I–III** provide the field strength
- Fuel, coolant and heat management with **Safe Mode** (auto-shutdown on overheat) and **Overclocking** (10x energy, 5x heat/speed)
- Thermal failure causes a catastrophic explosion — keep Safe Mode on!
- Feeds from and outputs directly into your ME network

### Mega Storage

- **White Dwarf / Pulsar / Neutron Star cells** from 1G up to **256T**, plus Echo / Beacon / Nexus / Core / Singularity variants
- Infinity Storage Component, ECO - LE9 Quantum Omni storage matrices and more AE2 addon formats
- Energy cells: Event Horizon Energy Cell, Quantum Energy Cell

### Stellar Matter Progression

Proto Matter → Corporeal Matter → White Dwarf Matter → Neutron Star Matter → Pulsar Matter → Dark Matter → Unstable White Hole Matter

Nuclear Star → Neutronium Sphere → Enriched Neutronium Sphere → Charged Enriched Neutronium Sphere

- Star fragments: white dwarf / pulsar / neutron star (ingots, dust, nuggets, rods, fluids)
- Catalysts: Matterflow / Chrono / Overflux / Quantum (T1–T3) — alter speed, energy cost, failure chance and bonus output

### UFO Gear

- **UFO tools**: sword (Soul Harvest that grows with kills), pickaxe (Auto-Smelt, progressive Fortune), hammer, greatsword, staff, bow, fishing rod and more
- **UFO armor**: helmet, chestplate, leggings, boots
- **Astral Nexus set**: infinite armor, absolute immunity, 1,000,000x damage reflect, creative flight, radiation immunity
- **Reality Ripper**: infinite damage — can even kill creative players
- **Thermal Resistor suit**: heat-resistant armor

### Fluids & Containment

- Fluids: UU Matter, UU Amplifier, Cryotheum Dust / Gelid Cryotheum, Liquid Starlight, Primordial Matter, Raw Star Matter Plasma, Transcending Matter, Spatial/Temporal Fluid, Stable Coolant and more
- **Aether Containment Capsule (ACC)**: safely store hazardous fluids
- **Safe Containment Matter (SCM)**: contain hazardous items
- Wither summoning rework: build the structure and right-click with a Cryptid Core (苦难核心) to summon a 4000 HP empowered Wither

### Utilities

- **Structure Terminal**: scan, auto-build and dismantle multiblocks, pull materials from AE, ghost hologram preview
- Built-in AE2 Guide tutorials (open with the Guide key in-game)

### Mod Integrations

**Applied Energistics 2 (AE2)** 〔required〕
- All quantum multiblocks hook directly into the ME network: patterns, automatic feeding, direct output
- Quantum Pattern Hatch / Quantum Pattern Provider: a 72-pattern AE2 pattern provider
- Mega cells (White Dwarf / Pulsar / Neutron Star) are AE2 cell formats for ME drives, with 40M–Infinity storage components
- Structure Terminal: auto-builds from ME materials and can pull from any distance via a bound wireless access point
- Stellar Nexus draws energy and fuel from the ME network
- Stellar simulations can mass-produce AE2 Singularities

**ExtendedAE+** 〔required〕
- Optimizes its IO port (ExIOPort) batch processing and bulk access
- Stellar simulations can mass-produce Entro Crystals

**AE2 Lightning Tech (AE2LT)** 〔required〕
- Deep integration with its IO ports, storage service and tick-rate systems for high-volume throughput

**Thunderbolt Core (AE2LT infrastructure)** 〔required〕
- Uses its fast autocrafting planner for large-scale crafting tasks

**NeoEcoAE** 〔required〕
- ECO Drive storage subsystem integration with consumption display
- The Quantum Omni storage matrix series uses the ECO storage subsystem format (see the joint integration with AE2 OmniCells below)

**AE2 OmniCells** 〔required〕
- Quantum Omni storage matrix (ECO - LE9) series: a **joint integration of NeoEcoAE + AE2 OmniCells** — NeoEcoAE's ECO storage subsystem (ECO Drive cell format) paired with AE2 OmniCells' universal storage backend (all key types, unlimited types), 1M–274G MiB capacity

**AppFlux** 〔optional〕
- Draws FE power from the AppFlux wireless energy network to recharge AE power items (e.g. energy cells) and hatches

**Mekanism** 〔optional〕
- Quantum machine hatches can store chemicals directly (IChemicalHandler)
- Chemicals can be accessed through the ME network (Mekanism chemical keys)
- Stellar simulations: Metallurgic Surge, Ethylene Enrichment, Fission Reactor Process (ethylene fuel)
- Astral Nexus armor grants Mekanism radiation immunity

**Advanced AE** 〔optional〕
- Stellar simulation for advanced quantum products

**KubeJS** 〔optional〕
- Register and edit custom recipes, including stellar simulations

**JEI** 〔optional〕
- All recipe categories: DMA, quantum multiblocks, stellar simulations, Wither summoning
- Click a controller to view the 3D multiblock structure preview

**Jade (WAILA)** 〔required〕
- Multiblock status and Quantum Pattern Hatch cache display

**ProjectE / Project Expansion** 〔optional〕
- Transmutation interfaces interoperate with ME storage

**GuideME** 〔optional〕
- In-game illustrated tutorials (open with the Guide key)

**GeckoLib** 〔optional〕
- UFO armor and entity animations

## Dependencies

- NeoForge **21.1.x**
- Minecraft **1.21.1**
- Applied Energistics 2 (AE2) `19.2.17+`
- ExtendedAE+ `1.5.5+`, Jade, AE2 Lightning Tech (AE2LT) `2.0.9+`, Thunderbolt Core `1.0.1+`, NeoEcoAE, AE2 OmniCells

## License

This project follows the same dual-license model as AE2 and its addons (see [LICENSE.md](LICENSE.md)):

- **Source code**: GNU LGPL v3.0 or later (LGPLv3+)
- **Art, textures, models, sounds and other assets**: Creative Commons Attribution-NonCommercial-ShareAlike 3.0 (CC BY-NC-SA 3.0)
