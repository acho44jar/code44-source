package com.igore.code44.entity;

import com.igore.code44.registry.ModEntities;
import com.igore.code44.registry.ModSounds;
import com.igore.code44.sound.HorrorSoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class EntitySpawnManager {
    private EntitySpawnManager() {
    }

    public static void playE44efbuiSpawnCue(ServerLevel level, ServerPlayer player) {
        BlockPos cuePos = findSpawnPositionNearPlayer(level, player);

        if (cuePos == null) {
            level.playSound(
                    null,
                    player.getX() + 4.0D,
                    player.getY(),
                    player.getZ(),
                    ModSounds.EFBUI_SPAWN.get(),
                    SoundSource.HOSTILE,
                    1.0F,
                    1.0F
            );
            return;
        }

        level.playSound(
                null,
                cuePos.getX() + 0.5D,
                cuePos.getY(),
                cuePos.getZ() + 0.5D,
                ModSounds.EFBUI_SPAWN.get(),
                SoundSource.HOSTILE,
                1.0F,
                1.0F
        );
    }

    public static boolean trySpawnE44efbui(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.E44EFBUI.get(), player.getBoundingBox().inflate(24.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findSpawnPositionNearPlayer(level, player);

        if (spawnPos == null) {
            return false;
        }

        E44efbuiEntity entity = ModEntities.E44EFBUI.get().create(level);

        if (entity == null) {
            return false;
        }

        RandomSource random = level.random;
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        entity.setTarget(player);
        level.addFreshEntity(entity);
        return true;
    }

    public static boolean trySpawnE44efbuiStare(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.E44EFBUI.get(), player.getBoundingBox().inflate(32.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findSpawnPositionNearPlayer(level, player.blockPosition(), 6, 12, 40);
        if (spawnPos == null) {
            return false;
        }

        E44efbuiEntity entity = ModEntities.E44EFBUI.get().create(level);
        if (entity == null) {
            return false;
        }

        Vec3 lookTarget = player.position().subtract(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        float yaw = (float) (Math.toDegrees(Math.atan2(lookTarget.z, lookTarget.x)) - 90.0D);
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, yaw, 0.0F);
        entity.configureStareOnly(60);
        level.addFreshEntity(entity);
        return true;
    }

    public static boolean trySpawnDoorwayE44efbui(ServerLevel level, ServerPlayer player, BlockPos clickedDoorPos) {
        if (!level.getEntities(ModEntities.E44EFBUI.get(), player.getBoundingBox().inflate(32.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos doorwayPos = DoorOpenerEntity.findDoorwaySpawnPos(level, clickedDoorPos, player.getDirection());
        if (doorwayPos == null) {
            return false;
        }

        E44efbuiEntity entity = ModEntities.E44EFBUI.get().create(level);
        if (entity == null) {
            return false;
        }

        Vec3 lookTarget = player.position().subtract(doorwayPos.getX() + 0.5D, doorwayPos.getY(), doorwayPos.getZ() + 0.5D);
        float yaw = (float) (Math.toDegrees(Math.atan2(lookTarget.z, lookTarget.x)) - 90.0D);
        entity.moveTo(doorwayPos.getX() + 0.5D, doorwayPos.getY(), doorwayPos.getZ() + 0.5D, yaw, 0.0F);
        entity.configureStareOnly(30);
        level.addFreshEntity(entity);
        HorrorSoundManager.playDoorEfbuiSpawn(level, doorwayPos);
        return true;
    }

    public static boolean trySpawnDoorOpener(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.DOOR_OPENER.get(), player.getBoundingBox().inflate(20.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findDryLandSpawnPositionNearPlayer(level, player.blockPosition(), 4, 10, 48);
        if (spawnPos == null) {
            return false;
        }

        DoorOpenerEntity entity = ModEntities.DOOR_OPENER.get().create(level);
        if (entity == null) {
            return false;
        }

        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, 0.0F, 0.0F);
        level.addFreshEntity(entity);
        return true;
    }

    public static boolean trySpawnZer000(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.ZER000.get(), player.getBoundingBox().inflate(48.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findSpawnPositionNearPlayer(level, player.blockPosition(), 8, 18, 50);

        if (spawnPos == null) {
            return false;
        }

        Zer000Entity entity = ModEntities.ZER000.get().create(level);

        if (entity == null) {
            return false;
        }

        RandomSource random = level.random;
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        entity.setTarget(player);
        level.addFreshEntity(entity);
        HorrorSoundManager.startZer000Sound(player, entity);
        return true;
    }

    public static boolean trySpawnZer000At(ServerLevel level, ServerPlayer player, Vec3 position) {
        if (!level.getEntities(ModEntities.ZER000.get(), player.getBoundingBox().inflate(48.0D), entity -> true).isEmpty()) {
            return false;
        }

        Zer000Entity entity = ModEntities.ZER000.get().create(level);

        if (entity == null) {
            return false;
        }

        RandomSource random = level.random;
        Vec3 spawnPos = player.position().add(player.getLookAngle().normalize().scale(1.2D));
        entity.moveTo(spawnPos.x, player.getY(), spawnPos.z, random.nextFloat() * 360.0F, 0.0F);
        entity.setAttackDelayTicks(0);
        entity.setTarget(player);
        level.addFreshEntity(entity);
        entity.doHurtTarget(player);
        HorrorSoundManager.startZer000Sound(player, entity);
        return true;
    }

    public static boolean trySpawnObserver(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.OBSERVER.get(), player.getBoundingBox().inflate(64.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findVisibleObserverSpawnPosition(level, player);
        return spawnObserverAt(level, player, spawnPos);
    }

    public static boolean trySpawnTunnelObserver(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.OBSERVER.get(), player.getBoundingBox().inflate(72.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = player.blockPosition().offset(0, 0, 14 + level.random.nextInt(20));
        clearSoftBlock(level, spawnPos);
        clearSoftBlock(level, spawnPos.above());

        ObserverEntity entity = ModEntities.OBSERVER.get().create(level);
        if (entity == null) {
            return false;
        }

        Vec3 lookTarget = player.position().subtract(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        float yaw = (float) (Math.toDegrees(Math.atan2(lookTarget.z, lookTarget.x)) - 90.0D);
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, yaw, 0.0F);
        entity.configureRushOnSeen(true);
        entity.setTarget(player);
        level.addFreshEntity(entity);
        return true;
    }

    public static boolean trySpawnWhiteName(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.WHITE_NAME.get(), player.getBoundingBox().inflate(64.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findWhiteNameSpawnPosition(level, player);

        if (spawnPos == null) {
            return false;
        }

        WhiteNameEntity entity = ModEntities.WHITE_NAME.get().create(level);

        if (entity == null) {
            return false;
        }

        Vec3 lookTarget = player.position().subtract(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        float yaw = (float) (Math.toDegrees(Math.atan2(lookTarget.z, lookTarget.x)) - 90.0D);
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, yaw, 0.0F);
        level.addFreshEntity(entity);
        return true;
    }

    public static boolean trySpawnPastMistakesDoor(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.PAST_MISTAKES_DOOR.get(), player.getBoundingBox().inflate(64.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findSpawnPositionNearPlayer(level, player.blockPosition(), 18, 30, 50);
        if (spawnPos == null) {
            return false;
        }

        PastMistakesDoorEntity entity = ModEntities.PAST_MISTAKES_DOOR.get().create(level);
        if (entity == null) {
            return false;
        }

        Direction facing = player.blockPosition().getX() >= spawnPos.getX() ? Direction.EAST : Direction.WEST;
        if (Math.abs(player.blockPosition().getZ() - spawnPos.getZ()) > Math.abs(player.blockPosition().getX() - spawnPos.getX())) {
            facing = player.blockPosition().getZ() >= spawnPos.getZ() ? Direction.SOUTH : Direction.NORTH;
        }

        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, facing.toYRot(), 0.0F);
        level.addFreshEntity(entity);
        return true;
    }

    public static boolean trySpawnFortyFour(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.FORTY_FOUR.get(), player.getBoundingBox().inflate(64.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findVisibleStaticSpawnPosition(level, player, 30, 50, 120);
        if (spawnPos == null) {
            return false;
        }

        FortyFourEntity entity = ModEntities.FORTY_FOUR.get().create(level);
        if (entity == null) {
            return false;
        }

        RandomSource random = level.random;
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(entity);
        return true;
    }

    public static boolean trySpawnSkinwalker(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.SKINWALKER.get(), player.getBoundingBox().inflate(48.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findSpawnPositionNearPlayer(level, player.blockPosition(), 6, 18, 50);
        if (spawnPos == null) {
            return false;
        }

        SkinwalkerEntity entity = ModEntities.SKINWALKER.get().create(level);
        if (entity == null) {
            return false;
        }

        Vec3 lookTarget = player.position().subtract(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        float yaw = (float) (Math.toDegrees(Math.atan2(lookTarget.z, lookTarget.x)) - 90.0D);
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, yaw, 0.0F);
        entity.mimicPlayer(player);
        level.addFreshEntity(entity);
        return true;
    }

    public static boolean trySpawnErr44r(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.ERR44R.get(), player.getBoundingBox().inflate(64.0D), entity -> true).isEmpty()) {
            return false;
        }

        RandomSource random = level.random;
        double angle = random.nextDouble() * (Math.PI * 2.0D);
        int distance = 10 + random.nextInt(21);
        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;
        double y = player.getY() + 10.0D + random.nextInt(10);

        com.igore.code44.entity.Err44rEntity entity = ModEntities.ERR44R.get().create(level);
        if (entity == null) {
            return false;
        }

        entity.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(entity);
        HorrorSoundManager.playErr44r(player);
        return true;
    }

    public static boolean trySpawnGreteminos(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.GRETEMINOS.get(), player.getBoundingBox().inflate(72.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findSpawnPositionNearPlayer(level, player.blockPosition(), 10, 22, 60);
        if (spawnPos == null) {
            return false;
        }

        GreteminosEntity entity = ModEntities.GRETEMINOS.get().create(level);
        if (entity == null) {
            return false;
        }

        Vec3 lookTarget = player.position().subtract(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        float yaw = (float) (Math.toDegrees(Math.atan2(lookTarget.z, lookTarget.x)) - 90.0D);
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, yaw, 0.0F);
        entity.setTarget(player);
        level.addFreshEntity(entity);
        return true;
    }

    public static BlockPos findChatFakePlayerSpawn(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();
        RandomSource random = level.random;

        for (int attempt = 0; attempt < 40; attempt++) {
            double angle = random.nextDouble() * (Math.PI * 2.0D);
            int distance = 4 + random.nextInt(5);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            BlockPos spawnPos = findSpawnPositionNearPlayer(level, center.offset(offsetX, 0, offsetZ), 0, 1, 6);
            if (spawnPos != null) {
                return spawnPos;
            }
        }

        return findSpawnPositionNearPlayer(level, player);
    }

    private static BlockPos findWhiteNameSpawnPosition(ServerLevel level, ServerPlayer player) {
        RandomSource random = level.random;
        BlockPos centerPos = player.blockPosition();

        for (int attempt = 0; attempt < 48; attempt++) {
            double angle = random.nextDouble() * (Math.PI * 2.0D);
            int distance = 30 + random.nextInt(16);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            int offsetY = random.nextInt(28);
            BlockPos spawnPos = centerPos.offset(offsetX, offsetY, offsetZ);

            AABB spawnBox = new AABB(
                    spawnPos.getX() + 0.2D,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.2D,
                    spawnPos.getX() + 0.8D,
                    spawnPos.getY() + 1.95D,
                    spawnPos.getZ() + 0.8D
            );

            if (!level.noCollision(spawnBox)) {
                continue;
            }

            return spawnPos;
        }

        return fallbackGroundSpawn(level, centerPos);
    }

    public static boolean trySpawnFootsteps(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.FOOTSTEPS.get(), player.getBoundingBox().inflate(32.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findSpawnPositionNearPlayer(level, player.blockPosition(), 4, 12, 40);
        if (spawnPos == null) {
            return false;
        }

        FootstepsEntity entity = ModEntities.FOOTSTEPS.get().create(level);
        if (entity == null) {
            return false;
        }

        RandomSource random = level.random;
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        entity.setTarget(player);
        level.addFreshEntity(entity);
        return true;
    }

    public static boolean trySpawnMineshaftEfbui(ServerLevel level, ServerPlayer player) {
        if (!level.getEntities(ModEntities.MINESHAFT_EFBUI.get(), player.getBoundingBox().inflate(64.0D), entity -> true).isEmpty()) {
            return false;
        }

        BlockPos spawnPos = findMineshaftSpawnPosition(level, player);
        if (spawnPos == null) {
            return false;
        }

        MineshaftEfbuiEntity entity = ModEntities.MINESHAFT_EFBUI.get().create(level);
        if (entity == null) {
            return false;
        }

        RandomSource random = level.random;
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        entity.setTarget(player);
        level.addFreshEntity(entity);
        HorrorSoundManager.playMineshaftEfbui(player);
        return true;
    }

    private static BlockPos findSpawnPositionNearPlayer(ServerLevel level, ServerPlayer player) {
        return findSpawnPositionNearPlayer(level, player.blockPosition(), 4, 14, 50);
    }

    private static BlockPos findSpawnPositionNearPlayer(ServerLevel level, BlockPos centerPos, int minDistance, int maxDistance, int attempts) {
        RandomSource random = level.random;

        for (int attempt = 0; attempt < attempts; attempt++) {
            double angle = random.nextDouble() * (Math.PI * 2.0D);
            int distance = minDistance + random.nextInt(maxDistance - minDistance + 1);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            int offsetY = random.nextInt(7) - 3;
            BlockPos basePos = centerPos.offset(offsetX, offsetY, offsetZ);

            for (int yShift = 4; yShift >= -6; yShift--) {
                BlockPos spawnPos = basePos.offset(0, yShift, 0);
                BlockPos belowPos = spawnPos.below();

                if (!canSoftOccupy(level, spawnPos) || !canSoftOccupy(level, spawnPos.above())) {
                    continue;
                }

                if (!level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP)) {
                    continue;
                }

                AABB spawnBox = new AABB(
                        spawnPos.getX() + 0.2D,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.2D,
                        spawnPos.getX() + 0.8D,
                        spawnPos.getY() + 1.95D,
                        spawnPos.getZ() + 0.8D
                );

                if (!level.noCollision(spawnBox)) {
                    continue;
                }

                clearSoftBlock(level, spawnPos);
                clearSoftBlock(level, spawnPos.above());
                return spawnPos;
            }
        }

        return fallbackGroundSpawn(level, centerPos);
    }

    public static boolean spawnObserverAt(ServerLevel level, ServerPlayer player, BlockPos spawnPos) {
        if (spawnPos == null) {
            return false;
        }

        ObserverEntity entity = ModEntities.OBSERVER.get().create(level);
        if (entity == null) {
            return false;
        }

        Vec3 lookTarget = player.position().subtract(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
        float yaw = (float) (Math.toDegrees(Math.atan2(lookTarget.z, lookTarget.x)) - 90.0D);
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, yaw, 0.0F);
        entity.setTarget(player);
        level.addFreshEntity(entity);
        return true;
    }

    public static BlockPos findVisibleObserverSpawnPosition(ServerLevel level, ServerPlayer player) {
        RandomSource random = level.random;
        BlockPos centerPos = player.blockPosition();

        for (int attempt = 0; attempt < 72; attempt++) {
            double angle = random.nextDouble() * (Math.PI * 2.0D);
            int distance = 20 + random.nextInt(31);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            int offsetY = random.nextInt(5) - 2;
            BlockPos basePos = centerPos.offset(offsetX, offsetY, offsetZ);

            for (int yShift = 4; yShift >= -6; yShift--) {
                BlockPos spawnPos = basePos.offset(0, yShift, 0);
                BlockPos belowPos = spawnPos.below();

                if (!canSoftOccupy(level, spawnPos) || !canSoftOccupy(level, spawnPos.above())) {
                    continue;
                }

                if (!level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP)) {
                    continue;
                }

                AABB spawnBox = new AABB(
                        spawnPos.getX() + 0.2D,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.2D,
                        spawnPos.getX() + 0.8D,
                        spawnPos.getY() + 1.95D,
                        spawnPos.getZ() + 0.8D
                );

                if (!level.noCollision(spawnBox)) {
                    continue;
                }

                HitResult hitResult = level.clip(new ClipContext(
                        player.getEyePosition(),
                        new Vec3(spawnPos.getX() + 0.5D, spawnPos.getY() + 1.6D, spawnPos.getZ() + 0.5D),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        player
                ));

                if (hitResult.getType() == HitResult.Type.MISS) {
                    clearSoftBlock(level, spawnPos);
                    clearSoftBlock(level, spawnPos.above());
                    return spawnPos;
                }
            }
        }

        return findSpawnPositionNearPlayer(level, centerPos, 20, 40, 36);
    }

    private static BlockPos findVisibleStaticSpawnPosition(ServerLevel level, ServerPlayer player, int minDistance, int maxDistance, int attempts) {
        RandomSource random = level.random;
        BlockPos centerPos = player.blockPosition();

        for (int attempt = 0; attempt < attempts; attempt++) {
            double angle = random.nextDouble() * (Math.PI * 2.0D);
            int distance = minDistance + random.nextInt(maxDistance - minDistance + 1);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            int offsetY = random.nextInt(5) - 2;
            BlockPos basePos = centerPos.offset(offsetX, offsetY, offsetZ);

            for (int yShift = 4; yShift >= -6; yShift--) {
                BlockPos spawnPos = basePos.offset(0, yShift, 0);
                BlockPos belowPos = spawnPos.below();

                if (!canSoftOccupy(level, spawnPos) || !canSoftOccupy(level, spawnPos.above())) {
                    continue;
                }

                if (!level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP)) {
                    continue;
                }

                AABB spawnBox = new AABB(
                        spawnPos.getX() + 0.1D,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.1D,
                        spawnPos.getX() + 0.9D,
                        spawnPos.getY() + 4.8D,
                        spawnPos.getZ() + 0.9D
                );

                if (!level.noCollision(spawnBox)) {
                    continue;
                }

                HitResult hitResult = level.clip(new ClipContext(
                        player.getEyePosition(),
                        new Vec3(spawnPos.getX() + 0.5D, spawnPos.getY() + 2.2D, spawnPos.getZ() + 0.5D),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        player
                ));

                if (hitResult.getType() == HitResult.Type.MISS) {
                    clearSoftBlock(level, spawnPos);
                    clearSoftBlock(level, spawnPos.above());
                    return spawnPos;
                }
            }
        }

        return findSpawnPositionNearPlayer(level, centerPos, minDistance, maxDistance, attempts / 2);
    }

    private static BlockPos findTunnelObserverSpawnPosition(ServerLevel level, ServerPlayer player) {
        BlockPos playerPos = player.blockPosition();
        if (playerPos.getY() >= 50) {
            return null;
        }

        for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            if (!isLongTunnel(level, playerPos, direction)) {
                continue;
            }

            int distance = 9 + level.random.nextInt(8);
            BlockPos spawnPos = playerPos.relative(direction, distance);

            if (isObserverSpawnClear(level, spawnPos)) {
                return spawnPos;
            }
        }

        return null;
    }

    private static boolean isLongTunnel(ServerLevel level, BlockPos startPos, Direction direction) {
        int openSegments = 0;

        for (int i = 2; i <= 14; i++) {
            BlockPos pos = startPos.relative(direction, i);
            if (!level.isEmptyBlock(pos) || !level.isEmptyBlock(pos.above())) {
                break;
            }

            BlockPos left = pos.relative(direction.getClockWise());
            BlockPos right = pos.relative(direction.getCounterClockWise());
            boolean sideWalls = !level.isEmptyBlock(left) || !level.isEmptyBlock(left.above())
                    || !level.isEmptyBlock(right) || !level.isEmptyBlock(right.above());
            boolean solidFloor = level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);

            if (sideWalls && solidFloor) {
                openSegments++;
            }
        }

        if (openSegments >= 8) {
            return true;
        }

        for (int i = 1; i <= 8; i++) {
            BlockPos pos = startPos.relative(direction, i);
            if (level.isEmptyBlock(pos) && level.isEmptyBlock(pos.above()) && level.isEmptyBlock(pos.below())) {
                return true;
            }
        }

        return false;
    }

    private static boolean isObserverSpawnClear(ServerLevel level, BlockPos spawnPos) {
        if (!canSoftOccupy(level, spawnPos) || !canSoftOccupy(level, spawnPos.above())) {
            return false;
        }

        if (!level.getBlockState(spawnPos.below()).isFaceSturdy(level, spawnPos.below(), Direction.UP)) {
            return false;
        }

        AABB spawnBox = new AABB(
                spawnPos.getX() + 0.2D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.2D,
                spawnPos.getX() + 0.8D,
                spawnPos.getY() + 1.95D,
                spawnPos.getZ() + 0.8D
        );

        return level.noCollision(spawnBox);
    }

    private static BlockPos findMineshaftSpawnPosition(ServerLevel level, ServerPlayer player) {
        RandomSource random = level.random;
        BlockPos centerPos = player.blockPosition();

        for (int attempt = 0; attempt < 60; attempt++) {
            double angle = random.nextDouble() * (Math.PI * 2.0D);
            int distance = 20 + random.nextInt(11);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            int offsetY = random.nextInt(5) - 2;
            BlockPos basePos = centerPos.offset(offsetX, offsetY, offsetZ);

            for (int yShift = 3; yShift >= -3; yShift--) {
                BlockPos spawnPos = basePos.offset(0, yShift, 0);
                BlockPos belowPos = spawnPos.below();

                if (!canSoftOccupy(level, spawnPos) || !canSoftOccupy(level, spawnPos.above())) {
                    continue;
                }

                if (!level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP)) {
                    continue;
                }

                if (level.canSeeSky(spawnPos)) {
                    continue;
                }

                AABB spawnBox = new AABB(
                        spawnPos.getX() + 0.2D,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.2D,
                        spawnPos.getX() + 0.8D,
                        spawnPos.getY() + 1.95D,
                        spawnPos.getZ() + 0.8D
                );

                if (!level.noCollision(spawnBox)) {
                    continue;
                }

                clearSoftBlock(level, spawnPos);
                clearSoftBlock(level, spawnPos.above());
                return spawnPos;
            }
        }

        return findSpawnPositionNearPlayer(level, centerPos, 2, 2, 18);
    }

    public static BlockPos findNearestDoor(ServerLevel level, BlockPos centerPos, int radius) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos bestPos = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    cursor.set(centerPos.getX() + x, centerPos.getY() + y, centerPos.getZ() + z);
                    if (!(level.getBlockState(cursor).getBlock() instanceof DoorBlock)) {
                        continue;
                    }

                    BlockPos lowerPos = DoorOpenerEntity.resolveLowerDoorPos(level, cursor);
                    if (lowerPos == null) {
                        continue;
                    }

                    double distance = lowerPos.distSqr(centerPos);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestPos = lowerPos.immutable();
                    }
                }
            }
        }

        return bestPos;
    }

    private static BlockPos findDryLandSpawnPositionNearPlayer(ServerLevel level, BlockPos centerPos, int minDistance, int maxDistance, int attempts) {
        RandomSource random = level.random;

        for (int attempt = 0; attempt < attempts; attempt++) {
            double angle = random.nextDouble() * (Math.PI * 2.0D);
            int distance = minDistance + random.nextInt(maxDistance - minDistance + 1);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            int offsetY = random.nextInt(7) - 3;
            BlockPos basePos = centerPos.offset(offsetX, offsetY, offsetZ);

            for (int yShift = 5; yShift >= -7; yShift--) {
                BlockPos spawnPos = basePos.offset(0, yShift, 0);
                BlockPos belowPos = spawnPos.below();

                if (!canSoftOccupy(level, spawnPos) || !canSoftOccupy(level, spawnPos.above())) {
                    continue;
                }

                if (!level.getFluidState(spawnPos).isEmpty() || !level.getFluidState(spawnPos.above()).isEmpty()) {
                    continue;
                }

                if (!level.getFluidState(belowPos).isEmpty()) {
                    continue;
                }

                if (!level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP)) {
                    continue;
                }

                AABB spawnBox = new AABB(
                        spawnPos.getX() + 0.2D,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.2D,
                        spawnPos.getX() + 0.8D,
                        spawnPos.getY() + 1.95D,
                        spawnPos.getZ() + 0.8D
                );

                if (!level.noCollision(spawnBox)) {
                    continue;
                }

                clearSoftBlock(level, spawnPos);
                clearSoftBlock(level, spawnPos.above());
                return spawnPos;
            }
        }

        return null;
    }

    private static boolean canSoftOccupy(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }

    private static BlockPos fallbackGroundSpawn(ServerLevel level, BlockPos centerPos) {
        BlockPos surface = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                centerPos
        ).above();
        clearSoftBlock(level, surface);
        clearSoftBlock(level, surface.above());
        return surface;
    }

    private static void clearSoftBlock(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.isAir() && state.canBeReplaced()) {
            level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        }
    }
}
