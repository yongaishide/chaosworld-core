---
navigation:
  parent: ufo_intro/index.md
  title: Quantum Matter Fabricator
  position: 42
item_ids:
  - chaosworld_core:quantum_matter_fabricator_controller
  - chaosworld_core:quantum_pattern_hatch
---

# Quantum Matter Fabricator

<BlockImage id="chaosworld_core:quantum_matter_fabricator_controller" scale="4"></BlockImage>

The **QMF** is the multiblock evolution of the DMA for heavy automation, bulk crafting and AE2 pattern workflows.

## Main Benefits

- Up to **27 parallel threads** in standard mode
- **9 parallel threads** in Safe Mode
- Accepts both native **QMF recipes** and **DMA recipes**
- Supports AE2 autocrafting through the **Quantum Pattern Hatch**
- Reads ingredients directly from the connected ME network
- Pushes outputs back into ME automatically

## Quantum Pattern Hatch

<BlockImage id="chaosworld_core:quantum_pattern_hatch" scale="3"></BlockImage>

- Stores up to **72 encoded patterns**
- Links to the controller when the structure is assembled
- Exposes the multiblock as a crafting machine to AE2
- Lets the controller run multiple jobs in parallel

## Thermal Profile

- Base heat generation: **1 HU per active thread per tick**.
- Overclock heat generation: **5 HU per active thread per tick**.
- Idle passive cooling: **-1 HU every 40 ticks**.
- Coolant tank values use the shared universal multiblock ladder:
- Gelid Cryotheum removes **1 HU per 120 mB**, up to **1000 mB/tick**.
- Stable Coolant removes **50 HU per mB**, up to **10 mB/tick**.
- Temporal Fluid removes **100 HU per mB**, up to **10 mB/tick**.
