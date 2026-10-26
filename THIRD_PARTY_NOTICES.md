# 第三方声明 / Third-Party Notices

Chaos World Core（`chaosworld_core`）包含由下列第三方作品衍生的代码与素材。本文件按
LGPLv3 第 5 条（修改声明）与 CC BY-NC-SA 3.0 第 4 条（署名与相同方式共享）的要求，
逐条记录出处、许可证与修改情况。

This project contains code and assets derived from the third-party works listed below.
This file records provenance, licensing and modification details as required by
LGPLv3 §5 (notices of modification) and CC BY-NC-SA 3.0 §4 (attribution and share-alike).

---

## 1. Applied Botanics

| 项目 | 内容 |
| --- | --- |
| 上游 / Upstream | https://github.com/ramidzkh/Applied-Botanics |
| 作者 / Authors | ramidzkh and contributors |
| 基准版本 / Based on | `1.6.0-alpha.3`（1.21.1 分支线） |
| 代码许可证 / Code license | LGPL-3.0-or-later |
| 素材许可证 / Asset license | CC BY-NC-SA 3.0 Unported |
| 修改日期 / Modified on | 2026-10-26 |
| 修改人 / Modified by | chaosworld（Chaos World Core） |

### 1.1 修改说明 / Nature of modification

- 移植到 Minecraft 1.21.1 / NeoForge 21.1.264 的工程结构（ModDevGradle，`chaosworld_core` 为唯一 mod id）。
- 包名由 `appbot.*` 改为 `com.yongaishide.chaosworld.botania`，类归属与注册入口并入本项目的
  `ChaosWorld.BLOCKS` / `ChaosWorld.ITEMS` 延迟注册器。
- 适配 Botania 1.21.1 的 API 变更：`SparkAttachable` → `ManaSparkAttachable`，
  `BotaniaForgeCapabilities` → `BotaniaNeoForgeCapabilities`。
- 未移植的部分：mana cell 系列物品、便携式 mana cell、Mana P2P 隧道、Corporea 集成、
  EMI/JEI/IGTooltip 集成与数据生成器。
- 新增的部分：`FluixPool` 的行为调整、`registerGenericAdapters` 的 NeoForge 能力签名更新。

### 1.2 衍生源文件 / Derived source files

上游文件所在路径前缀：`src/main/java/`；本仓库文件所在路径前缀：
`src/main/java/com/yongaishide/chaosworld/`。

| 本仓库文件 | 上游文件 |
| --- | --- |
| `botania/SafeMana.java` | `appbot/ae2/SafeMana.java` |
| `botania/ManaKey.java` | `appbot/ae2/ManaKey.java` |
| `botania/ManaKeyType.java` | `appbot/ae2/ManaKeyType.java` |
| `botania/ManaHelper.java` | `appbot/ae2/ManaHelper.java` |
| `botania/ManaContainerItemStrategy.java` | `appbot/ae2/ManaContainerItemStrategy.java` |
| `botania/ManaExternalStorageStrategy.java` | `appbot/ae2/ManaExternalStorageStrategy.java` |
| `botania/ManaGenericStackInvStorage.java` | `appbot/ae2/ManaGenericStackInvStorage.java` |
| `botania/ManaStorageExportStrategy.java` | `appbot/ae2/ManaStorageExportStrategy.java` |
| `botania/ManaStorageImportStrategy.java` | `appbot/ae2/ManaStorageImportStrategy.java` |
| `botania/package-info.java` | `appbot/ae2/package-info.java` |
| `botania/FluixPool.java` | `appbot/block/FluixPool.java` |
| `botania/FluixPoolBlockEntity.java` | `appbot/block/FluixPoolBlockEntity.java` |
| `botania/client/ManaRenderer.java` | `appbot/client/ManaRenderer.java` |
| `botania/ChaosBotania.java` | `appbot/AppliedBotanicsForge.java`（注册与能力适配部分） |

上述文件均以 **LGPL-3.0-or-later** 授权，与本项目 `LICENSE.md` 的源码条款一致。
每个文件头部带有简短的来源声明，指回本文件。

### 1.3 衍生素材 / Derived assets

上游素材路径前缀：`src/main/resources/assets/appbot/textures/block/`；
本仓库素材路径前缀：`src/main/resources/assets/chaosworld_core/textures/block/`。

| 本仓库文件 | 上游文件 | 字节数 |
| --- | --- | --- |
| `fluix_mana_pool.png` | `fluixpool.png` | 6395 |
| `fluix_mana_pool_greebles.png` | `fluixpoolgreebles.png` | 7470 |
| `fluix_mana_pool_inside.png` | `fluixpoolinside.png` | 6867 |

三张文件为上游素材的逐字节副本（SHA-256 一致），**仅**做了重命名，因此仍以
**CC BY-NC-SA 3.0 Unported** 授权，不得用于商业用途；任何修改后的版本必须继续以同一
协议发布。署名方式：

> Fluix Mana Pool textures by ramidzkh and contributors, from
> [Applied Botanics](https://github.com/ramidzkh/Applied-Botanics),
> licensed under [CC BY-NC-SA 3.0](https://creativecommons.org/licenses/by-nc-sa/3.0/).

此外，方块模型 `assets/chaosworld_core/models/block/fluix_mana_pool.json`（6288 字节）
衍生自上游 `assets/appbot/models/block/fluix_mana_pool.json`（6241 字节），已修改贴图
引用路径（`appbot:block/fluixpool*` → `chaosworld_core:block/fluix_mana_pool*`）。依照
CC BY-NC-SA 3.0 的相同方式共享要求，该模型随贴图一并按同一协议处理。

同目录下的 `blockstates/fluix_mana_pool.json`（94 字节）、`models/item/fluix_mana_pool.json`
（55 字节）、`data/chaosworld_core/loot_table/blocks/fluix_mana_pool.json` 与
`data/chaosworld_core/recipe/fluix_mana_pool.json` 为本项目自行编写的占位文件，不含
上游内容。

---

## 2. 互操作对象（未包含其代码）/ Interoperated with (no code bundled)

下列项目仅作为编译期依赖或运行期可选依赖被引用，本仓库不包含其源代码或素材：

| 项目 | 用途 | 关系 |
| --- | --- | --- |
| [Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2) | AE2 API、键类型、存储策略、P2P 隧道 | 编译期 + 运行期，LGPLv3 |
| [Botania](https://github.com/VazkiiMods/Botania) | `ManaReceiver` / `ManaSparkAttachable` API、魔力池参考实现 | 编译期 + 运行期（可选，`ModList` 守卫） |
| UFO Future | 量子多方块、恒星物理、巨型存储底座 | 编译期扩展目标 + 运行期 |

---

## 3. 许可证兼容性说明 / License compatibility

- 衍生自 Applied Botanics 的源码为 LGPL-3.0-or-later，本项目整体源码亦为
  LGPL-3.0-or-later，符合 LGPLv3 §2（修改版本须以本协议授权）的要求。
- 分发本项目的二进制产物时，须同时提供对应源码（LGPLv3 §4、§6）。本仓库即对应源码，
  发布页面须给出指向本仓库的链接。
- 若以整合包形式分发，Applied Botanics 的贴图素材要求该整合包**不得用于商业用途**；
  如有商业化需求，须替换 `fluix_mana_pool` 系列三张贴图为原创素材。
