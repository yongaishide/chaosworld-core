---
navigation:
  parent: ufo_intro/index.md
  title: Advanced Star Matter
  position: 20
---

# Advanced Star Matter (Ingot Guide)

The advanced materials used across UFO Future are manufactured in the Dimensional Matter Assembler (DMA). They are not mined directly from the world. These high-density ingots form the backbone of late-game tech, including processors, mega-storage, and armor alloys.

This page documents the primary star-matter ingots, how to obtain them, and their typical uses.

---

## White Dwarf Ingot

**Role:** Base high-density ingot used in many mid-to-late-game components.

### Acquisition

White Dwarf Ingots are crafted in the DMA from compressed precursor materials and reactive fluids. Use the DMA recipe for `white_dwarf_ingot` to convert base components into this ingot.

<Recipe id="chaosworld_core:dma/ingot/white_dwarf_fragment" />

### Common uses

- Parts for advanced machines
- Early-tier star-matter alloys
- Component for Thermal Resistor Exosuit pieces

---

## Neutron Star Ingot

**Role:** Denser and more energetic than White Dwarf; used for high-tier components.

### Acquisition

Produced in the DMA using more exotic inputs, higher energy cost, and special fluids or catalysts.

<Recipe id="chaosworld_core:dma/ingot/neutron_star_fragment" />

### Common uses

- Mega Co-Processors
- Advanced storage matrices
- High-tier armor components and tools

---

## Pulsar Ingot

**Role:** An advanced, high-yield ingot intended for endgame builds.

### Acquisition

Crafted in the DMA with top-tier inputs and usually requires strong catalysts and premium coolant, such as Transcending Matter Fluid.

<Recipe id="chaosworld_core:dma/ingot/pulsar_fragment" />

### Common uses

- Top-tier processors and matrices
- Endgame armor upgrades and components
- High-value progression gate items

---

## Other notable materials

The mod also includes several related high-density items and transitional materials used in ingot production:

- **UU-Matter Crystal:** a solid form used in certain DMA recipes and catalyst tiers.
  <Recipe id="chaosworld_core:dma/uu_matter_crystal" />
- **Dark Matter / Proto Matter / Transcending Matter:** special-tier materials used as inputs for the highest-tier recipes or as fluids for coolant and processing steps.

---

## Notes & best practices

- **DMA only:** These ingots must be assembled in the DMA. Make sure you have sufficient energy, catalysts, and coolant before attempting advanced recipes.
- **Catalyst & coolant guidance:** High-tier ingots commonly require T2/T3 catalysts and premium coolants. Use Overflux to reduce failure risk and Matterflow to reduce energy cost.
- **Recipe complexity rule:** Some DMA recipes enforce `ItemInputs + FluidInputs ≤ 4`. If a DMA recipe fails validation, check the number of distinct item/fluid inputs.
- **Server admins:** Consider gating Pulsar and Transcending-tier recipes behind progression, quests, boss drops, or research to avoid early-game imbalance.

---

## Troubleshooting

- Recipe is not showing in DMA: verify the recipe ID and that the DMA `data/` JSON is enabled in your datapack or mod.
- Frequent failures: add Overflux catalysts and stronger coolant, or reduce Chrono catalysts that increase heat.
- High energy drain: add Matterflow T2/T3 to reduce energy cost.

Use JEI/GuideME recipe lookup if a pack overrides these DMA recipes.
