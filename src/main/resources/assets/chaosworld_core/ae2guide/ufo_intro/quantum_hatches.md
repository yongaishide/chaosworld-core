---
navigation:
  parent: ufo_intro/index.md
  title: Quantum Hatches
  position: 46
item_ids:
  - chaosworld_core:quantum_pattern_hatch
  - chaosworld_core:quantum_pattern_provider_part
  - chaosworld_core:me_massive_output_hatch
  - chaosworld_core:me_massive_fluid_hatch
  - chaosworld_core:me_massive_input_hatch
  - chaosworld_core:ae_energy_input_hatch
---

# Quantum Hatches

<BlockImage id="chaosworld_core:quantum_pattern_hatch" scale="3"></BlockImage>

The **Quantum Pattern Hatch** exposes universal multiblocks to AE2 autocrafting and stores up to **72 patterns**.

<ItemImage id="chaosworld_core:quantum_pattern_provider_part" scale="3" />

The **Quantum Pattern Provider** is the cable-bus part version of the expanded provider.

- **ME Massive Output Hatch** returns finished items to the ME network.
- **ME Massive Fluid Hatch** handles large-scale fluid transfer.
- **ME Massive Input Hatch** feeds bulk item throughput into multiblocks.
- **AE Energy Input Hatch** is the dedicated AE power hatch.

## AE2 Connection Rules

Massive hatches are **ME cable ports**, not sided inventories or normal tanks.

- Connect an ME cable to any side of the hatch.
- Put item and fluid inputs in ME storage.
- Finished item and fluid outputs return to ME storage through the matching hatch.
- The AE Energy Input Hatch lets the multiblock draw AE from the connected grid.
- Use the hatch role the structure asks for: item input, item output, fluid output or AE energy.
