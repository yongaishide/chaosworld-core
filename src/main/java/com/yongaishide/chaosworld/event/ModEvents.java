package com.yongaishide.chaosworld.event;

import com.yongaishide.chaosworld.datagen.ModDataComponents;
import com.yongaishide.chaosworld.item.custom.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents; // Novo Import
import net.minecraft.core.registries.Registries; // Novo Import
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantments; // Novo Import
import net.minecraft.world.item.enchantment.ItemEnchantments; // Novo Import
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = "chaosworld_core")
public class ModEvents {

    // --- PICKAXE AND HAMMER LOGIC (Block Break & Auto-Smelt) ---
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.level().isClientSide) return;
        ItemStack stack = player.getMainHandItem();

        // Aplica tanto para Pickaxe quanto para Hammer
        if (stack.getItem() instanceof UfoEnergyPickaxeItem || stack.getItem() instanceof HammerItem) {

            // L贸gica de Fortuna Progressiva (Apenas Pickaxe)
            if (stack.getItem() instanceof UfoEnergyPickaxeItem) {
                int currentFortune = stack.getOrDefault(ModDataComponents.PROGRESSIVE_FORTUNE.get(), 0);

                // Limite de n铆vel 100 (ou ajuste conforme necess谩rio)
                if (currentFortune < 300) {
                    int newFortune = currentFortune + 1;

                    // 1. Atualiza o componente customizado (para o seu controle interno e HUD)
                    stack.set(ModDataComponents.PROGRESSIVE_FORTUNE.get(), newFortune);

                    // 2. [CORRE脟脙O] Aplica o Encantamento Vanilla REAL no item
                    // Isso faz o jogo entender que o item tem Fortuna X para o c谩lculo de drops
                    var registry = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                    var fortuneEnchant = registry.getOrThrow(Enchantments.FORTUNE);

                    // Pega os encantamentos atuais, cria uma vers茫o mut谩vel, atualiza a Fortuna e salva de volta
                    ItemEnchantments.Mutable enchantmentsMap = new ItemEnchantments.Mutable(stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
                    enchantmentsMap.set(fortuneEnchant, newFortune);

                    stack.set(DataComponents.ENCHANTMENTS, enchantmentsMap.toImmutable());
                }
            }

            // --- AUTO SMELT LOGIC ---
            if (stack.getOrDefault(ModDataComponents.AUTO_SMELT.get(), false)) {
                ServerLevel level = (ServerLevel) player.level();
                BlockPos pos = event.getPos();
                BlockState state = event.getState();

                // Simula os drops que aconteceriam (agora considerando a Fortuna aplicada acima)
                List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), player, stack);
                boolean smelledAny = false;

                // Tenta smeltar os drops
                for (ItemStack drop : drops) {
                    Optional<net.minecraft.world.item.crafting.RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager()
                            .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(drop), level);

                    if (recipe.isPresent()) {
                        ItemStack result = recipe.get().value().getResultItem(level.registryAccess()).copy();
                        result.setCount(drop.getCount()); // Mant茅m a quantidade multiplicada pela fortuna

                        spawnItem(level, pos, result);
                        smelledAny = true;
                    } else {
                        spawnItem(level, pos, drop); // Drop normal se n茫o tiver receita
                    }
                }

                if (smelledAny) {
                    // Consome energia extra pelo processo
                    consumeEnergyDirect(stack, 50);

                    // Impede o drop vanilla (j谩 dropamos manualmente)
                    event.setCanceled(true);

                    // Destr贸i o bloco manualmente (sem drops vanilla e evitando sons duplicados se poss铆vel)
                    level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private static void spawnItem(Level level, BlockPos pos, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        level.addFreshEntity(entity);
    }

    // --- COMBAT LOGIC (Sword and Axe) ---

    @SubscribeEvent
    public static void onPlayerAttack(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack stack = player.getMainHandItem();

            // 1. Sword Logic
            if (stack.getItem() instanceof UfoEnergySwordItem || stack.getItem() instanceof UfoEnergyGreatswordItem) {
                // 额外伤害（击杀数×2）已通过 updateSoulHarvestDamage 加到攻击力属性上，这里不再事件叠加。
                long lastHit = stack.getOrDefault(ModDataComponents.LAST_HIT_TIME.get(), 0L);
                int combo = stack.getOrDefault(ModDataComponents.COMBO_COUNT.get(), 0);
                long time = player.level().getGameTime();

                if (time - lastHit < 60) {
                    combo++;
                    if (combo >= 5) {
                        event.setAmount(event.getAmount() * 1.5f);
                        player.displayClientMessage(Component.translatable("message.ufo.combo").withStyle(ChatFormatting.GOLD), true);
                        combo = 0;
                    }
                } else {
                    combo = 1;
                }
                stack.set(ModDataComponents.COMBO_COUNT.get(), combo);
                stack.set(ModDataComponents.LAST_HIT_TIME.get(), time);

                if (stack.getItem() instanceof UfoEnergySwordItem) {
                    event.getEntity().addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2));
                }
            }

            // 2. Greatsword Logic
            if (stack.getItem() instanceof UfoEnergyGreatswordItem) {
                float maxHp = player.getMaxHealth();
                float currentHp = player.getHealth();
                float percent = currentHp / maxHp;

                float multiplier = 1.0f;
                if (percent <= 0.10f) multiplier = 1.5f;
                else if (percent <= 0.30f) multiplier = 1.2f;

                event.setAmount(event.getAmount() * multiplier);
            }

            // 3. Axe Logic
            if (stack.getItem() instanceof UfoEnergyAxeItem) {
                float targetArmor = event.getEntity().getArmorValue();
                if (targetArmor > 0) {
                    event.setAmount(event.getAmount() + (targetArmor * 0.2f));
                }
            }
        }
    }

    // --- ARMOR LOGIC (Protection) ---
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDefend(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!isFullUfoArmor(player)) return;

            // 1. Anti-Void
            if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD) && event.getAmount() < Float.MAX_VALUE) {
                if (consumeArmorEnergyDirect(player, 50000)) {
                    event.setCanceled(true);
                    teleportToSafety(player);
                    return;
                }
            }

            // 2. Anti-Kill Command / Absolute Damage
            if (isExtremeDamage(event.getSource(), event.getAmount())) {

                if (consumeArmorEnergyDirect(player, 100000)) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
                    player.sendSystemMessage(Component.translatable("message.ufo.anti_death_protocol").withStyle(ChatFormatting.GOLD));
                    return;
                }
            }

            // 3. Teleport on Low Health
            if (player.getHealth() - event.getAmount() <= 4.0f) {
                if (consumeArmorEnergyDirect(player, 10000)) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth() / 2);
                    teleportToSafety(player);
                    player.sendSystemMessage(Component.translatable("message.ufo.emergency_evacuation").withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        // Sword Logic
        if (event.getSource().getEntity() instanceof Player attacker) {
            ItemStack stack = attacker.getMainHandItem();
            if (stack.getItem() instanceof UfoEnergySwordItem || stack.getItem() instanceof UfoEnergyGreatswordItem) {
                if (event.getEntity() instanceof Enemy) {
                    int kills = stack.getOrDefault(ModDataComponents.KILL_COUNT.get(), 0);
                    stack.set(ModDataComponents.KILL_COUNT.get(), kills + 1);
                } else {
                    int kills = stack.getOrDefault(ModDataComponents.KILL_COUNT.get(), 0);
                    int newKills = Math.max(0, kills - 1);
                    stack.set(ModDataComponents.KILL_COUNT.get(), newKills);
                    if (kills > newKills) {
                        attacker.sendSystemMessage(Component.translatable("message.ufo.sword_power_loss").withStyle(ChatFormatting.RED));
                    }
                }
                UfoEnergySwordItem.updateSoulHarvestDamage(stack);
            }
        }

        // Armor Logic
        if (event.getEntity() instanceof ServerPlayer player) {
            if (isFullUfoArmor(player)) {
                if (isExtremeDamage(event.getSource(), Float.MAX_VALUE) || consumeArmorEnergyDirect(player, 200000)) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
                    player.removeAllEffects();
                    player.sendSystemMessage(Component.translatable("message.ufo.lazarus_protocol").withStyle(ChatFormatting.GOLD));
                }
            }
        }
    }

    // --- HELPER METHODS ---

    private static boolean isFullUfoArmor(Player player) {
        for (ItemStack stack : player.getInventory().armor) {
            if (!(stack.getItem() instanceof UfoArmorItem)) return false;
        }
        return true;
    }

    private static boolean isExtremeDamage(DamageSource source, float amount) {
        String messageId = source.getMsgId();
        return source.is(DamageTypes.GENERIC_KILL)
                || amount >= Float.MAX_VALUE
                || (source.is(DamageTypes.FELL_OUT_OF_WORLD) && amount > 10000f)
                || messageId.contains("chaos")
                || messageId.contains("guardian");
    }

    private static boolean consumeArmorEnergyDirect(Player player, int amountNeeded) {
        int amountLeft = amountNeeded;
        int totalAvailable = 0;

        for (ItemStack stack : player.getInventory().armor) {
            if (stack.getItem() instanceof UfoArmorItem) {
                totalAvailable += stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
            }
        }

        if (totalAvailable < amountNeeded) return false;

        for (ItemStack stack : player.getInventory().armor) {
            if (amountLeft <= 0) break;
            if (stack.getItem() instanceof UfoArmorItem) {
                int current = stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
                int toExtract = Math.min(current, amountLeft);

                stack.set(ModDataComponents.ENERGY.get(), current - toExtract);
                amountLeft -= toExtract;
            }
        }
        return true;
    }

    private static void consumeEnergyDirect(ItemStack stack, int amount) {
        int current = stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
        if (current >= amount) {
            stack.set(ModDataComponents.ENERGY.get(), current - amount);
        }
    }

    private static void teleportToSafety(ServerPlayer player) {
        BlockPos respawnPos = player.getRespawnPosition();
        ServerLevel respawnLevel = player.server.getLevel(player.getRespawnDimension());

        if (respawnPos != null && respawnLevel != null) {
            player.teleportTo(respawnLevel, respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), player.getYRot(), player.getXRot());
        } else {
            BlockPos worldSpawn = player.level().getSharedSpawnPos();
            player.teleportTo(worldSpawn.getX(), 300, worldSpawn.getZ());
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 1200));
        }
    }
}
