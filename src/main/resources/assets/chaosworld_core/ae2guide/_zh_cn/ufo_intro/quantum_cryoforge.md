---
navigation:
  parent: ufo_intro/index.md
  title: 量子低温锻造炉
  position: 44
item_ids:
  - chaosworld_core:quantum_cryoforge_controller
---
# 量子低温锻造炉

<BlockImage id="chaosworld_core:quantum_cryoforge_controller" scale="4"></BlockImage>

量子低温锻造炉是专门用于大规模冷却剂生产的通用多方块。

- 与其他通用多方块共享相同的控制器流程。
- 在有效外壳位置接受通用舱口。
- 专注于冷却剂和热流体吞吐。
- 机器等级来自安装的恒星场生成器。

## 热力配置

- 热量生成为普通通用多方块的 50%。
- 基础热量生成：活跃线程数 x 0.5 HU/tick（向上取整）。
- 超频热量生成：活跃线程数 x 5 x 0.5 HU/tick（向上取整）。
- 空闲被动冷却：每 40 tick -1 HU。

## 冷却剂储罐

- 凝滞冷却剂：每 120 mB 移除 1 HU，最高 1000 mB/tick。
- 稳定冷却剂：每 mB 移除 50 HU，最高 10 mB/tick。
- 时间流体：每 mB 移除 100 HU，最高 10 mB/tick。

## 稳定冷却剂门槛

稳定冷却剂是 MK3 量子低温锻造炉的配方。

在期望稳定冷却剂配方运行之前，将每个场生成器位置替换为恒星场生成器 Mk.III。凝滞冷却剂是早期冷却路径。
