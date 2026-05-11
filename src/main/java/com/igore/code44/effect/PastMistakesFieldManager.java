package com.igore.code44.effect;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.GreteminosEntity;
import com.igore.code44.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class PastMistakesFieldManager {
    private static final ResourceKey<Level> PAST_MISTAKES_FIELD = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "past_mistakes_field_v2")
    );

    private static final String ACTIVE_KEY = "code44PastMistakesFieldActive";
    private static final String RETURN_TICK_KEY = "code44PastMistakesFieldReturnTick";
    private static final String RETURN_X_KEY = "code44PastMistakesFieldReturnX";
    private static final String RETURN_Y_KEY = "code44PastMistakesFieldReturnY";
    private static final String RETURN_Z_KEY = "code44PastMistakesFieldReturnZ";
    private static final String RETURN_YAW_KEY = "code44PastMistakesFieldReturnYaw";
    private static final String RETURN_PITCH_KEY = "code44PastMistakesFieldReturnPitch";
    private static final String RETURN_DIMENSION_KEY = "code44PastMistakesFieldReturnDimension";
    private static final String LAST_CENTER_X_KEY = "code44PastMistakesFieldCenterX";
    private static final String LAST_CENTER_Z_KEY = "code44PastMistakesFieldCenterZ";
    private static final String NEXT_GRETEMINOS_SPAWN_TICK_KEY = "code44PastMistakesNextGreteminosSpawnTick";

    private static final int FIELD_Y = 80;
    private static final int FIELD_RADIUS = 96;
    private static final int FIELD_HEIGHT = 7;
    private static final int TREE_SPACING = 12;
    private static final int REGENERATE_DISTANCE = 24;
    private static final int SAFE_SPAWN_RADIUS = 4;
    private static final int EXIT_DOOR_COUNT = 2;
    private static final int EXIT_DOOR_MIN_DISTANCE = 300;
    private static final int EXIT_DOOR_MAX_DISTANCE = 1300;
    private static final int EXIT_DOOR_MIN_SPACING = 1000;
    private static final int EXIT_DOOR_CLEANUP_RADIUS = 5000;
    private static final int GRETEMINOS_MIN_RESPAWN_TICKS = 20 * 120;
    private static final int GRETEMINOS_MAX_RESPAWN_TICKS = 20 * 240;
    private static final int GRETEMINOS_RETRY_TICKS = 60;

    private PastMistakesFieldManager() {
    }

    public static boolean isSceneActive(ServerPlayer player) {
        return getModData(player).getBoolean(ACTIVE_KEY);
    }

    public static boolean isFieldLevel(Level level) {
        return level.dimension() == PAST_MISTAKES_FIELD;
    }

    public static boolean canEntityExist(Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return true;
        }

        ResourceLocation entityId = entity.getType().builtInRegistryHolder().key().location();
        return Code44Mod.MODID.equals(entityId.getNamespace());
    }

    public static boolean startScene(ServerLevel level, ServerPlayer player) {
        if (isSceneActive(player)) {
            return false;
        }

        ServerLevel fieldLevel = level.getServer().getLevel(PAST_MISTAKES_FIELD);
        if (fieldLevel == null) {
            return false;
        }

        CompoundTag data = getModData(player);
        data.putBoolean(ACTIVE_KEY, true);
        data.putDouble(RETURN_X_KEY, player.getX());
        data.putDouble(RETURN_Y_KEY, player.getY());
        data.putDouble(RETURN_Z_KEY, player.getZ());
        data.putFloat(RETURN_YAW_KEY, player.getYRot());
        data.putFloat(RETURN_PITCH_KEY, player.getXRot());
        data.putString(RETURN_DIMENSION_KEY, level.dimension().location().toString());
        data.putInt(LAST_CENTER_X_KEY, Integer.MIN_VALUE);
        data.putInt(LAST_CENTER_Z_KEY, Integer.MIN_VALUE);
        scheduleNextGreteminosSpawn(player, data);

        BlockPos spawnPos = new BlockPos(0, FIELD_Y + 1, TREE_SPACING / 2);
        ensureFieldAround(fieldLevel, data, spawnPos);
        spawnExitDoors(fieldLevel, spawnPos);
        player.teleportTo(fieldLevel, spawnPos.getX() + 0.5D, FIELD_Y + 1.05D, spawnPos.getZ() + 0.5D, player.getYRot(), player.getXRot());
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;
        player.connection.resetPosition();
        return true;
    }

    public static void tick(ServerLevel level, ServerPlayer player) {
        if (!isSceneActive(player)) {
            return;
        }

        if (!player.isAlive()) {
            clearSceneData(player);
            return;
        }

        CompoundTag data = getModData(player);
        ensureFieldAround(level, data, player.blockPosition());
        keepPlayerUnstuck(level, player);
        tickGreteminos(level, player, data);

        if (player.getY() < FIELD_Y + 0.9D) {
            liftPlayerToSurface(player);
        }
    }

    public static void recoverPlayerIfNeeded(ServerPlayer player) {
        if (!isSceneActive(player)) {
            return;
        }

        if (!player.isAlive()) {
            clearSceneData(player);
        }
    }

    public static void clearSceneData(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.remove(ACTIVE_KEY);
        data.remove(RETURN_X_KEY);
        data.remove(RETURN_Y_KEY);
        data.remove(RETURN_Z_KEY);
        data.remove(RETURN_YAW_KEY);
        data.remove(RETURN_PITCH_KEY);
        data.remove(RETURN_DIMENSION_KEY);
        data.remove(LAST_CENTER_X_KEY);
        data.remove(LAST_CENTER_Z_KEY);
        data.remove(NEXT_GRETEMINOS_SPAWN_TICK_KEY);
    }

    public static boolean exitScene(ServerPlayer player) {
        if (!isSceneActive(player)) {
            return false;
        }

        finishScene(player);
        return true;
    }

    private static void finishScene(ServerPlayer player) {
        CompoundTag data = getModData(player);
        ServerLevel returnLevel = resolveReturnLevel(player, data);
        double returnX = data.getDouble(RETURN_X_KEY);
        double returnY = data.getDouble(RETURN_Y_KEY);
        double returnZ = data.getDouble(RETURN_Z_KEY);
        float returnYaw = data.getFloat(RETURN_YAW_KEY);
        float returnPitch = data.getFloat(RETURN_PITCH_KEY);

        clearSceneData(player);

        if (returnLevel != null) {
            player.teleportTo(returnLevel, returnX, returnY, returnZ, returnYaw, returnPitch);
            player.setDeltaMovement(Vec3.ZERO);
            player.fallDistance = 0.0F;
            player.connection.resetPosition();
        }
    }

    private static ServerLevel resolveReturnLevel(ServerPlayer player, CompoundTag data) {
        ResourceLocation location = ResourceLocation.tryParse(data.getString(RETURN_DIMENSION_KEY));
        if (location == null) {
            return player.server.getLevel(Level.OVERWORLD);
        }

        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, location);
        ServerLevel level = player.server.getLevel(key);
        return level != null ? level : player.server.getLevel(Level.OVERWORLD);
    }

    private static void ensureFieldAround(ServerLevel level, CompoundTag data, BlockPos center) {
        int lastCenterX = data.getInt(LAST_CENTER_X_KEY);
        int lastCenterZ = data.getInt(LAST_CENTER_Z_KEY);
        if (Math.abs(center.getX() - lastCenterX) < REGENERATE_DISTANCE
                && Math.abs(center.getZ() - lastCenterZ) < REGENERATE_DISTANCE) {
            return;
        }

        generateField(level, center);
        data.putInt(LAST_CENTER_X_KEY, center.getX());
        data.putInt(LAST_CENTER_Z_KEY, center.getZ());
    }

    private static void generateField(ServerLevel level, BlockPos center) {
        for (int x = center.getX() - FIELD_RADIUS; x <= center.getX() + FIELD_RADIUS; x++) {
            for (int z = center.getZ() - FIELD_RADIUS; z <= center.getZ() + FIELD_RADIUS; z++) {
                BlockPos floorPos = new BlockPos(x, FIELD_Y, z);
                level.setBlockAndUpdate(floorPos.below(4), Blocks.BEDROCK.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(3), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(2), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos, Blocks.GRASS_BLOCK.defaultBlockState());

                for (int dy = 1; dy <= FIELD_HEIGHT; dy++) {
                    level.setBlockAndUpdate(floorPos.above(dy), Blocks.AIR.defaultBlockState());
                }
                level.setBlockAndUpdate(floorPos.above(), Blocks.AIR.defaultBlockState());
            }
        }

        int minTreeX = Math.floorDiv(center.getX() - FIELD_RADIUS, TREE_SPACING);
        int maxTreeX = Math.floorDiv(center.getX() + FIELD_RADIUS, TREE_SPACING);
        int minTreeZ = Math.floorDiv(center.getZ() - FIELD_RADIUS, TREE_SPACING);
        int maxTreeZ = Math.floorDiv(center.getZ() + FIELD_RADIUS, TREE_SPACING);

        for (int gridX = minTreeX; gridX <= maxTreeX; gridX++) {
            for (int gridZ = minTreeZ; gridZ <= maxTreeZ; gridZ++) {
                BlockPos treePos = new BlockPos(gridX * TREE_SPACING, FIELD_Y + 1, gridZ * TREE_SPACING);
                if (Math.abs(treePos.getX() - center.getX()) <= SAFE_SPAWN_RADIUS
                        && Math.abs(treePos.getZ() - center.getZ()) <= SAFE_SPAWN_RADIUS) {
                    continue;
                }
                buildTree(level, treePos);
            }
        }

        clearSpawnPatch(level, center);
    }

    private static void buildTree(ServerLevel level, BlockPos basePos) {
        BlockPos trunkBase = basePos;
        level.setBlockAndUpdate(trunkBase.below(), Blocks.GRASS_BLOCK.defaultBlockState());
        level.setBlockAndUpdate(trunkBase.below(2), Blocks.DIRT.defaultBlockState());
        level.setBlockAndUpdate(trunkBase.below(3), Blocks.DIRT.defaultBlockState());
        level.setBlockAndUpdate(trunkBase.below(4), Blocks.BEDROCK.defaultBlockState());

        int trunkHeight = 4 + Math.floorMod(basePos.getX() + basePos.getZ(), 2);
        for (int dy = 0; dy < trunkHeight; dy++) {
            level.setBlockAndUpdate(trunkBase.above(dy), Blocks.OAK_LOG.defaultBlockState());
        }

        BlockPos canopyCenter = trunkBase.above(trunkHeight - 1);
        placeLeafLayer(level, canopyCenter.below(), 2, true);
        placeLeafLayer(level, canopyCenter, 2, false);
        placeLeafLayer(level, canopyCenter.above(), 1, false);
        placeLeaf(level, canopyCenter.above(2));
        placeLeaf(level, canopyCenter.above(1).north());
        placeLeaf(level, canopyCenter.above(1).south());
        placeLeaf(level, canopyCenter.above(1).east());
        placeLeaf(level, canopyCenter.above(1).west());
        placeFlowersAroundTree(level, trunkBase);
    }

    private static void placeLeaf(ServerLevel level, BlockPos pos) {
        level.setBlockAndUpdate(pos, Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true));
    }

    private static void placeLeafLayer(ServerLevel level, BlockPos center, int radius, boolean trimCross) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (Math.abs(dx) == radius && Math.abs(dz) == radius) {
                    continue;
                }
                if (trimCross && Math.abs(dx) == radius && dz == 0) {
                    continue;
                }
                if (trimCross && Math.abs(dz) == radius && dx == 0) {
                    continue;
                }
                placeLeaf(level, center.offset(dx, 0, dz));
            }
        }
    }

    private static void placeFlowersAroundTree(ServerLevel level, BlockPos trunkBase) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                BlockPos flowerPos = new BlockPos(trunkBase.getX() + dx, FIELD_Y + 1, trunkBase.getZ() + dz);
                BlockPos floorPos = flowerPos.below();
                level.setBlockAndUpdate(floorPos.below(4), Blocks.BEDROCK.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(3), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(2), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos, Blocks.GRASS_BLOCK.defaultBlockState());
                level.setBlockAndUpdate(flowerPos, ((dx + dz) & 1) == 0 ? Blocks.DANDELION.defaultBlockState() : Blocks.POPPY.defaultBlockState());
            }
        }
    }

    private static void clearSpawnPatch(ServerLevel level, BlockPos center) {
        for (int dx = -SAFE_SPAWN_RADIUS; dx <= SAFE_SPAWN_RADIUS; dx++) {
            for (int dz = -SAFE_SPAWN_RADIUS; dz <= SAFE_SPAWN_RADIUS; dz++) {
                BlockPos floorPos = new BlockPos(center.getX() + dx, FIELD_Y, center.getZ() + dz);
                level.setBlockAndUpdate(floorPos.below(4), Blocks.BEDROCK.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(3), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(2), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos, Blocks.GRASS_BLOCK.defaultBlockState());

                for (int dy = 1; dy <= 5; dy++) {
                    level.setBlockAndUpdate(floorPos.above(dy), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private static void spawnExitDoors(ServerLevel level, BlockPos center) {
        AABB bounds = new AABB(
                center.getX() - EXIT_DOOR_CLEANUP_RADIUS,
                FIELD_Y - 4,
                center.getZ() - EXIT_DOOR_CLEANUP_RADIUS,
                center.getX() + EXIT_DOOR_CLEANUP_RADIUS,
                FIELD_Y + FIELD_HEIGHT + 8,
                center.getZ() + EXIT_DOOR_CLEANUP_RADIUS
        );
        level.getEntities(ModEntities.WHITE_DOOR.get(), bounds, entity -> true).forEach(Entity::discard);

        List<BlockPos> usedPositions = new ArrayList<>();
        double primaryAngle = level.random.nextDouble() * (Math.PI * 2.0D);
        BlockPos firstDoor = createExitDoorPos(center, primaryAngle, 300 + level.random.nextInt(81));
        placeExitDoor(level, center, firstDoor, usedPositions);

        double oppositeAngle = primaryAngle + Math.PI + ((level.random.nextDouble() * 0.34D) - 0.17D);
        BlockPos secondDoor = createExitDoorPos(center, oppositeAngle, 1100 + level.random.nextInt(121));
        placeExitDoor(level, center, secondDoor, usedPositions);
    }

    private static BlockPos createExitDoorPos(BlockPos center, double angle, int distance) {
        int x = center.getX() + (int) Math.round(Math.cos(angle) * distance);
        int z = center.getZ() + (int) Math.round(Math.sin(angle) * distance);
        return new BlockPos(x, FIELD_Y + 1, z);
    }

    private static void placeExitDoor(ServerLevel level, BlockPos center, BlockPos spawnPos, List<BlockPos> usedPositions) {
        if (spawnPos.closerThan(center, EXIT_DOOR_MIN_DISTANCE - 0.5D)) {
            return;
        }
        for (BlockPos usedPosition : usedPositions) {
            if (usedPosition.closerThan(spawnPos, EXIT_DOOR_MIN_SPACING)) {
                return;
            }
        }

        prepareExitDoorArea(level, spawnPos);
        generateField(level, spawnPos);
        if (spawnExitDoor(level, center, spawnPos)) {
            usedPositions.add(spawnPos);
        }
    }

    private static void prepareExitDoorArea(ServerLevel level, BlockPos spawnPos) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos floorPos = new BlockPos(spawnPos.getX() + dx, FIELD_Y, spawnPos.getZ() + dz);
                level.setBlockAndUpdate(floorPos.below(4), Blocks.BEDROCK.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(3), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(2), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(), Blocks.DIRT.defaultBlockState());
                level.setBlockAndUpdate(floorPos, Blocks.GRASS_BLOCK.defaultBlockState());

                for (int dy = 1; dy <= 5; dy++) {
                    level.setBlockAndUpdate(floorPos.above(dy), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private static boolean spawnExitDoor(ServerLevel level, BlockPos center, BlockPos spawnPos) {
        Entity entity = ModEntities.WHITE_DOOR.get().create(level);
        if (entity == null) {
            return false;
        }

        float yaw = calculateDoorYaw(center, spawnPos);
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, yaw, 0.0F);
        return level.addFreshEntity(entity);
    }

    private static float calculateDoorYaw(BlockPos center, BlockPos doorPos) {
        double deltaX = center.getX() - doorPos.getX();
        double deltaZ = center.getZ() - doorPos.getZ();
        float rawYaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0D);
        return Math.round(rawYaw / 90.0F) * 90.0F;
    }

    private static void liftPlayerToSurface(ServerPlayer player) {
        double x = Math.floor(player.getX()) + 0.5D;
        double z = Math.floor(player.getZ()) + 0.5D;
        player.teleportTo(player.serverLevel(), x, FIELD_Y + 1.05D, z, player.getYRot(), player.getXRot());
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;
        player.connection.resetPosition();
    }

    private static void keepPlayerUnstuck(ServerLevel level, ServerPlayer player) {
        BlockPos feetPos = BlockPos.containing(player.getX(), player.getY(), player.getZ());
        clearIfSolid(level, feetPos);
        clearIfSolid(level, feetPos.above());
        clearIfSolid(level, feetPos.above(2));

        if (!level.getBlockState(feetPos).isAir() || !level.getBlockState(feetPos.above()).isAir()) {
            liftPlayerToSurface(player);
        }
    }

    private static void clearIfSolid(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).isAir()) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    private static void tickGreteminos(ServerLevel level, ServerPlayer player, CompoundTag data) {
        if (player.tickCount < data.getInt(NEXT_GRETEMINOS_SPAWN_TICK_KEY)) {
            return;
        }

        if (hasActiveGreteminos(level, player)) {
            scheduleNextGreteminosSpawn(player, data);
            return;
        }

        if (spawnGreteminos(level, player)) {
            scheduleNextGreteminosSpawn(player, data);
        } else {
            data.putInt(NEXT_GRETEMINOS_SPAWN_TICK_KEY, player.tickCount + GRETEMINOS_RETRY_TICKS);
        }
    }

    private static boolean hasActiveGreteminos(ServerLevel level, ServerPlayer player) {
        return !level.getEntities(ModEntities.GRETEMINOS.get(), player.getBoundingBox().inflate(48.0D), Entity::isAlive).isEmpty();
    }

    private static boolean spawnGreteminos(ServerLevel level, ServerPlayer player) {
        GreteminosEntity entity = ModEntities.GRETEMINOS.get().create(level);
        if (entity == null) {
            return false;
        }

        for (int attempt = 0; attempt < 20; attempt++) {
            double angle = level.random.nextDouble() * (Math.PI * 2.0D);
            int distance = 12 + level.random.nextInt(9);
            double x = player.getX() + Math.cos(angle) * distance;
            double z = player.getZ() + Math.sin(angle) * distance;
            BlockPos spawnPos = new BlockPos((int) Math.floor(x), FIELD_Y + 1, (int) Math.floor(z));

            if (!level.getBlockState(spawnPos.below()).isSolidRender(level, spawnPos.below())) {
                continue;
            }
            if (!level.getBlockState(spawnPos).isAir() || !level.getBlockState(spawnPos.above()).isAir()) {
                continue;
            }

            entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, player.getYRot(), 0.0F);
            entity.setTarget(player);
            level.addFreshEntity(entity);
            return true;
        }

        return false;
    }

    private static void scheduleNextGreteminosSpawn(ServerPlayer player, CompoundTag data) {
        int interval = GRETEMINOS_MIN_RESPAWN_TICKS
                + player.level().random.nextInt(GRETEMINOS_MAX_RESPAWN_TICKS - GRETEMINOS_MIN_RESPAWN_TICKS + 1);
        data.putInt(NEXT_GRETEMINOS_SPAWN_TICK_KEY, player.tickCount + interval);
    }

    private static CompoundTag getModData(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains(FearManager.MOD_DATA_KEY)) {
            persistentData.put(FearManager.MOD_DATA_KEY, new CompoundTag());
        }
        return persistentData.getCompound(FearManager.MOD_DATA_KEY);
    }
}
