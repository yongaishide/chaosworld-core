---
navigation:
  parent: ufo_intro/index.md
  title: 恒星场
  position: 47
item_ids:
  - chaosworld_core:stellar_field_generator_t1
  - chaosworld_core:stellar_field_generator_t2
  - chaosworld_core:stellar_field_generator_t3
---
# 恒星场生成器

<BlockImage id="chaosworld_core:stellar_field_generator_t1" scale="3"></BlockImage>

场生成器是 UFO 高级多方块系统中使用的稳定化方块。

- 通用多方块使用其场位置中最低等级的场生成器作为机器等级。
- 需求等级高于当前机器等级的配方将保持锁定。
- 低于安装等级的配方运行更快、消耗更少 AE：每高一级时间减半，并应用 0.75x AE 消耗倍率。

## 通用冷却优先级

- MK1 优先使用凝滞冷却剂：每 120 mB 移除 1 HU，最高 1000 mB/tick。
- MK2 优先使用稳定冷却剂：每 mB 移除 50 HU，最高 10 mB/tick。
- MK3 优先使用时间流体：每 mB 移除 100 HU，最高 10 mB/tick。

## 恒星联结场

恒星联结比通用多方块更严格：所有四个场生成器位置必须使用同一等级。

- Mk.I 以 500K AE/t 为缓冲充能。
- Mk.II 以 1M AE/t 为缓冲充能。
- Mk.III 以 2M AE/t 为缓冲充能。
- 更高的场等级倍增冷却强度：Mk.I x2、Mk.II x3、Mk.III x4。
- 如果允许不安全过热，失败半径随等级缩放：30 / 50 / 100 格。
