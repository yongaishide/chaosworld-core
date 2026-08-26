---
navigation:
  parent: ufo_intro/index.md
  title: Stellar Nexus
  position: 50
item_ids:
  - chaosworld_core:stellar_nexus_controller
---

# Stellar Nexus

<BlockImage id="chaosworld_core:stellar_nexus_controller" scale="4"></BlockImage>

The **Stellar Nexus** is the endgame stellar simulation multiblock.

## Core Mechanics

- Reads inputs directly from the ME network
- Charges a **200B AE** internal buffer while assembled
- Consumes fuel on start
- Consumes coolant while running
- Generates heat continuously during operation
- Requires one item input hatch, one item output hatch, one fluid output hatch and one AE energy input hatch

## Field Tiers

The four field generator positions must all match the same tier:

- **MK1**
- **MK2**
- **MK3**

Mixed tiers invalidate the structure.

## Safety And Overclock

- **Safe Mode** costs **2.5x** AE, fuel and coolant, but shuts down instead of exploding
- **Overclock** gives **5x** speed, **10x** AE cost and **5x** fuel, heat and coolant use

## Heat Profile

- Base heat generation: **recipe cooling level + 1 HU/tick**.
- Overclock heat generation: **5x** the recipe heat per tick.
- Stronger recipes naturally run hotter, so cooling level is part of the machine's thermal burden.

## Coolant Ladder

- The controller tries to consume **100 mB/tick** of coolant while running.
- **Safe Mode** raises that to **250 mB/tick**.
- **Overclock** multiplies coolant draw by **5x** on top of that.
- **Gelid Cryotheum**: **1 cooling per mB**.
- **Stable Coolant**: **4 cooling per mB**.
- **Temporal Fluid**: **8 cooling per mB**.
- Final cooling is multiplied by your field tier bonus, so better field generators make the same coolant stronger.
