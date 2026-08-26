---
navigation:
  parent: ufo_intro/index.md
  title: Quantum Cryoforge
  position: 44
item_ids:
  - chaosworld_core:quantum_cryoforge_controller
---

# Quantum Cryoforge

<BlockImage id="chaosworld_core:quantum_cryoforge_controller" scale="4"></BlockImage>

The **Quantum Cryoforge** is the universal multiblock dedicated to large-scale coolant production.

- Shares the same controller flow as the other universal multiblocks.
- Accepts universal hatches in valid casing positions.
- Focuses on coolant and thermal-fluid throughput.
- Machine tier comes from the installed Stellar Field Generators.

## Thermal Profile

- Heat generation is reduced to **50%** of the normal universal multiblock rate.
- Base heat generation: **ceil(active threads x 0.5) HU/tick**.
- Overclock heat generation: **ceil(active threads x 5 x 0.5) HU/tick**.
- Idle passive cooling: **-1 HU every 40 ticks**.

## Coolant Tank

- **Gelid Cryotheum** removes **1 HU per 120 mB**, up to **1000 mB/tick**.
- **Stable Coolant** removes **50 HU per mB**, up to **10 mB/tick**.
- **Temporal Fluid** removes **100 HU per mB**, up to **10 mB/tick**.

## Stable Coolant Gate

Stable Coolant is a **MK3 Quantum Cryoforge** recipe.

Replace every field generator position with **MK3 Stellar Field Generators** before expecting the Stable Coolant recipe to run. Gelid Cryotheum remains the early coolant path.
