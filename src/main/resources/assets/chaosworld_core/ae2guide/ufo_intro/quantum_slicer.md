---
navigation:
  parent: ufo_intro/index.md
  title: Quantum Slicer
  position: 43
item_ids:
  - chaosworld_core:quantum_slicer_controller
  - chaosworld_core:quantum_pattern_hatch
---

# Quantum Slicer

<BlockImage id="chaosworld_core:quantum_slicer_controller" scale="4"></BlockImage>

The **Quantum Slicer** prepares printed components for large processor and circuit pipelines.

## What It Does

- Uses the universal multiblock recipe system
- Supports up to **27 parallel jobs** in standard mode
- Drops to **9 parallel jobs** in Safe Mode
- Accepts AE2 encoded patterns through the **Quantum Pattern Hatch**
- Pulls ingredients from ME and returns outputs automatically

## Pattern Hatch

- The **Quantum Pattern Hatch** stores **72 encoded patterns**
- It links to the controller after assembly
- AE2 can push jobs without manual inventory handling

## Thermal Profile

- Base heat generation: **1 HU per active thread per tick**.
- Overclock heat generation: **5 HU per active thread per tick**.
- Idle passive cooling: **-1 HU every 40 ticks**.
- Coolant tank values use the shared universal multiblock ladder:
- Gelid Cryotheum removes **1 HU per 120 mB**, up to **1000 mB/tick**.
- Stable Coolant removes **50 HU per mB**, up to **10 mB/tick**.
- Temporal Fluid removes **100 HU per mB**, up to **10 mB/tick**.
