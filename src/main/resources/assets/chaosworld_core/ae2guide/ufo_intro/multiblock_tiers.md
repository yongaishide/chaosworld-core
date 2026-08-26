---
navigation:
  parent: ufo_intro/index.md
  title: Multiblock Tiers
  position: 45
---

# Multiblock Tiers

Universal multiblocks use a shared **MK tier** system.

## Recipe Locking
- **MK1** controllers run MK1 recipes.
- **MK2** controllers run MK1 and MK2 recipes.
- **MK3** controllers run MK1, MK2 and MK3 recipes.

If the machine tier is below the recipe tier, the recipe stays locked.

## Tier Buffs
When a higher-tier multiblock runs a lower-tier recipe, it gets automatic scaling:

- **Time**: halved for each tier above the recipe.
- **Energy**: reduced to **75%** for each tier above the recipe.

Examples:
- MK2 machine running MK1 recipe = **2x speed** and **25% less energy**
- MK3 machine running MK1 recipe = **4x speed** and **43.75% less energy**

## Why This Matters
Upgrading tiers is not only about unlocking new recipes. It also compresses old production lines into much faster background throughput.

This is what lets a late-game base keep older chains running while the player moves into heavier simulations and multiblock automation.
