package com.igore.code44.effect;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.ObserverEntity;
import com.igore.code44.registry.ModEntities;
import com.igore.code44.registry.ModBlocks;
import com.igore.code44.sound.HorrorSoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public final class TunnelDimensionManager {
    private static final ResourceKey<Level> TUNNEL_DIMENSION = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "tunnel_void")
    );

    private static final String ACTIVE_KEY = "code44TunnelDimensionActive";
    private static final String RETURN_TICK_KEY = "code44TunnelDimensionReturnTick";
    private static final String RETURN_X_KEY = "code44TunnelDimensionReturnX";
    private static final String RETURN_Y_KEY = "code44TunnelDimensionReturnY";
    private static final String RETURN_Z_KEY = "code44TunnelDimensionReturnZ";
    private static final String RETURN_YAW_KEY = "code44TunnelDimensionReturnYaw";
    private static final String RETURN_PITCH_KEY = "code44TunnelDimensionReturnPitch";
    private static final String RETURN_DIMENSION_KEY = "code44TunnelDimensionReturnDimension";
    private static final String NEXT_SPAWN_TICK_KEY = "code44TunnelDimensionNextSpawnTick";
    private static final String OBSERVER_SPAWNED_KEY = "code44TunnelDimensionObserverSpawned";
    private static final String LAST_TRIGGER_TICK_KEY = "code44TunnelDimensionLastTriggerTick";

    private static final int TOTAL_DURATION_TICKS = 20 * 180;
    private static final int FIRST_SPAWN_DELAY_TICKS = 20 * 60;
    private static final int RETRY_DELAY_TICKS = 100;
    private static final int TRIGGER_COOLDOWN_TICKS = 20 * 90;
    private static final int TUNNEL_HALF_LENGTH = 1536;
    private static final int SHELL_HALF_WIDTH = 24;
    private static final int SHELL_HALF_HEIGHT = 6;
    private static final int TUNNEL_Y = 80;

    private TunnelDimensionManager() {
    }

    public static boolean isSceneActive(ServerPlayer player) {
        return getModData(player).getBoolean(ACTIVE_KEY);
    }

    public static boolean tryTriggerFromOre(ServerLevel level, ServerPlayer player, BlockPos orePos) {
        CompoundTag data = getModData(player);
        if (isSceneActive(player)) {
            return false;
        }

        long now = level.getGameTime();
        if (now < data.getLong(LAST_TRIGGER_TICK_KEY)) {
            return false;
        }

        if (level.random.nextInt(100) >= 12) {
            return false;
        }

        boolean started = startScene(level, player);
        if (started) {
            data.putLong(LAST_TRIGGER_TICK_KEY, now + TRIGGER_COOLDOWN_TICKS);
        }
        return started;
    }

    public static boolean startScene(ServerLevel level, ServerPlayer player) {
        if (isSceneActive(player)) {
            return false;
        }

        ServerLevel tunnelLevel = level.getServer().getLevel(TUNNEL_DIMENSION);
        if (tunnelLevel == null) {
            return false;
        }

        BlockPos origin = getTunnelOrigin(player);
        prepareTunnel(tunnelLevel, origin);
        clearSceneEntities(tunnelLevel, origin);

        CompoundTag data = getModData(player);
        data.putBoolean(ACTIVE_KEY, true);
        data.putInt(RETURN_TICK_KEY, player.tickCount + TOTAL_DURATION_TICKS);
        data.putDouble(RETURN_X_KEY, player.getX());
        data.putDouble(RETURN_Y_KEY, player.getY());
        data.putDouble(RETURN_Z_KEY, player.getZ());
        data.putFloat(RETURN_YAW_KEY, player.getYRot());
        data.putFloat(RETURN_PITCH_KEY, player.getXRot());
        data.putString(RETURN_DIMENSION_KEY, level.dimension().location().toString());
        data.putInt(NEXT_SPAWN_TICK_KEY, player.tickCount + FIRST_SPAWN_DELAY_TICKS);
        data.putBoolean(OBSERVER_SPAWNED_KEY, false);

        player.teleportTo(tunnelLevel, origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D, 0.0F, 0.0F);
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;
        player.connection.resetPosition();
        return true;
    }

    public static void tick(ServerLevel level, ServerPlayer player) {
        if (!isSceneActive(player)) {
            return;
        }

        CompoundTag data = getModData(player);
        if (!player.isAlive()) {
            clearSceneData(player);
            return;
        }

        if (player.tickCount >= data.getInt(NEXT_SPAWN_TICK_KEY)) {
            boolean observerSpawned = data.getBoolean(OBSERVER_SPAWNED_KEY);

            if (!observerSpawned && spawnTunnelObserver(level, player)) {
                data.putBoolean(OBSERVER_SPAWNED_KEY, true);
                observerSpawned = true;
            }

            if (!observerSpawned) {
                data.putInt(NEXT_SPAWN_TICK_KEY, player.tickCount + RETRY_DELAY_TICKS);
            } else {
                data.putInt(NEXT_SPAWN_TICK_KEY, Integer.MAX_VALUE);
            }
        }

        if (player.tickCount >= data.getInt(RETURN_TICK_KEY)) {
            finishScene(player);
        }
    }

    public static void recoverPlayerIfNeeded(ServerPlayer player) {
        if (!isSceneActive(player)) {
            return;
        }

        if (player.isAlive()) {
            finishScene(player);
        } else {
            clearSceneData(player);
        }
    }

    public static void clearSceneData(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.remove(ACTIVE_KEY);
        data.remove(RETURN_TICK_KEY);
        data.remove(RETURN_X_KEY);
        data.remove(RETURN_Y_KEY);
        data.remove(RETURN_Z_KEY);
        data.remove(RETURN_YAW_KEY);
        data.remove(RETURN_PITCH_KEY);
        data.remove(RETURN_DIMENSION_KEY);
        data.remove(NEXT_SPAWN_TICK_KEY);
        data.remove(OBSERVER_SPAWNED_KEY);
    }

    private static void finishScene(ServerPlayer player) {
        CompoundTag data = getModData(player);
        ServerLevel returnLevel = resolveReturnLevel(player, data);
        double returnX = data.getDouble(RETURN_X_KEY);
        double returnY = data.getDouble(RETURN_Y_KEY);
        double returnZ = data.getDouble(RETURN_Z_KEY);
        float returnYaw = data.getFloat(RETURN_YAW_KEY);
        float returnPitch = data.getFloat(RETURN_PITCH_KEY);

        clearSceneEntities(player.serverLevel(), getTunnelOrigin(player));
        clearSceneData(player);

        if (returnLevel != null) {
            player.teleportTo(returnLevel, returnX, returnY, returnZ, returnYaw, returnPitch);
        }

        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;
        player.connection.resetPosition();
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

    private static void prepareTunnel(ServerLevel level, BlockPos origin) {
        int minZ = origin.getZ() - TUNNEL_HALF_LENGTH;
        int maxZ = origin.getZ() + TUNNEL_HALF_LENGTH;
        BlockState tunnelStone = ModBlocks.TUNNEL_STONE.get().defaultBlockState();

        for (int z = minZ; z <= maxZ; z++) {
            for (int x = -SHELL_HALF_WIDTH; x <= SHELL_HALF_WIDTH; x++) {
                level.setBlockAndUpdate(new BlockPos(origin.getX() + x, origin.getY() - 2, z), tunnelStone);
                level.setBlockAndUpdate(new BlockPos(origin.getX() + x, origin.getY() - 1, z), tunnelStone);
                level.setBlockAndUpdate(new BlockPos(origin.getX() + x, origin.getY() + 2, z), tunnelStone);
                level.setBlockAndUpdate(new BlockPos(origin.getX() + x, origin.getY() + 3, z), tunnelStone);
            }

            for (int y = -2; y <= 3; y++) {
                level.setBlockAndUpdate(new BlockPos(origin.getX() - 1, origin.getY() + y, z), tunnelStone);
                level.setBlockAndUpdate(new BlockPos(origin.getX() + 1, origin.getY() + y, z), tunnelStone);
            }

            level.setBlockAndUpdate(new BlockPos(origin.getX(), origin.getY(), z), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(new BlockPos(origin.getX(), origin.getY() + 1, z), Blocks.AIR.defaultBlockState());

            if ((Math.abs(z - origin.getZ()) % 10) == 0 && z != origin.getZ()) {
                BlockPos torchPos = new BlockPos(origin.getX() - 1, origin.getY() + 1, z);
                BlockState torchState = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST);
                level.setBlockAndUpdate(torchPos, torchState);
            }
        }
    }

    private static boolean spawnTunnelObserver(ServerLevel level, ServerPlayer player) {
        ObserverEntity entity = ModEntities.OBSERVER.get().create(level);
        if (entity == null) {
            return false;
        }

        HorrorSoundManager.playTunnelObserverSpawn(player);
        BlockPos spawnPos = player.blockPosition().offset(0, 0, 18 + level.random.nextInt(12));
        entity.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, 180.0F, 0.0F);
        if (entity.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) != null) {
            entity.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(0.22D);
        }
        entity.setTarget(player);
        level.addFreshEntity(entity);
        return true;
    }

    private static void clearSceneEntities(ServerLevel level, BlockPos origin) {
        AABB bounds = new AABB(
                origin.getX() - SHELL_HALF_WIDTH,
                origin.getY() - SHELL_HALF_HEIGHT,
                origin.getZ() - TUNNEL_HALF_LENGTH,
                origin.getX() + SHELL_HALF_WIDTH + 1,
                origin.getY() + SHELL_HALF_HEIGHT + 1,
                origin.getZ() + TUNNEL_HALF_LENGTH + 1
        );

        for (Entity entity : level.getEntitiesOfClass(Entity.class, bounds, entity -> !(entity instanceof ServerPlayer))) {
            entity.discard();
        }
    }

    private static BlockPos getTunnelOrigin(ServerPlayer player) {
        UUID uuid = player.getUUID();
        long mix = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
        int x = (int) Math.floorMod(mix, 200_000L);
        int z = (int) Math.floorMod(mix >>> 20, 200_000L);
        return new BlockPos(x, TUNNEL_Y, z);
    }

    private static CompoundTag getModData(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains(FearManager.MOD_DATA_KEY)) {
            persistentData.put(FearManager.MOD_DATA_KEY, new CompoundTag());
        }
        return persistentData.getCompound(FearManager.MOD_DATA_KEY);
    }
}
