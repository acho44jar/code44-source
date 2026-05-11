package com.igore.code44.world;

import com.igore.code44.data.Code44WorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public final class LegacyBiomeManager {
    private static final int REGION_SIZE_CHUNKS = 6;
    private static final int REGION_CHANCE = 13;
    private static final int NIGHTMARE_REGION_CHANCE = 97;
    private static final int MIN_SURFACE_Y = 58;
    private static final int MAX_SURFACE_Y = 120;
    private static final int TICK_INTERVAL = 20;
    private static final int[][] CHUNK_SCAN_PATTERN = new int[][]{
            {0, 0}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}
    };

    private LegacyBiomeManager() {
    }

    public static void tickAroundPlayer(ServerLevel level, ServerPlayer player) {
        if (level.dimension() != Level.OVERWORLD || !HelpWorldManager.isHelpWorld(level)) {
            return;
        }

        if (player.tickCount % TICK_INTERVAL != 0) {
            return;
        }

        ChunkPos center = player.chunkPosition();
        int patternIndex = (player.tickCount / TICK_INTERVAL) % CHUNK_SCAN_PATTERN.length;
        int[] offset = CHUNK_SCAN_PATTERN[patternIndex];
        ChunkPos chunkPos = new ChunkPos(center.x + offset[0], center.z + offset[1]);
        if (!level.hasChunk(chunkPos.x, chunkPos.z)) {
            return;
        }

        Variant variant = resolveVariant(level, chunkPos);
        if (variant == Variant.NONE) {
            return;
        }

        Code44WorldData data = Code44WorldData.get(level);
        long chunkKey = chunkPos.toLong();
        if (data.isLegacyBiomeChunkProcessed(chunkKey)) {
            return;
        }

        applyVariant(level, chunkPos, variant);
        data.markLegacyBiomeChunkProcessed(chunkKey);
    }

    public static boolean forceAroundPlayer(ServerLevel level, ServerPlayer player) {
        if (level.dimension() != Level.OVERWORLD) {
            return false;
        }

        ChunkPos center = player.chunkPosition();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                ChunkPos chunkPos = new ChunkPos(center.x + dx, center.z + dz);
                Variant forced = ((dx + dz) & 1) == 0 ? Variant.ALPHA_FOREST : Variant.BROKEN_WASTES;
                if (dx == 0 && dz == 0) {
                    forced = Variant.HAUNTED_OLD_GROWTH;
                }
                applyVariant(level, chunkPos, forced);
                Code44WorldData.get(level).markLegacyBiomeChunkProcessed(chunkPos.toLong());
            }
        }
        return true;
    }

    private static Variant resolveVariant(ServerLevel level, ChunkPos chunkPos) {
        int regionX = Math.floorDiv(chunkPos.x, REGION_SIZE_CHUNKS);
        int regionZ = Math.floorDiv(chunkPos.z, REGION_SIZE_CHUNKS);
        long hash = mix(level.getSeed(), regionX, regionZ);
        if (Math.floorMod(hash, NIGHTMARE_REGION_CHANCE) == 0L) {
            return Variant.HAUNTED_OLD_GROWTH;
        }
        if (Math.floorMod(hash, REGION_CHANCE) != 0L) {
            return Variant.NONE;
        }
        int selector = (int) Math.floorMod(hash >>> 3, 3);
        return switch (selector) {
            case 0 -> Variant.LEGACY_MEADOW;
            case 1 -> Variant.BROKEN_WASTES;
            default -> Variant.ALPHA_FOREST;
        };
    }

    private static void applyVariant(ServerLevel level, ChunkPos chunkPos, Variant variant) {
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int maxX = chunkPos.getMaxBlockX();
        int maxZ = chunkPos.getMaxBlockZ();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
                if (!isSurfaceValid(level, surface)) {
                    continue;
                }

                switch (variant) {
                    case LEGACY_MEADOW -> mutateLegacyMeadow(level, surface, x, z);
                    case BROKEN_WASTES -> mutateBrokenWastes(level, surface, x, z);
                    case ALPHA_FOREST -> mutateAlphaForest(level, surface, x, z);
                    case HAUNTED_OLD_GROWTH -> mutateHauntedOldGrowth(level, surface, x, z);
                    default -> {
                    }
                }
            }
        }

        placeChunkFeatures(level, chunkPos, variant);
    }

    private static boolean isSurfaceValid(ServerLevel level, BlockPos surface) {
        return surface.getY() >= MIN_SURFACE_Y
                && surface.getY() <= MAX_SURFACE_Y
                && level.getFluidState(surface).isEmpty()
                && level.getFluidState(surface.above()).isEmpty();
    }

    private static void mutateLegacyMeadow(ServerLevel level, BlockPos surface, int x, int z) {
        int pattern = Math.floorMod(x * 31 + z * 17, 11);
        if (pattern <= 3) {
            level.setBlock(surface, Blocks.COARSE_DIRT.defaultBlockState(), 3);
        } else if (pattern == 4) {
            level.setBlock(surface, Blocks.MOSS_BLOCK.defaultBlockState(), 3);
        } else {
            level.setBlock(surface, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        }

        BlockPos above = surface.above();
        if (!level.getBlockState(above).isAir()) {
            return;
        }

        int deco = Math.floorMod(x * 13 + z * 29, 23);
        if (deco == 0) {
            level.setBlock(above, Blocks.GRASS.defaultBlockState(), 3);
        } else if (deco == 1) {
            level.setBlock(above, Blocks.DANDELION.defaultBlockState(), 3);
        } else if (deco == 2) {
            level.setBlock(above, Blocks.POPPY.defaultBlockState(), 3);
        } else if (deco == 3) {
            level.setBlock(above, Blocks.BROWN_MUSHROOM.defaultBlockState(), 3);
        }
    }

    private static void mutateBrokenWastes(ServerLevel level, BlockPos surface, int x, int z) {
        int pattern = Math.floorMod(x * 19 + z * 37, 16);
        if (pattern <= 4) {
            level.setBlock(surface, Blocks.STONE.defaultBlockState(), 3);
        } else if (pattern <= 8) {
            level.setBlock(surface, Blocks.GRAVEL.defaultBlockState(), 3);
        } else if (pattern <= 10) {
            level.setBlock(surface, Blocks.COARSE_DIRT.defaultBlockState(), 3);
        } else {
            level.setBlock(surface, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        }

        BlockPos above = surface.above();
        if (pattern == 0 && level.getBlockState(above).isAir()) {
            level.setBlock(above, Blocks.DEAD_BUSH.defaultBlockState(), 3);
        }

        if (Math.floorMod(x * 7 + z * 11, 41) == 0) {
            carveBrokenPit(level, surface);
        }
    }

    private static void carveBrokenPit(ServerLevel level, BlockPos surface) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos top = surface.offset(dx, 0, dz);
                level.setBlock(top, Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(top.below(), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(top.below(2), Blocks.STONE.defaultBlockState(), 3);
            }
        }
    }

    private static void placeChunkFeatures(ServerLevel level, ChunkPos chunkPos, Variant variant) {
        int featureSeed = Mth.floor((float) Math.floorMod(mix(level.getSeed(), chunkPos.x, chunkPos.z), Integer.MAX_VALUE));
        int localX = 2 + Math.floorMod(featureSeed, 12);
        int localZ = 2 + Math.floorMod(featureSeed / 13, 12);
        BlockPos surface = level.getHeightmapPos(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                new BlockPos(chunkPos.getMinBlockX() + localX, 0, chunkPos.getMinBlockZ() + localZ)
        );
        if (!isSurfaceValid(level, surface)) {
            return;
        }

        switch (variant) {
            case LEGACY_MEADOW -> {
                if ((featureSeed & 1) == 0) {
                    buildOldOak(level, surface.above());
                } else {
                    buildCobbleRelic(level, surface.above());
                }
            }
            case BROKEN_WASTES -> {
                if ((featureSeed & 1) == 0) {
                    buildDeadStump(level, surface.above());
                } else {
                    buildFloatingDirtShard(level, surface.above());
                }
            }
            case ALPHA_FOREST -> {
                if ((featureSeed & 1) == 0) {
                    buildAlphaPine(level, surface.above());
                } else {
                    buildOldOak(level, surface.above());
                }
            }
            case HAUNTED_OLD_GROWTH -> {
                if ((featureSeed & 1) == 0) {
                    buildHauntedTree(level, surface.above());
                } else {
                    buildBrokenMonolith(level, surface.above());
                }
            }
            default -> {
            }
        }
    }

    private static void mutateAlphaForest(ServerLevel level, BlockPos surface, int x, int z) {
        int pattern = Math.floorMod(x * 23 + z * 11, 14);
        if (pattern <= 2) {
            level.setBlock(surface, Blocks.PODZOL.defaultBlockState(), 3);
        } else if (pattern <= 5) {
            level.setBlock(surface, Blocks.COARSE_DIRT.defaultBlockState(), 3);
        } else {
            level.setBlock(surface, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        }

        BlockPos above = surface.above();
        if (!level.getBlockState(above).isAir()) {
            return;
        }

        if (pattern == 6) {
            level.setBlock(above, Blocks.FERN.defaultBlockState(), 3);
        } else if (pattern == 7) {
            level.setBlock(above, Blocks.GRASS.defaultBlockState(), 3);
        } else if (pattern == 8) {
            level.setBlock(above, Blocks.BROWN_MUSHROOM.defaultBlockState(), 3);
        }
    }

    private static void mutateHauntedOldGrowth(ServerLevel level, BlockPos surface, int x, int z) {
        int pattern = Math.floorMod(x * 41 + z * 17, 19);
        if (pattern <= 4) {
            level.setBlock(surface, Blocks.STONE.defaultBlockState(), 3);
        } else if (pattern <= 8) {
            level.setBlock(surface, Blocks.PODZOL.defaultBlockState(), 3);
        } else if (pattern <= 11) {
            level.setBlock(surface, Blocks.COARSE_DIRT.defaultBlockState(), 3);
        } else {
            level.setBlock(surface, Blocks.MOSS_BLOCK.defaultBlockState(), 3);
        }

        BlockPos above = surface.above();
        if (!level.getBlockState(above).isAir()) {
            return;
        }

        if (pattern == 0) {
            level.setBlock(above, Blocks.COBWEB.defaultBlockState(), 3);
        } else if (pattern == 1) {
            level.setBlock(above, Blocks.RED_MUSHROOM.defaultBlockState(), 3);
        } else if (pattern == 2) {
            level.setBlock(above, Blocks.DEAD_BUSH.defaultBlockState(), 3);
        }
    }

    private static void buildOldOak(ServerLevel level, BlockPos base) {
        clearColumn(level, base, 8);
        int trunkHeight = 4 + Math.floorMod(base.getX() + base.getZ(), 3);
        for (int dy = 0; dy < trunkHeight; dy++) {
            level.setBlock(base.above(dy), Blocks.OAK_LOG.defaultBlockState(), 3);
        }
        BlockPos canopyCenter = base.above(trunkHeight);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                    continue;
                }
                placeLeaf(level, canopyCenter.offset(dx, 0, dz));
                if (Math.abs(dx) < 2 && Math.abs(dz) < 2) {
                    placeLeaf(level, canopyCenter.above().offset(dx, 0, dz));
                }
            }
        }
        placeLeaf(level, canopyCenter.above(2));
    }

    private static void buildCobbleRelic(ServerLevel level, BlockPos base) {
        clearColumn(level, base, 4);
        level.setBlock(base, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3);
        level.setBlock(base.above(), Blocks.COBBLESTONE_WALL.defaultBlockState(), 3);
        level.setBlock(base.above(2), Blocks.TORCH.defaultBlockState(), 3);
    }

    private static void buildDeadStump(ServerLevel level, BlockPos base) {
        clearColumn(level, base, 5);
        level.setBlock(base, Blocks.DARK_OAK_LOG.defaultBlockState(), 3);
        level.setBlock(base.above(), Blocks.DARK_OAK_LOG.defaultBlockState(), 3);
        if (Math.floorMod(base.getX() + base.getZ(), 2) == 0) {
            level.setBlock(base.above(2), Blocks.DEAD_BUSH.defaultBlockState(), 3);
        }
    }

    private static void buildFloatingDirtShard(ServerLevel level, BlockPos base) {
        BlockPos shard = base.above(4 + Math.floorMod(base.getX() + base.getZ(), 3));
        level.setBlock(shard, Blocks.DIRT.defaultBlockState(), 3);
        level.setBlock(shard.east(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        if (level.getBlockState(shard.above()).isAir()) {
            level.setBlock(shard.above(), Blocks.GRASS.defaultBlockState(), 3);
        }
    }

    private static void buildAlphaPine(ServerLevel level, BlockPos base) {
        clearColumn(level, base, 11);
        int trunkHeight = 6 + Math.floorMod(base.getX() + base.getZ(), 3);
        for (int dy = 0; dy < trunkHeight; dy++) {
            level.setBlock(base.above(dy), Blocks.SPRUCE_LOG.defaultBlockState(), 3);
        }

        BlockPos top = base.above(trunkHeight - 1);
        for (int layer = 0; layer < 4; layer++) {
            int radius = 3 - layer;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) <= radius + 1) {
                        level.setBlock(top.below(layer).offset(dx, 0, dz),
                                Blocks.SPRUCE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3);
                    }
                }
            }
        }
        level.setBlock(top.above(), Blocks.SPRUCE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3);
    }

    private static void buildHauntedTree(ServerLevel level, BlockPos base) {
        clearColumn(level, base, 12);
        int trunkHeight = 7 + Math.floorMod(base.getX() + base.getZ(), 2);
        for (int dy = 0; dy < trunkHeight; dy++) {
            level.setBlock(base.above(dy), Blocks.DARK_OAK_LOG.defaultBlockState(), 3);
        }
        BlockPos crown = base.above(trunkHeight - 1);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                    continue;
                }
                if (Math.floorMod(dx * 3 + dz * 5 + crown.getX() + crown.getZ(), 4) != 0) {
                    level.setBlock(crown.offset(dx, 0, dz),
                            Blocks.DARK_OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3);
                }
            }
        }
        level.setBlock(crown.above(), Blocks.COBWEB.defaultBlockState(), 3);
    }

    private static void buildBrokenMonolith(ServerLevel level, BlockPos base) {
        clearColumn(level, base, 7);
        level.setBlock(base, Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 3);
        level.setBlock(base.above(), Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 3);
        level.setBlock(base.above(2), Blocks.COBBLESTONE_WALL.defaultBlockState(), 3);
        level.setBlock(base.above(3), Blocks.SOUL_TORCH.defaultBlockState(), 3);
    }

    private static void clearColumn(ServerLevel level, BlockPos start, int height) {
        for (int dy = 0; dy <= height; dy++) {
            level.setBlock(start.above(dy), Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static void placeLeaf(ServerLevel level, BlockPos pos) {
        BlockState leaves = Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
        level.setBlock(pos, leaves, 3);
    }

    private static long mix(long seed, int a, int b) {
        long value = seed;
        value ^= (long) a * 341873128712L;
        value ^= (long) b * 132897987541L;
        value ^= (value >>> 33);
        value *= 0xff51afd7ed558ccdL;
        value ^= (value >>> 33);
        value *= 0xc4ceb9fe1a85ec53L;
        value ^= (value >>> 33);
        return value;
    }

    private enum Variant {
        NONE,
        LEGACY_MEADOW,
        BROKEN_WASTES,
        ALPHA_FOREST,
        HAUNTED_OLD_GROWTH
    }
}
