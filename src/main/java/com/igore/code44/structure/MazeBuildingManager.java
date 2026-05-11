package com.igore.code44.structure;

import com.igore.code44.effect.MazeDimensionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class MazeBuildingManager {
    private static final Random RANDOM = new Random();
    private static final Map<UUID, BlockPos> ACTIVE_ALTARS = new HashMap<>();

    private MazeBuildingManager() {
    }

    public static boolean tryPlaceMazeBuilding(ServerLevel level, ServerPlayer player) {
        BlockPos origin = findAltarPosition(level, player);
        if (origin == null) {
            return false;
        }

        buildAltar(level, origin);
        ACTIVE_ALTARS.put(player.getUUID(), origin.immutable());
        return true;
    }

    public static void tick(ServerLevel level, ServerPlayer player) {
        BlockPos altarPos = ACTIVE_ALTARS.get(player.getUUID());
        if (altarPos == null) {
            return;
        }

        Vec3 altarCenter = new Vec3(altarPos.getX() + 0.5D, altarPos.getY() + 1.0D, altarPos.getZ() + 0.5D);
        if (player.position().distanceTo(altarCenter) <= 3.4D) {
            if (MazeDimensionManager.startScene(level, player, altarPos)) {
                ACTIVE_ALTARS.remove(player.getUUID());
            }
        }
    }

    private static void buildAltar(ServerLevel level, BlockPos origin) {
        BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = origin.offset(x, 0, z);
                level.setBlockAndUpdate(pos, cobble);
            }
        }

        level.setBlockAndUpdate(origin, cobble);
        level.setBlockAndUpdate(origin.above(), Blocks.STONE_PRESSURE_PLATE.defaultBlockState());
        level.setBlockAndUpdate(origin.offset(-1, 1, -1), Blocks.REDSTONE_TORCH.defaultBlockState());
        level.setBlockAndUpdate(origin.offset(1, 1, -1), Blocks.REDSTONE_TORCH.defaultBlockState());
        level.setBlockAndUpdate(origin.offset(-1, 1, 1), Blocks.REDSTONE_TORCH.defaultBlockState());
        level.setBlockAndUpdate(origin.offset(1, 1, 1), Blocks.REDSTONE_TORCH.defaultBlockState());
    }

    private static BlockPos findAltarPosition(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        for (int attempt = 0; attempt < 50; attempt++) {
            double angle = RANDOM.nextDouble() * (Math.PI * 2.0D);
            int distance = 4 + RANDOM.nextInt(7);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            BlockPos surface = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, center.offset(offsetX, 0, offsetZ));
            return surface;
        }

        return level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                center.offset(8, 0, 0)
        );
    }
}
