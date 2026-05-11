package com.igore.code44.entity;

import com.igore.code44.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;

public final class BlackAnimalManager {
    private BlackAnimalManager() {
    }

    public static boolean trySpawnBlackAnimal(ServerLevel level, ServerPlayer player) {
        if (countBlackAnimalsNearby(level, player.blockPosition(), 48.0D) >= 4) {
            return false;
        }

        BlockPos spawnPos = findGroundSpawnPosition(level, player.blockPosition(), 18, 34, 48);
        if (spawnPos == null) {
            return false;
        }

        EntityType<? extends PathfinderMob> type = pickRandomType(level);
        PathfinderMob entity = type.create(level);
        if (entity == null) {
            return false;
        }

        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(entity);
        return true;
    }

    public static int countBlackAnimalsNearby(ServerLevel level, BlockPos centerPos, double radius) {
        AABB box = new AABB(centerPos).inflate(radius);
        return level.getEntities(ModEntities.BLACK_COW.get(), box, entity -> true).size()
                + level.getEntities(ModEntities.BLACK_PIG.get(), box, entity -> true).size()
                + level.getEntities(ModEntities.BLACK_CHICKEN.get(), box, entity -> true).size();
    }

    private static EntityType<? extends PathfinderMob> pickRandomType(ServerLevel level) {
        return switch (level.random.nextInt(3)) {
            case 0 -> ModEntities.BLACK_COW.get();
            case 1 -> ModEntities.BLACK_PIG.get();
            default -> ModEntities.BLACK_CHICKEN.get();
        };
    }

    private static BlockPos findGroundSpawnPosition(ServerLevel level, BlockPos centerPos, int minDistance, int maxDistance, int attempts) {
        for (int attempt = 0; attempt < attempts; attempt++) {
            double angle = level.random.nextDouble() * (Math.PI * 2.0D);
            int distance = minDistance + level.random.nextInt(maxDistance - minDistance + 1);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            BlockPos groundPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, centerPos.offset(offsetX, 0, offsetZ));
            BlockPos spawnPos = groundPos.above();

            if (!level.isEmptyBlock(spawnPos) || !level.isEmptyBlock(spawnPos.above())) {
                continue;
            }

            if (!level.getFluidState(spawnPos).isEmpty() || !level.getFluidState(spawnPos.below()).isEmpty()) {
                continue;
            }

            AABB spawnBox = new AABB(
                    spawnPos.getX() + 0.1D,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.1D,
                    spawnPos.getX() + 0.9D,
                    spawnPos.getY() + 1.9D,
                    spawnPos.getZ() + 0.9D
            );

            if (!level.noCollision(spawnBox)) {
                continue;
            }

            return spawnPos;
        }

        return null;
    }
}
