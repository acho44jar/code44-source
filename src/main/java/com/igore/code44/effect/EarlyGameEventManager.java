package com.igore.code44.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EarlyGameEventManager {
    private static final String GHOST_TREE_USED_KEY = "code44GhostTreeUsed";
    private static final Map<UUID, WrongMobData> WRONG_MOBS = new HashMap<>();
    private static final Map<UUID, GhostTreeData> GHOST_TREES = new HashMap<>();

    private EarlyGameEventManager() {
    }

    public static void tick(ServerLevel level, ServerPlayer player) {
        tickWrongMob(level, player);
        tickGhostTree(level, player);
    }

    public static boolean triggerWrongMob(ServerLevel level, ServerPlayer player) {
        if (WRONG_MOBS.containsKey(player.getUUID())) {
            return false;
        }

        Animal target = findWrongMobTarget(level, player);
        if (target == null) {
            target = spawnFallbackWrongMob(level, player);
        }

        if (target == null) {
            return false;
        }

        WRONG_MOBS.put(player.getUUID(), new WrongMobData(target.getUUID(), player.tickCount + 160));
        return true;
    }

    public static boolean triggerGhostTree(ServerLevel level, ServerPlayer player) {
        if (GHOST_TREES.containsKey(player.getUUID())) {
            return false;
        }
        if (player.getPersistentData().getBoolean(GHOST_TREE_USED_KEY)) {
            return false;
        }
        if ((int) (level.getDayTime() / 24000L) != 1) {
            return false;
        }

        BlockPos basePos = findGhostTreePosition(level, player);
        if (basePos == null) {
            return false;
        }

        List<BlockSnapshot> snapshots = new ArrayList<>();
        placeGhostTree(level, basePos, snapshots);
        GHOST_TREES.put(player.getUUID(), new GhostTreeData(-1, basePos.immutable(), snapshots));
        player.getPersistentData().putBoolean(GHOST_TREE_USED_KEY, true);
        return !snapshots.isEmpty();
    }

    private static void tickWrongMob(ServerLevel level, ServerPlayer player) {
        WrongMobData data = WRONG_MOBS.get(player.getUUID());
        if (data == null) {
            return;
        }

        if (player.tickCount >= data.untilTick()) {
            WRONG_MOBS.remove(player.getUUID());
            return;
        }

        if (!(level.getEntity(data.entityUuid()) instanceof Animal animal) || !animal.isAlive()) {
            WRONG_MOBS.remove(player.getUUID());
            return;
        }

        animal.getLookControl().setLookAt(player, 90.0F, 90.0F);
        if (animal.position().distanceTo(player.position()) > 2.4D) {
            animal.getNavigation().moveTo(player.getX(), player.getY(), player.getZ(), 1.35D);
        } else {
            animal.getNavigation().stop();
            animal.setDeltaMovement(Vec3.ZERO);
        }
    }

    private static void tickGhostTree(ServerLevel level, ServerPlayer player) {
        GhostTreeData data = GHOST_TREES.get(player.getUUID());
        if (data == null) {
            return;
        }

        if (data.restoreTick() < 0) {
            if (hasPlayerNoticedGhostTree(player, data.basePos())) {
                GHOST_TREES.put(player.getUUID(), new GhostTreeData(player.tickCount + 200, data.basePos(), data.snapshots()));
            }
            return;
        }

        if (player.tickCount < data.restoreTick()) {
            return;
        }

        for (BlockSnapshot snapshot : data.snapshots()) {
            level.setBlock(snapshot.pos(), snapshot.state(), 3);
        }

        GHOST_TREES.remove(player.getUUID());
    }

    public static boolean isManagedGhostTreeBlock(ServerPlayer player, BlockPos pos) {
        GhostTreeData data = GHOST_TREES.get(player.getUUID());
        if (data == null) {
            return false;
        }

        for (BlockSnapshot snapshot : data.snapshots()) {
            if (snapshot.pos().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    private static Animal findWrongMobTarget(ServerLevel level, ServerPlayer player) {
        return level.getEntitiesOfClass(
                Animal.class,
                player.getBoundingBox().inflate(28.0D),
                animal -> animal instanceof Cow || animal instanceof Pig || animal instanceof Sheep || animal instanceof Chicken
        ).stream().findFirst().orElse(null);
    }

    private static Animal spawnFallbackWrongMob(ServerLevel level, ServerPlayer player) {
        BlockPos spawnPos = findWrongMobSpawnPosition(level, player);
        if (spawnPos == null) {
            spawnPos = findCloseWrongMobSpawnPosition(level, player);
        }
        if (spawnPos == null) {
            return null;
        }

        EntityType<? extends Animal> type = switch (level.random.nextInt(4)) {
            case 0 -> EntityType.COW;
            case 1 -> EntityType.PIG;
            case 2 -> EntityType.SHEEP;
            default -> EntityType.CHICKEN;
        };

        Animal animal = type.create(level);
        if (animal == null) {
            return null;
        }

        animal.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(animal);
        return animal;
    }

    private static BlockPos findWrongMobSpawnPosition(ServerLevel level, ServerPlayer player) {
        for (int attempt = 0; attempt < 40; attempt++) {
            double angle = level.random.nextDouble() * (Math.PI * 2.0D);
            int distance = 8 + level.random.nextInt(13);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            BlockPos surfacePos = level.getHeightmapPos(
                    net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    player.blockPosition().offset(offsetX, 0, offsetZ)
            ).above();

            BlockPos belowPos = surfacePos.below();
            if (!level.getBlockState(belowPos).isFaceSturdy(level, belowPos, net.minecraft.core.Direction.UP)) {
                continue;
            }

            if (!level.getFluidState(belowPos).isEmpty() || !level.getFluidState(surfacePos).isEmpty()) {
                continue;
            }

            BlockState feetState = level.getBlockState(surfacePos);
            BlockState headState = level.getBlockState(surfacePos.above());
            if (!feetState.isAir() && !feetState.canBeReplaced()) {
                continue;
            }
            if (!headState.isAir() && !headState.canBeReplaced()) {
                continue;
            }

            if (!feetState.isAir()) {
                level.setBlock(surfacePos, Blocks.AIR.defaultBlockState(), 3);
            }
            if (!headState.isAir()) {
                level.setBlock(surfacePos.above(), Blocks.AIR.defaultBlockState(), 3);
            }
            return surfacePos;
        }

        return null;
    }

    private static BlockPos findCloseWrongMobSpawnPosition(ServerLevel level, ServerPlayer player) {
        BlockPos playerPos = player.blockPosition();

        for (int attempt = 0; attempt < 48; attempt++) {
            double angle = level.random.nextDouble() * (Math.PI * 2.0D);
            int distance = 4 + level.random.nextInt(6);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            BlockPos basePos = playerPos.offset(offsetX, 0, offsetZ);

            for (int yShift = 3; yShift >= -4; yShift--) {
                BlockPos spawnPos = basePos.offset(0, yShift, 0);
                BlockPos belowPos = spawnPos.below();
                BlockState feetState = level.getBlockState(spawnPos);
                BlockState headState = level.getBlockState(spawnPos.above());

                if (!level.getBlockState(belowPos).isFaceSturdy(level, belowPos, net.minecraft.core.Direction.UP)) {
                    continue;
                }

                if (!level.getFluidState(spawnPos).isEmpty() || !level.getFluidState(spawnPos.above()).isEmpty()) {
                    continue;
                }

                if (!feetState.isAir() && !feetState.canBeReplaced()) {
                    continue;
                }
                if (!headState.isAir() && !headState.canBeReplaced()) {
                    continue;
                }

                if (!level.noCollision(new net.minecraft.world.phys.AABB(
                        spawnPos.getX() + 0.2D,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.2D,
                        spawnPos.getX() + 0.8D,
                        spawnPos.getY() + 1.95D,
                        spawnPos.getZ() + 0.8D
                ))) {
                    continue;
                }

                if (!feetState.isAir()) {
                    level.setBlock(spawnPos, Blocks.AIR.defaultBlockState(), 3);
                }
                if (!headState.isAir()) {
                    level.setBlock(spawnPos.above(), Blocks.AIR.defaultBlockState(), 3);
                }
                return spawnPos;
            }
        }

        return null;
    }

    private static BlockPos findGhostTreePosition(ServerLevel level, ServerPlayer player) {
        Vec3 look = player.getViewVector(1.0F).normalize();

        for (int distance = 9; distance <= 15; distance++) {
            BlockPos guess = BlockPos.containing(player.getX() + (look.x * distance), player.getY(), player.getZ() + (look.z * distance));
            BlockPos basePos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, guess);

            if (!level.canSeeSky(basePos)) {
                continue;
            }

            if (!level.getBlockState(basePos.below()).isFaceSturdy(level, basePos.below(), net.minecraft.core.Direction.UP)) {
                continue;
            }

            if (!isGhostTreeAreaClear(level, basePos)) {
                continue;
            }

            return basePos;
        }

        return null;
    }

    private static boolean isGhostTreeAreaClear(ServerLevel level, BlockPos basePos) {
        for (int y = 0; y <= 5; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos pos = basePos.offset(x, y, z);
                    if (!level.isEmptyBlock(pos)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void placeGhostTree(ServerLevel level, BlockPos basePos, List<BlockSnapshot> snapshots) {
        for (int y = 0; y < 4; y++) {
            setTreeBlock(level, basePos.above(y), Blocks.OAK_LOG.defaultBlockState(), snapshots);
        }

        BlockPos crown = basePos.above(4);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                setTreeBlock(level, crown.offset(x, 0, z), Blocks.OAK_LEAVES.defaultBlockState(), snapshots);
            }
        }

        setTreeBlock(level, crown.above(), Blocks.OAK_LEAVES.defaultBlockState(), snapshots);
    }

    private static void setTreeBlock(ServerLevel level, BlockPos pos, BlockState newState, List<BlockSnapshot> snapshots) {
        snapshots.add(new BlockSnapshot(pos.immutable(), level.getBlockState(pos)));
        level.setBlock(pos, newState, 3);
    }

    private static boolean hasPlayerNoticedGhostTree(ServerPlayer player, BlockPos basePos) {
        Vec3 playerEyes = player.getEyePosition();
        Vec3 treeCenter = Vec3.atCenterOf(basePos.above(2));
        Vec3 toTree = treeCenter.subtract(playerEyes);
        if (toTree.lengthSqr() > (26.0D * 26.0D)) {
            return false;
        }

        Vec3 look = player.getViewVector(1.0F).normalize();
        Vec3 direction = toTree.normalize();
        return look.dot(direction) > 0.93D;
    }

    private record WrongMobData(UUID entityUuid, int untilTick) {
    }

    private record GhostTreeData(int restoreTick, BlockPos basePos, List<BlockSnapshot> snapshots) {
    }

    private record BlockSnapshot(BlockPos pos, BlockState state) {
    }
}
