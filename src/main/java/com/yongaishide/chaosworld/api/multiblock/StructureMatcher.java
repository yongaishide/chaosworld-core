package com.yongaishide.chaosworld.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A per-position block matcher for multiblock structures, inspired by GTCEu's
 * TraceabilityPredicate but fully self-contained.
 * <p>
 * A matcher accepts a position when ANY of its checks passes. It can also carry
 * counting limits (global and per-layer) that are enforced during a structure scan:
 * <ul>
 *     <li>{@link #withMinGlobalLimited(int)} / {@link #withMaxGlobalLimited(int)}</li>
 *     <li>{@link #withMinLayerLimited(int)} / {@link #withMaxLayerLimited(int)}</li>
 * </ul>
 */
public class StructureMatcher {

    public static final int UNLIMITED = Integer.MAX_VALUE;

    @FunctionalInterface
    public interface BlockCheck {
        boolean test(BlockState state, Level level, BlockPos pos);
    }

    private final List<BlockCheck> checks = new ArrayList<>();
    private Component name;
    private int minGlobal = 0;
    private int maxGlobal = UNLIMITED;
    private int minLayer = 0;
    private int maxLayer = UNLIMITED;
    private boolean any;

    private StructureMatcher() {
    }

    public static StructureMatcher create() {
        return new StructureMatcher();
    }

    public static StructureMatcher any() {
        StructureMatcher matcher = new StructureMatcher();
        matcher.any = true;
        matcher.checks.add((state, level, pos) -> true);
        return matcher;
    }

    public static StructureMatcher air() {
        return create().custom((state, level, pos) -> state.isAir());
    }

    public static StructureMatcher block(Block block) {
        return create().custom((state, level, pos) -> state.is(block));
    }

    public static StructureMatcher blocks(Block... blocks) {
        return create().custom((state, level, pos) -> {
            for (Block block : blocks) {
                if (state.is(block)) {
                    return true;
                }
            }
            return false;
        });
    }

    public static StructureMatcher tag(TagKey<Block> tag) {
        return create().custom((state, level, pos) -> state.is(tag));
    }

    public StructureMatcher custom(BlockCheck check) {
        this.checks.add(check);
        return this;
    }

    public StructureMatcher named(Component name) {
        this.name = name;
        return this;
    }

    public StructureMatcher withMinGlobalLimited(int n) {
        this.minGlobal = Math.max(0, n);
        return this;
    }

    public StructureMatcher withMaxGlobalLimited(int n) {
        this.maxGlobal = Math.max(0, n);
        return this;
    }

    public StructureMatcher withMinLayerLimited(int n) {
        this.minLayer = Math.max(0, n);
        return this;
    }

    public StructureMatcher withMaxLayerLimited(int n) {
        this.maxLayer = Math.max(0, n);
        return this;
    }

    public boolean test(BlockState state, Level level, BlockPos pos) {
        for (BlockCheck check : this.checks) {
            if (check.test(state, level, pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether this matcher accepts anything and its positions are NOT considered
     * structure parts (e.g. the empty volume around a spherical structure).
     */
    public boolean isAny() {
        return this.any;
    }

    public Component getName() {
        return this.name;
    }

    public int getMinGlobal() {
        return this.minGlobal;
    }

    public int getMaxGlobal() {
        return this.maxGlobal;
    }

    public int getMinLayer() {
        return this.minLayer;
    }

    public int getMaxLayer() {
        return this.maxLayer;
    }
}
