---
navigation:
  parent: ufo_intro/index.md
  title: Stellar Fields
  position: 47
item_ids:
  - chaosworld_core:stellar_field_generator_t1
  - chaosworld_core:stellar_field_generator_t2
  - chaosworld_core:stellar_field_generator_t3
---

# Stellar Field Generators

<BlockImage id="chaosworld_core:stellar_field_generator_t1" scale="3"></BlockImage>

Field generators are the stabilization blocks used throughout the advanced UFO multiblock line.

- Universal multiblocks use the **lowest field generator tier** in their field positions as the machine tier.
- Recipes with a higher required tier will not run until the machine tier is high enough.
- Recipes below the installed tier run faster and use less AE: each extra tier halves processing time and applies a **0.75x** AE cost multiplier.

## Universal Coolant Priority

- **MK1** prefers Gelid Cryotheum: **1 HU per 120 mB**, up to **1000 mB/tick**.
- **MK2** prefers Stable Coolant: **50 HU per mB**, up to **10 mB/tick**.
- **MK3** prefers Temporal Fluid: **100 HU per mB**, up to **10 mB/tick**.

## Stellar Nexus Fields

The Stellar Nexus is stricter than universal multiblocks: all four field generator positions must use the same tier.

- **MK1** charges the Nexus buffer at **500K AE/t**.
- **MK2** charges the Nexus buffer at **1M AE/t**.
- **MK3** charges the Nexus buffer at **2M AE/t**.
- Better field tiers multiply Nexus cooling strength: **MK1 x2**, **MK2 x3**, **MK3 x4**.
- If unsafe overheating is allowed, failure radius scales by tier: **30 / 50 / 100 blocks**.
