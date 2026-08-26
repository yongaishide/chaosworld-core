package com.yongaishide.chaosworld.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A reusable 3D pattern matcher for multiblock structures.
 * <p>
 * The pattern is defined as a 3D char array (layer × row × column) together with
 * a legend map that binds each char to a predicate (block or tag check).
 * The controller position within the pattern is marked by a special character
 * (default {@code 'C'}).
 * <p>
 * <b>Usage:</b>
 * <pre>{@code
 * MultiblockPattern pattern = new MultiblockPattern.Builder()
 *     .layer(new String[]{
 *         "SSS",
 *         "SCS",
 *         "SSS"
 *     })
 *     .layer(new String[]{
 *         "SSS",
 *         "S S",
 *         "SSS"
 *     })
 *     .layer(new String[]{
 *         "SSS",
 *         "SSS",
 *         "SSS"
 *     })
 *     .where('S', block -> block instanceof MyCasingBlock)
 *     .where(' ', block -> true) // air / anything
 *     .build();
 * }</pre>
 */
public class MultiblockPattern {

    /** Functional interface used to test whether a block satisfies a pattern slot. */
    @FunctionalInterface
    public interface BlockPredicate {
        boolean test(BlockState state, Level level, BlockPos pos);
    }

    private final char[][][] pattern;      // [layer][row][col]
    private final Map<Character, StructureMatcher> legend;
    private final Map<Character, Component> legendNames;
    private final Map<Character, List<BlockState>> displayCandidates;
    private final Set<Character> shellSymbols;
    private final Character controllerChar;
    private final int controllerLayer;
    private final int controllerRow;
    private final int controllerCol;
    private final Set<StructureMatcher> minGlobalMatchers;
    private final Set<StructureMatcher> minLayerMatchers;
    private final java.util.LinkedHashMap<Block, Integer> requiredBlocks;

    public char[][][] getPattern() { return pattern; }
    public char getControllerChar() { return controllerChar != null ? controllerChar : 'C'; }
    public int getControllerLayer() { return controllerLayer; }
    public int getControllerRow() { return controllerRow; }
    public int getControllerCol() { return controllerCol; }
    public Component getLegendName(char symbol) { return legendNames.getOrDefault(symbol, Component.literal("Unknown Block")); }

    public boolean isShell(char symbol) { return shellSymbols.contains(symbol); }

    private MultiblockPattern(char[][][] pattern, Map<Character, StructureMatcher> legend, Map<Character, Component> legendNames,
                              Map<Character, List<BlockState>> displayCandidates, Set<Character> shellSymbols, char controllerChar,
                              java.util.LinkedHashMap<Block, Integer> requiredBlocks) {
        this.pattern = pattern;
        this.legend = legend;
        this.legendNames = legendNames;
        this.displayCandidates = displayCandidates;
        this.shellSymbols = shellSymbols;
        this.controllerChar = controllerChar;
        this.requiredBlocks = requiredBlocks;

        Set<StructureMatcher> minGlobals = new java.util.HashSet<>();
        Set<StructureMatcher> minLayers = new java.util.HashSet<>();
        for (StructureMatcher matcher : legend.values()) {
            if (matcher.getMinGlobal() > 0) minGlobals.add(matcher);
            if (matcher.getMinLayer() > 0) minLayers.add(matcher);
        }
        this.minGlobalMatchers = java.util.Collections.unmodifiableSet(minGlobals);
        this.minLayerMatchers = java.util.Collections.unmodifiableSet(minLayers);

        // Locate controller position in the pattern
        int cLayer = -1, cRow = -1, cCol = -1;
        for (int y = 0; y < pattern.length; y++) {
            for (int z = 0; z < pattern[y].length; z++) {
                for (int x = 0; x < pattern[y][z].length; x++) {
                    if (pattern[y][z][x] == controllerChar) {
                        cLayer = y;
                        cRow = z;
                        cCol = x;
                    }
                }
            }
        }
        if (cLayer == -1) {
            throw new IllegalArgumentException("Controller char '" + controllerChar + "' not found in pattern!");
        }
        this.controllerLayer = cLayer;
        this.controllerRow = cRow;
        this.controllerCol = cCol;
    }

    /**
     * Validates the multiblock structure by testing the world against the pattern,
     * centered on the given controller world position.
     *
     * @param level         the level to check
     * @param controllerPos the world position of the controller block
     * @return a {@link MatchResult} containing whether the structure matched and which positions are parts
     */
    public MatchResult match(Level level, BlockPos controllerPos, net.minecraft.core.Direction facing) {
        return match(level, controllerPos, facing, false);
    }

    /**
     * Validates the multiblock structure by testing the world against the pattern,
     * centered on the given controller world position.
     *
     * @param level         the level to check
     * @param controllerPos the world position of the controller block
     * @param facing        the controller's facing
     * @param flipped       mirror the pattern left-right relative to the facing
     * @return a {@link MatchResult} containing whether the structure matched and which positions are parts
     */
    public MatchResult match(Level level, BlockPos controllerPos, net.minecraft.core.Direction facing, boolean flipped) {
        List<BlockPos> partPositions = new ArrayList<>();
        PatternError firstError = null;
        List<PatternError> allErrors = new ArrayList<>();
        boolean valid = true;
        boolean hasUnloadedPositions = false;

        Map<StructureMatcher, Integer> globalCounts = new java.util.IdentityHashMap<>();
        Map<StructureMatcher, Integer> layerCounts = new java.util.IdentityHashMap<>();

        for (int y = 0; y < pattern.length; y++) {
            layerCounts.clear();
            if (!this.minLayerMatchers.isEmpty()) {
                for (int z = 0; z < pattern[y].length; z++) {
                    for (int x = 0; x < pattern[y][z].length; x++) {
                        StructureMatcher matcher = legend.get(pattern[y][z][x]);
                        if (matcher != null && matcher.getMinLayer() > 0) {
                            layerCounts.putIfAbsent(matcher, 0);
                        }
                    }
                }
            }
            for (int z = 0; z < pattern[y].length; z++) {
                for (int x = 0; x < pattern[y][z].length; x++) {
                    char c = pattern[y][z][x];

                    // Calculate world offset from controller
                    int offsetX = x - controllerCol;
                    int offsetY = y - controllerLayer;
                    int offsetZ = z - controllerRow;

                    BlockPos worldPos = getRotatedPos(controllerPos, offsetX, offsetY, offsetZ, facing, flipped);

                    // Skip the controller position itself
                    if (worldPos.equals(controllerPos)) {
                        continue;
                    }

                    StructureMatcher matcher = legend.get(c);
                    if (matcher == null) {
                        // Unknown char in pattern ⇒ treat as "anything"
                        continue;
                    }

                    if (!level.isLoaded(worldPos)) {
                        valid = false;
                        hasUnloadedPositions = true;
                        PatternError err = new PatternError(worldPos, Component.literal("Chunk not loaded"));
                        allErrors.add(err);
                        if (firstError == null) firstError = err;
                        continue;
                    }

                    BlockState state = level.getBlockState(worldPos);
                    if (matcher.test(state, level, worldPos)) {
                        if (matcher.isAny()) {
                            // "Any" positions are part of the bounding volume but not
                            // structure parts: they must not count toward limits and
                            // must not be treated as parts (e.g. by dismantling).
                            continue;
                        }
                        int layerCount = layerCounts.getOrDefault(matcher, 0);
                        int globalCount = globalCounts.getOrDefault(matcher, 0);
                        if (layerCount >= matcher.getMaxLayer() || globalCount >= matcher.getMaxGlobal()) {
                            valid = false;
                            Component expected = matcherName(matcher);
                            PatternError err = new PatternError(worldPos,
                                    expected.copy().append(Component.literal(" (" +
                                            Component.translatable("message.ufo.multiblock.limit_exceeded").getString() + ")")));
                            allErrors.add(err);
                            if (firstError == null) firstError = err;
                        } else {
                            layerCounts.put(matcher, layerCount + 1);
                            globalCounts.put(matcher, globalCount + 1);
                            partPositions.add(worldPos);
                        }
                    } else {
                        valid = false;
                        Component expected = legendNames.getOrDefault(c, Component.literal("Expected part"));
                        PatternError err = new PatternError(worldPos, expected);
                        allErrors.add(err);
                        if (firstError == null) firstError = err;
                    }
                }
            }
            for (StructureMatcher matcher : this.minLayerMatchers) {
                if (layerCounts.getOrDefault(matcher, 0) < matcher.getMinLayer()) {
                    valid = false;
                    if (firstError == null) {
                        firstError = new PatternError(controllerPos,
                                matcherName(matcher).copy().append(Component.literal(" (" +
                                        Component.translatable("message.ufo.multiblock.min_layer",
                                                matcher.getMinLayer()).getString() + ")")));
                    }
                }
            }
        }
        for (StructureMatcher matcher : this.minGlobalMatchers) {
            if (globalCounts.getOrDefault(matcher, 0) < matcher.getMinGlobal()) {
                valid = false;
                if (firstError == null) {
                    firstError = new PatternError(controllerPos,
                            matcherName(matcher).copy().append(Component.literal(" (" +
                                    Component.translatable("message.ufo.multiblock.min_global",
                                            matcher.getMinGlobal()).getString() + ")")));
                }
            }
        }

        return new MatchResult(
                valid,
                valid ? Collections.unmodifiableList(partPositions) : Collections.emptyList(),
                Optional.ofNullable(firstError),
                Collections.unmodifiableList(allErrors),
                hasUnloadedPositions,
                Collections.unmodifiableMap(new java.util.HashMap<>(globalCounts)));
    }

    private static Component matcherName(StructureMatcher matcher) {
        return matcher.getName() != null ? matcher.getName() : Component.literal("Structure requirement");
    }

    /**
     * Translates local pattern offsets into world coordinates based on the controller's facing direction.
     * Assumes pattern is built such that z=0 is the front face looking SOUTH (+Z). 
     * If facing is NORTH, the machine goes $+Z$ backwards.
     */
    private BlockPos getRotatedPos(BlockPos center, int localX, int localY, int localZ, net.minecraft.core.Direction facing, boolean flipped) {
        if (flipped) {
            localX = -localX;
        }
        switch (facing) {
            case SOUTH:
                return center.offset(-localX, localY, -localZ);
            case WEST:
                return center.offset(localZ, localY, -localX);
            case EAST:
                return center.offset(-localZ, localY, localX);
            case NORTH:
            default:
                return center.offset(localX, localY, localZ);
        }
    }

    /**
     * Instantly assembles the structure unconditionally, replacing non-matching blocks 
     * using the provided map of default states. Does not replace the controller.
     */
    public void assembleAsCreative(Level level, BlockPos controllerPos, net.minecraft.core.Direction facing, Map<Character, BlockState> defaultStates) {
        placeRequiredBlocks(level, controllerPos, facing, false, null, true);
        for (int y = 0; y < pattern.length; y++) {
            for (int z = 0; z < pattern[y].length; z++) {
                for (int x = 0; x < pattern[y][z].length; x++) {
                    char c = pattern[y][z][x];
                    
                    int offsetX = x - controllerCol;
                    int offsetY = y - controllerLayer;
                    int offsetZ = z - controllerRow;

                    BlockPos worldPos = getRotatedPos(controllerPos, offsetX, offsetY, offsetZ, facing, false);

                    if (worldPos.equals(controllerPos)) continue;

                    if (!level.isInWorldBounds(worldPos)) continue;
                    if (!level.hasChunkAt(worldPos)) continue;

                    StructureMatcher predicate = legend.get(c);
                    BlockState targetState = defaultStates.get(c);

                    if (predicate != null && targetState != null) {
                        BlockState currentState = level.getBlockState(worldPos);
                        if (!isRequiredBlock(currentState) && !predicate.test(currentState, level, worldPos)) {
                            level.setBlock(worldPos, targetState, Block.UPDATE_CLIENTS);
                        }
                    }
                }
            }
        }
    }

    /**
     * Assembles the structure by consuming blocks from a provider (e.g. a player
     * inventory). The provider picks the best available candidate block for each
     * position; already-valid lower-tier blocks are upgraded to higher tiers when
     * possible and the replaced block is handed back through {@code returnBlock}.
     */
    public AssembleResult assembleWithProvider(Level level, BlockPos controllerPos, net.minecraft.core.Direction facing,
                                               Map<Character, BlockState> defaultStates,
                                               Function<List<Block>, Block> consumeBest,
                                               Consumer<Block> returnBlock) {
        return assembleWithProvider(level, controllerPos, facing, defaultStates, consumeBest, returnBlock, false, true);
    }

    /**
     * Assembles the structure by consuming blocks from a provider (e.g. a player
     * inventory). The provider picks the best available candidate block for each
     * position; already-valid lower-tier blocks are upgraded to higher tiers when
     * possible and the replaced block is handed back through {@code returnBlock}.
     *
     * @param flipped        mirror the pattern left-right relative to the facing
     * @param replaceInvalid when false, only place into air (occupied wrong blocks are left alone)
     */
    public AssembleResult assembleWithProvider(Level level, BlockPos controllerPos, net.minecraft.core.Direction facing,
                                               Map<Character, BlockState> defaultStates,
                                               Function<List<Block>, Block> consumeBest,
                                               Consumer<Block> returnBlock,
                                               boolean flipped, boolean replaceInvalid) {
        int placed = 0;
        int missing = 0;

        // Special placement pass: required blocks (e.g. hatches) are placed first,
        // at any position whose matcher accepts them.
        int[] requiredResult = placeRequiredBlocks(level, controllerPos, facing, flipped, consumeBest, replaceInvalid);
        placed += requiredResult[0];
        missing += requiredResult[1];

        for (int y = 0; y < pattern.length; y++) {
            for (int z = 0; z < pattern[y].length; z++) {
                for (int x = 0; x < pattern[y][z].length; x++) {
                    char c = pattern[y][z][x];

                    int offsetX = x - controllerCol;
                    int offsetY = y - controllerLayer;
                    int offsetZ = z - controllerRow;

                    BlockPos worldPos = getRotatedPos(controllerPos, offsetX, offsetY, offsetZ, facing, flipped);

                    if (worldPos.equals(controllerPos)) continue;
                    if (!level.isInWorldBounds(worldPos)) continue;
                    if (!level.hasChunkAt(worldPos)) continue;

                    StructureMatcher predicate = legend.get(c);
                    BlockState targetState = defaultStates.get(c);

                    if (predicate != null && targetState != null) {
                        List<Block> candidates = candidateBlocks(c, targetState.getBlock());
                        BlockState currentState = level.getBlockState(worldPos);
                        boolean currentValid = predicate.test(currentState, level, worldPos);

                        if (isRequiredBlock(currentState)) {
                            // Required blocks (e.g. hatches) are final; never upgrade them away.
                            continue;
                        }
                        Block defaultBlock = targetState.getBlock();
                        if (currentValid) {
                            // Upgrade only towards the pattern's default block: a lower-tier
                            // candidate (e.g. MK1 field generator) may be upgraded to the default.
                            // Blocks that are not on the path to the default (e.g. hatches at
                            // casing slots) are left alone.
                            int currentIndex = candidates.indexOf(currentState.getBlock());
                            int defaultIndex = candidates.indexOf(defaultBlock);
                            if (currentIndex >= 0 && defaultIndex > currentIndex) {
                                Block best = consumeBest.apply(candidates.subList(currentIndex + 1, candidates.size()));
                                if (best != null) {
                                    level.setBlock(worldPos, best.defaultBlockState(), Block.UPDATE_CLIENTS);
                                    placed++;
                                    returnBlock.accept(currentState.getBlock());
                                }
                            }
                        } else {
                            if (replaceInvalid || currentState.isAir()) {
                                Block best = consumeBest.apply(java.util.List.of(defaultBlock));
                                if (best != null) {
                                    level.setBlock(worldPos, best.defaultBlockState(), Block.UPDATE_CLIENTS);
                                    placed++;
                                } else {
                                    missing++;
                                }
                            } else {
                                missing++;
                            }
                        }
                    }
                }
            }
        }
        return new AssembleResult(placed, missing);
    }

    /**
     * Places the required blocks (e.g. hatches) first, at positions whose matcher
     * accepts them. Returns {@code [placed, missing]}.
     *
     * @param consumeBest    source for blocks; when {@code null} (creative), blocks are placed freely
     * @param replaceInvalid when false, only place into air
     */
    private int[] placeRequiredBlocks(Level level, BlockPos controllerPos, net.minecraft.core.Direction facing,
                                      boolean flipped, @org.jetbrains.annotations.Nullable Function<List<Block>, Block> consumeBest,
                                      boolean replaceInvalid) {
        int placed = 0;
        int missing = 0;
        for (java.util.Map.Entry<Block, Integer> entry : this.requiredBlocks.entrySet()) {
            Block required = entry.getKey();
            int needed = entry.getValue();
            if (needed <= 0) {
                continue;
            }
            List<BlockPos> spots = new ArrayList<>();
            for (int y = 0; y < pattern.length; y++) {
                for (int z = 0; z < pattern[y].length; z++) {
                    for (int x = 0; x < pattern[y][z].length; x++) {
                        StructureMatcher matcher = legend.get(pattern[y][z][x]);
                        if (matcher == null) {
                            continue;
                        }
                        int offsetX = x - controllerCol;
                        int offsetY = y - controllerLayer;
                        int offsetZ = z - controllerRow;
                        BlockPos worldPos = getRotatedPos(controllerPos, offsetX, offsetY, offsetZ, facing, flipped);
                        if (worldPos.equals(controllerPos)) {
                            continue;
                        }
                        if (!level.isInWorldBounds(worldPos) || !level.hasChunkAt(worldPos)) {
                            continue;
                        }
                        if (!matcher.test(required.defaultBlockState(), level, worldPos)) {
                            continue;
                        }
                        BlockState currentState = level.getBlockState(worldPos);
                        if (currentState.is(required)) {
                            needed--;
                        } else if (currentState.isAir() || replaceInvalid) {
                            spots.add(worldPos);
                        }
                    }
                }
            }
            while (needed > 0 && !spots.isEmpty()) {
                Block best = consumeBest != null ? consumeBest.apply(java.util.List.of(required)) : required;
                if (best == null) {
                    break;
                }
                BlockPos spot = spots.remove(spots.size() - 1);
                level.setBlock(spot, best.defaultBlockState(), Block.UPDATE_CLIENTS);
                placed++;
                needed--;
            }
            if (needed > 0) {
                missing += needed;
            }
        }
        return new int[]{placed, missing};
    }

    private boolean isRequiredBlock(BlockState state) {
        return this.requiredBlocks.containsKey(state.getBlock());
    }

    private List<Block> candidateBlocks(char c, Block defaultBlock) {
        List<Block> blocks = new ArrayList<>();
        for (BlockState candidate : displayCandidates.getOrDefault(c, List.of())) {
            if (candidate != null && !blocks.contains(candidate.getBlock())) {
                blocks.add(candidate.getBlock());
            }
        }
        if (defaultBlock != null && !blocks.contains(defaultBlock)) {
            blocks.add(defaultBlock);
        }
        return blocks;
    }

    public record AssembleResult(int placed, int missing) {
    }

    /**
     * Returns the exact world positions for a specific character in the pattern.
     */
    public List<BlockPos> getExpectedPositions(BlockPos controllerPos, net.minecraft.core.Direction facing, char targetChar) {
        return getExpectedPositions(controllerPos, facing, targetChar, false);
    }

    /**
     * Returns the exact world positions for a specific character in the pattern.
     *
     * @param flipped mirror the pattern left-right relative to the facing
     */
    public List<BlockPos> getExpectedPositions(BlockPos controllerPos, net.minecraft.core.Direction facing,
                                               char targetChar, boolean flipped) {
        List<BlockPos> list = new ArrayList<>();
        for (int y = 0; y < pattern.length; y++) {
            for (int z = 0; z < pattern[y].length; z++) {
                for (int x = 0; x < pattern[y][z].length; x++) {
                    if (pattern[y][z][x] == targetChar) {
                        int offsetX = x - controllerCol;
                        int offsetY = y - controllerLayer;
                        int offsetZ = z - controllerRow;
                        BlockPos pos = getRotatedPos(controllerPos, offsetX, offsetY, offsetZ, facing, flipped);
                        list.add(pos);
                    }
                }
            }
        }
        return list;
    }

    public List<BlockState> getDisplayCandidates(char symbol) {
        return displayCandidates.getOrDefault(symbol, List.of());
    }

    public Optional<Character> getSymbolAt(BlockPos controllerPos, net.minecraft.core.Direction facing, BlockPos worldPos) {
        for (int y = 0; y < pattern.length; y++) {
            for (int z = 0; z < pattern[y].length; z++) {
                for (int x = 0; x < pattern[y][z].length; x++) {
                    int offsetX = x - controllerCol;
                    int offsetY = y - controllerLayer;
                    int offsetZ = z - controllerRow;
                    BlockPos expectedPos = getRotatedPos(controllerPos, offsetX, offsetY, offsetZ, facing, false);
                    if (expectedPos.equals(worldPos)) {
                        return Optional.of(pattern[y][z][x]);
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Represents a specific error in pattern matching.
     */
    public record PatternError(BlockPos pos, Component expected) {}

    /**
     * Result of a pattern match attempt.
     */
    public record MatchResult(
            boolean isValid,
            List<BlockPos> partPositions,
            Optional<PatternError> error,
            List<PatternError> allErrors,
            boolean hasUnloadedPositions,
            Map<StructureMatcher, Integer> matchedCounts) {}

    // ──────────────────────── Builder ────────────────────────

    public static class Builder {
        private final List<String[]> layers = new ArrayList<>();
        private final Map<Character, StructureMatcher> legend = new HashMap<>();
        private final Map<Character, Component> legendNames = new HashMap<>();
        private final Map<Character, List<BlockState>> displayCandidates = new HashMap<>();
        private final Set<Character> shellSymbols = new HashSet<>();
        private final java.util.LinkedHashMap<Block, Integer> requiredBlocks = new java.util.LinkedHashMap<>();
        private char controllerChar = 'C';

        /**
         * Adds a horizontal layer to the pattern (bottom to top).
         * Each string represents a row (north to south); each char a column (west to east).
         */
        public Builder layer(String[] rows) {
            this.layers.add(rows);
            return this;
        }

        /**
         * Defines what blocks a character in the pattern maps to, with optional
         * counting limits (global / per-layer) on the given matcher.
         */
        public Builder where(char c, StructureMatcher matcher) {
            this.legend.put(c, matcher);
            this.legendNames.put(c, matcher.getName() != null ? matcher.getName() : Component.literal("Unknown Block"));
            return this;
        }

        /**
         * Maps a character to "any block". These positions are always valid but are
         * never considered structure parts (useful for the empty volume around a
         * spherical or rounded structure).
         */
        public Builder whereAny(char c) {
            return where(c, StructureMatcher.any());
        }

        /**
         * Defines what block a character in the pattern maps to.
         */
        public Builder where(char c, BlockPredicate predicate) {
            return where(c, StructureMatcher.create().custom(predicate::test));
        }

        public Builder where(char c, BlockPredicate predicate, Component expectedName) {
            return where(c, StructureMatcher.create().custom(predicate::test).named(expectedName));
        }

        /**
         * Convenience: maps a char to a specific block class.
         */
        public Builder where(char c, Block block) {
            return where(c, StructureMatcher.create().custom((state, level, pos) -> state.is(block)).named(block.getName()));
        }

        public Builder candidates(char c, BlockState... states) {
            return candidates(c, Arrays.asList(states));
        }

        public Builder candidates(char c, List<BlockState> states) {
            List<BlockState> cleaned = states.stream()
                    .filter(Objects::nonNull)
                    .toList();
            if (!cleaned.isEmpty()) {
                this.displayCandidates.put(c, cleaned);
            }
            return this;
        }

        /**
         * Sets the character that represents the controller in the pattern.
         * Defaults to {@code 'C'}.
         */
        public Builder controllerChar(char c) {
            this.controllerChar = c;
            return this;
        }

        /**
         * Marks symbols as "shell" blocks (casing/frame/glass) that can be hidden
         * in previews to expose the internal functional components.
         */
        public Builder shell(char... symbols) {
            for (char symbol : symbols) {
                this.shellSymbols.add(symbol);
            }
            return this;
        }

        /**
         * Declares a block that auto-build must place a fixed number of times
         * (e.g. hatches), before filling the rest of the structure.
         */
        public Builder requireBlock(Block block, int count) {
            this.requiredBlocks.put(block, Math.max(0, count));
            return this;
        }

        public MultiblockPattern build() {
            // Convert List<String[]> → char[][][]
            char[][][] patternArray = new char[layers.size()][][];
            for (int y = 0; y < layers.size(); y++) {
                String[] rows = layers.get(y);
                patternArray[y] = new char[rows.length][];
                for (int z = 0; z < rows.length; z++) {
                    patternArray[y][z] = rows[z].toCharArray();
                }
            }
            return new MultiblockPattern(patternArray, legend, legendNames, new HashMap<>(displayCandidates), new HashSet<>(shellSymbols), controllerChar, new java.util.LinkedHashMap<>(requiredBlocks));
        }
    }
}
