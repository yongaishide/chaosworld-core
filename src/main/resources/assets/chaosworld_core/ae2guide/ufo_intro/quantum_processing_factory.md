---
navigation:
  parent: ufo_intro/index.md
  title: Quantum Processing Factory
  position: 44
item_ids:
  - chaosworld_core:quantum_processing_factory_controller
  - chaosworld_core:quantum_pattern_hatch
---

# Quantum Processing Factory

<BlockImage id="chaosworld_core:quantum_processing_factory_controller" scale="4"></BlockImage>

The **Quantum Processing Factory** is the bulk processor-finishing multiblock.

## Features

- Up to **27 parallel jobs** in standard mode
- **9 parallel jobs** in Safe Mode
- AE2 pattern support through the **Quantum Pattern Hatch**
- Universal multiblock recipe support for processor families
- Direct ME network pull and push behavior for items and fluids

## Pattern Hatch

- The **Quantum Pattern Hatch** stores **72 encoded patterns**
- It exposes the controller to AE2 as a crafting machine
- Each pushed pattern reserves one free thread

## Thermal Profile

- Base heat generation: **1 HU per active thread per tick**.
- Overclock heat generation: **5 HU per active thread per tick**.
- Idle passive cooling: **-1 HU every 40 ticks**.
- Coolant tank values use the shared universal multiblock ladder:
- Gelid Cryotheum removes **1 HU per 120 mB**, up to **1000 mB/tick**.
- Stable Coolant removes **50 HU per mB**, up to **10 mB/tick**.
- Temporal Fluid removes **100 HU per mB**, up to **10 mB/tick**.
