package com.igore.code44.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public final class GlassFrameManager {
    private static final Random RANDOM = new Random();

    private GlassFrameManager() {
    }

    public static boolean tryPlaceGlassFrame(ServerLevel level, ServerPlayer player) {
        BlockPos origin = findFramePosition(level, player);
        if (origin == null) {
            return false;
        }

        buildFrame(level, origin);
        return true;
    }

    private static void buildFrame(ServerLevel level, BlockPos origin) {
        BlockState glass = Blocks.GLASS.defaultBlockState();

        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 4; y++) {
                BlockPos pos = origin.offset(x, y, 0);
                boolean frameEdge = x == -1 || x == 1 || y == 0 || y == 4;
                boolean centerHole = x == 0 && y >= 1 && y <= 2;
                if (centerHole) {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                } else if (frameEdge || y == 3) {
                    level.setBlockAndUpdate(pos, glass);
                } else {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        }

        level.setBlockAndUpdate(origin.offset(0, 2, 1), glass);
        level.setBlockAndUpdate(origin.offset(0, 3, 1), glass);
        level.setBlockAndUpdate(origin.offset(0, 4, 1), glass);
    }

    private static BlockPos findFramePosition(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();

        for (int attempt = 0; attempt < 60; attempt++) {
            double angle = RANDOM.nextDouble() * (Math.PI * 2.0D);
            int distance = 20 + RANDOM.nextInt(31);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            BlockPos surface = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, center.offset(offsetX, 0, offsetZ));
            BlockPos origin = surface;
            clearFrameSpace(level, origin);
            return origin;
        }

        BlockPos fallbackSurface = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                center.offset(40, 0, 0)
        );
        BlockPos fallbackOrigin = fallbackSurface;
        clearFrameSpace(level, fallbackOrigin);
        return fallbackOrigin;
    }

    private static void clearFrameSpace(ServerLevel level, BlockPos origin) {
        for (int y = 0; y <= 5; y++) {
            for (int x = -1; x <= 1; x++) {
                BlockPos pos = origin.offset(x, y, 0);
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }
    }
}
