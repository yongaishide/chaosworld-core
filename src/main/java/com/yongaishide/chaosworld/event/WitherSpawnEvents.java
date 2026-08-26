package com.yongaishide.chaosworld.event;

import com.yongaishide.chaosworld.ChaosWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Custom wither summoning: right-click any block of the custom structure with a
 * Cryptid Core to spawn the wither.
 * <p>
 * Structure (side view):
 * <pre>
 *  头 头 头    <- 3 wither skeleton skulls
 *  沙 烈焰立方 沙 <- middle of the row is avaritia:blaze_cube_block
 *     沙       <- soul sand / soul soil (bottom center)
 * </pre>
 * The vanilla auto-spawn on skull placement is disabled (see WitherSkullBlockMixin).
 */
@EventBusSubscriber(modid = "chaosworld_core")
public class WitherSpawnEvents {

    private static final ResourceLocation BLAZE_CUBE_ID = ResourceLocation.parse("avaritia:blaze_cube_block");
    private static Block blazeCubeCache;

    @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST)
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        // Enforce the buffed health on every wither entering the world.
        // Runs last so other mods' health scaling cannot override it.
        if (event.getEntity() instanceof WitherBoss wither) {
            wither.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(4000.0);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (!stack.is(ChaosWorld.CRYPTID_CORE.get())) {
            return;
        }

        Level level = event.getLevel();
        if (level.isClientSide) {
            // Do NOT cancel on the client: canceling here prevents the right-click
            // packet from reaching the server, so the summon would never happen.
            return;
        }
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }

        BlockPos cube = findBlazeCube(level, event.getPos());
        if (cube == null) {
            player.displayClientMessage(net.minecraft.network.chat.Component
                    .translatable("message.ufo.wither.structure_incomplete"), true);
            return;
        }

        // Consume the Cryptid Core (not in creative)
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        // Remove the summoning structure
        clearStructure(level, cube);

        // Spawn the wither above the middle skull
        WitherBoss wither = net.minecraft.world.entity.EntityType.WITHER.create(level);
        if (wither != null) {
            BlockPos skullPos = cube.above();
            wither.moveTo(skullPos.getX() + 0.5, skullPos.getY() + 0.55, skullPos.getZ() + 0.5, 0.0F, 0.0F);
            wither.yBodyRot = 0.0F;
            wither.yHeadRot = 0.0F;
            wither.makeInvulnerable();
            level.addFreshEntity(wither);
            player.displayClientMessage(net.minecraft.network.chat.Component
                    .translatable("message.ufo.wither.summoned"), true);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    /**
     * Searches for the blaze cube block (bottom center of the structure) near the
     * clicked position and validates the full structure around it.
     */
    private static BlockPos findBlazeCube(Level level, BlockPos clicked) {
        for (int dy = -2; dy <= 1; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos pos = clicked.offset(dx, dy, dz);
                    if (isBlazeCube(level.getBlockState(pos))
                            && (isWitherStructureAt(level, pos, Direction.EAST)
                            || isWitherStructureAt(level, pos, Direction.NORTH))) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Validates the structure anchored at the blaze cube block (middle of the row),
     * in the given orientation.
     */
    private static boolean isWitherStructureAt(Level level, BlockPos cube, Direction axis) {
        BlockPos left = cube.relative(axis.getCounterClockWise());
        BlockPos right = cube.relative(axis.getClockWise());
        BlockPos bottom = cube.below();
        if (!isSoulBase(level.getBlockState(left))
                || !isSoulBase(level.getBlockState(right))
                || !isSoulBase(level.getBlockState(bottom))) {
            return false;
        }
        BlockPos skullLeft = left.above();
        BlockPos skullRight = right.above();
        BlockPos skullTop = cube.above();
        return isWitherSkull(level, skullLeft)
                && isWitherSkull(level, skullRight)
                && isWitherSkull(level, skullTop);
    }

    private static void clearStructure(Level level, BlockPos cube) {
        level.setBlock(cube, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(cube.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(cube.below(), Blocks.AIR.defaultBlockState(), 3);
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            level.setBlock(cube.relative(dir), Blocks.AIR.defaultBlockState(), 3);
            level.setBlock(cube.relative(dir).above(), Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static boolean isSoulBase(BlockState state) {
        return state.is(BlockTags.WITHER_SUMMON_BASE_BLOCKS);
    }

    private static boolean isWitherSkull(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(Blocks.WITHER_SKELETON_SKULL) || state.is(Blocks.WITHER_SKELETON_WALL_SKULL);
    }

    private static boolean isBlazeCube(BlockState state) {
        if (blazeCubeCache == null) {
            blazeCubeCache = BuiltInRegistries.BLOCK.get(BLAZE_CUBE_ID);
        }
        return blazeCubeCache != null && state.is(blazeCubeCache);
    }
}
