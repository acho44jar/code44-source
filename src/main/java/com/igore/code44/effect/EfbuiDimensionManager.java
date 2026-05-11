package com.igore.code44.effect;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.E44efbuiEntity;
import com.igore.code44.network.ModNetworking;
import com.igore.code44.network.packet.SceneCameraPacket;
import com.igore.code44.registry.ModEntities;
import com.igore.code44.registry.ModBlocks;
import com.igore.code44.sound.HorrorSoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.UUID;
import net.minecraftforge.network.PacketDistributor;

public final class EfbuiDimensionManager {
    private static final String DELAY_KEY = "code44EfbuiDimensionDelay";
    private static final String TRIGGERED_KEY = "code44EfbuiDimensionTriggered";
    private static final String ACTIVE_KEY = "code44EfbuiDimensionActive";
    private static final String SPAWN_TICK_KEY = "code44EfbuiDimensionSpawnTick";
    private static final String RETURN_TICK_KEY = "code44EfbuiDimensionReturnTick";
    private static final String RETURN_X_KEY = "code44EfbuiDimensionReturnX";
    private static final String RETURN_Y_KEY = "code44EfbuiDimensionReturnY";
    private static final String RETURN_Z_KEY = "code44EfbuiDimensionReturnZ";
    private static final String RETURN_YAW_KEY = "code44EfbuiDimensionReturnYaw";
    private static final String RETURN_PITCH_KEY = "code44EfbuiDimensionReturnPitch";
    private static final String RETURN_DIMENSION_KEY = "code44EfbuiDimensionReturnDimension";
    private static final String ANCHOR_X_KEY = "code44EfbuiDimensionAnchorX";
    private static final String ANCHOR_Y_KEY = "code44EfbuiDimensionAnchorY";
    private static final String ANCHOR_Z_KEY = "code44EfbuiDimensionAnchorZ";
    private static final String EFBUI_UUID_KEY = "code44EfbuiDimensionEntityUuid";
    private static final String NEXT_HUM_TICK_KEY = "code44EfbuiDimensionNextHumTick";
    private static final String MESSAGE_TICK_KEY = "code44EfbuiDimensionMessageTick";
    private static final String MESSAGE_SENT_KEY = "code44EfbuiDimensionMessageSent";
    private static final int ROOM_RADIUS = 48;
    private static final int ROOM_HEIGHT = 24;
    private static final int EMPTY_DELAY_TICKS = 20;
    private static final int TOTAL_DURATION_TICKS = 600;
    private static final ResourceKey<Level> EFBUI_DIMENSION = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "efbui_void")
    );
    private static final Random RANDOM = new Random();

    private EfbuiDimensionManager() {
    }

    public static void initializeSession(ServerPlayer player) {
        CompoundTag data = getModData(player);

        if (!data.contains(TRIGGERED_KEY)) {
            data.putBoolean(TRIGGERED_KEY, false);
        }

        data.remove(DELAY_KEY);
    }

    public static boolean shouldTrigger(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);

        if (!FearManager.isEfbuiDimensionUnlocked(level)) {
            return false;
        }

        if (data.getBoolean(TRIGGERED_KEY) || data.getBoolean(ACTIVE_KEY)) {
            return false;
        }

        long triggerTick = ensureDayWindowSchedule(level, data);
        return level.getDayTime() >= triggerTick;
    }

    public static void deferTriggerAfterNightJump(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);
        long scheduledTick = getStoredTime(data, DELAY_KEY);
        long currentDayStart = (level.getDayTime() / 24000L) * 24000L;
        long nextDayStart = currentDayStart + 24000L;

        if (data.getBoolean(TRIGGERED_KEY) || data.getBoolean(ACTIVE_KEY)) {
            return;
        }

        if (scheduledTick <= 0L || scheduledTick < currentDayStart || scheduledTick >= nextDayStart) {
            return;
        }

        data.putLong(DELAY_KEY, nextDayStart + 11000L + RANDOM.nextInt(4001));
    }

    public static void startScene(ServerLevel level, ServerPlayer player) {
        ServerLevel efbuiLevel = level.getServer().getLevel(EFBUI_DIMENSION);

        if (efbuiLevel == null) {
            return;
        }

        CompoundTag data = getModData(player);
        BlockPos roomCenter = getRoomCenter(player);
        double anchorX = roomCenter.getX() + 0.5D;
        double anchorY = roomCenter.getY() + 1.0D;
        double anchorZ = roomCenter.getZ() + 0.5D;

        prepareRoom(efbuiLevel, roomCenter);
        clearSceneEntities(efbuiLevel, roomCenter);

        data.putBoolean(TRIGGERED_KEY, true);
        data.putBoolean(ACTIVE_KEY, true);
        data.putInt(SPAWN_TICK_KEY, player.tickCount + EMPTY_DELAY_TICKS);
        data.putInt(RETURN_TICK_KEY, player.tickCount + TOTAL_DURATION_TICKS);
        data.putDouble(RETURN_X_KEY, player.getX());
        data.putDouble(RETURN_Y_KEY, player.getY());
        data.putDouble(RETURN_Z_KEY, player.getZ());
        data.putFloat(RETURN_YAW_KEY, player.getYRot());
        data.putFloat(RETURN_PITCH_KEY, player.getXRot());
        data.putString(RETURN_DIMENSION_KEY, level.dimension().location().toString());
        data.putDouble(ANCHOR_X_KEY, anchorX);
        data.putDouble(ANCHOR_Y_KEY, anchorY);
        data.putDouble(ANCHOR_Z_KEY, anchorZ);
        data.putInt(NEXT_HUM_TICK_KEY, player.tickCount + 10);
        data.putInt(MESSAGE_TICK_KEY, player.tickCount + 200);
        data.putBoolean(MESSAGE_SENT_KEY, false);
        data.remove(EFBUI_UUID_KEY);

        player.teleportTo(efbuiLevel, anchorX, anchorY, anchorZ, player.getYRot(), player.getXRot());
        player.setDeltaMovement(Vec3.ZERO);
        player.setNoGravity(true);
        player.fallDistance = 0.0F;
        player.connection.resetPosition();
    }

    public static boolean isSceneActive(ServerPlayer player) {
        return getModData(player).getBoolean(ACTIVE_KEY);
    }

    public static void tick(ServerLevel level, ServerPlayer player) {
        CompoundTag data = getModData(player);

        if (!data.getBoolean(ACTIVE_KEY)) {
            return;
        }

        freezePlayer(player, data);

        if (player.tickCount >= data.getInt(NEXT_HUM_TICK_KEY)) {
            HorrorSoundManager.playEfbuiDimensionHum(player);
            data.putInt(NEXT_HUM_TICK_KEY, player.tickCount + 90);
        }

        if (!data.contains(EFBUI_UUID_KEY) && player.tickCount >= data.getInt(SPAWN_TICK_KEY)) {
            spawnWatcher(level, player, data);
        }

        E44efbuiEntity efbui = getWatcher(level, data);
        if (efbui != null) {
            lookAtPlayer(efbui, player);
        }

        if (!data.getBoolean(MESSAGE_SENT_KEY) && player.tickCount >= data.getInt(MESSAGE_TICK_KEY)) {
            player.sendSystemMessage(Component.literal("What is the meaning of your life?"));
            data.putBoolean(MESSAGE_SENT_KEY, true);
        }

        if (!player.isAlive() || player.tickCount >= data.getInt(RETURN_TICK_KEY)) {
            finishScene(player);
        }
    }

    public static void recoverPlayerIfNeeded(ServerPlayer player) {
        if (isSceneActive(player)) {
            finishScene(player);
        } else {
            clearSceneData(player);
        }
    }

    public static void clearSceneData(ServerPlayer player) {
        CompoundTag data = getModData(player);
        data.remove(ACTIVE_KEY);
        data.remove(SPAWN_TICK_KEY);
        data.remove(RETURN_TICK_KEY);
        data.remove(RETURN_X_KEY);
        data.remove(RETURN_Y_KEY);
        data.remove(RETURN_Z_KEY);
        data.remove(RETURN_YAW_KEY);
        data.remove(RETURN_PITCH_KEY);
        data.remove(RETURN_DIMENSION_KEY);
        data.remove(ANCHOR_X_KEY);
        data.remove(ANCHOR_Y_KEY);
        data.remove(ANCHOR_Z_KEY);
        data.remove(EFBUI_UUID_KEY);
        data.remove(NEXT_HUM_TICK_KEY);
        data.remove(MESSAGE_TICK_KEY);
        data.remove(MESSAGE_SENT_KEY);
    }

    private static void finishScene(ServerPlayer player) {
        CompoundTag data = getModData(player);
        ServerLevel currentLevel = player.serverLevel();
        E44efbuiEntity efbui = getWatcher(currentLevel, data);

        if (efbui != null) {
            efbui.discard();
        }

        ServerLevel returnLevel = resolveReturnLevel(player, data);
        double returnX = data.getDouble(RETURN_X_KEY);
        double returnY = data.getDouble(RETURN_Y_KEY);
        double returnZ = data.getDouble(RETURN_Z_KEY);
        float returnYaw = data.getFloat(RETURN_YAW_KEY);
        float returnPitch = data.getFloat(RETURN_PITCH_KEY);

        clearSceneData(player);
        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SceneCameraPacket(false, -1));

        if (returnLevel != null) {
            player.teleportTo(returnLevel, returnX, returnY, returnZ, returnYaw, returnPitch);
        } else {
            player.teleportTo(returnX, returnY, returnZ);
            player.setYRot(returnYaw);
            player.setXRot(returnPitch);
        }

        player.setDeltaMovement(Vec3.ZERO);
        player.setNoGravity(false);
        player.fallDistance = 0.0F;
        player.connection.resetPosition();
    }

    private static ServerLevel resolveReturnLevel(ServerPlayer player, CompoundTag data) {
        if (!data.contains(RETURN_DIMENSION_KEY)) {
            return player.server.getLevel(Level.OVERWORLD);
        }

        ResourceLocation location = ResourceLocation.tryParse(data.getString(RETURN_DIMENSION_KEY));
        if (location == null) {
            return player.server.getLevel(Level.OVERWORLD);
        }

        ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, location);
        ServerLevel level = player.server.getLevel(levelKey);
        return level != null ? level : player.server.getLevel(Level.OVERWORLD);
    }

    private static void freezePlayer(ServerPlayer player, CompoundTag data) {
        double anchorX = data.getDouble(ANCHOR_X_KEY);
        double anchorY = data.getDouble(ANCHOR_Y_KEY);
        double anchorZ = data.getDouble(ANCHOR_Z_KEY);
        double dx = player.getX() - anchorX;
        double dy = player.getY() - anchorY;
        double dz = player.getZ() - anchorZ;

        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;
        player.setSprinting(false);
        player.setNoGravity(true);
        player.hurtMarked = true;

        if ((dx * dx) + (dy * dy) + (dz * dz) > 0.01D) {
            player.teleportTo(anchorX, anchorY, anchorZ);
            player.connection.resetPosition();
        }
    }

    private static void spawnWatcher(ServerLevel level, ServerPlayer player, CompoundTag data) {
        E44efbuiEntity efbui = ModEntities.E44EFBUI.get().create(level);

        if (efbui == null) {
            return;
        }

        double anchorX = data.getDouble(ANCHOR_X_KEY);
        double anchorY = data.getDouble(ANCHOR_Y_KEY);
        double anchorZ = data.getDouble(ANCHOR_Z_KEY);

        efbui.setNoAi(true);
        efbui.setInvulnerable(true);
        efbui.setSilent(true);
        efbui.setScriptedScene(true);
        efbui.setCustomNameVisible(false);
        efbui.setNoGravity(true);
        efbui.moveTo(anchorX, anchorY + 4.0D, anchorZ - 4.0D, 180.0F, 0.0F);
        level.addFreshEntity(efbui);
        data.putUUID(EFBUI_UUID_KEY, efbui.getUUID());
        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SceneCameraPacket(true, efbui.getId()));
    }

    private static E44efbuiEntity getWatcher(ServerLevel level, CompoundTag data) {
        if (!data.hasUUID(EFBUI_UUID_KEY)) {
            return null;
        }

        Entity entity = level.getEntity(data.getUUID(EFBUI_UUID_KEY));
        return entity instanceof E44efbuiEntity efbui ? efbui : null;
    }

    private static void lookAtPlayer(E44efbuiEntity efbui, ServerPlayer player) {
        Vec3 delta = player.getEyePosition().subtract(efbui.getEyePosition());
        float yaw = (float) (Mth.atan2(delta.z, delta.x) * (180.0D / Math.PI)) - 90.0F;
        float pitch = (float) (-(Mth.atan2(delta.y, Math.sqrt((delta.x * delta.x) + (delta.z * delta.z))) * (180.0D / Math.PI)));

        efbui.setYRot(yaw);
        efbui.yBodyRot = yaw;
        efbui.yHeadRot = yaw;
        efbui.setYHeadRot(yaw);
        efbui.setXRot(pitch);
    }

    private static void clearSceneEntities(ServerLevel level, BlockPos center) {
        AABB bounds = new AABB(
                center.getX() - ROOM_RADIUS - 2,
                center.getY() - 8,
                center.getZ() - ROOM_RADIUS - 2,
                center.getX() + ROOM_RADIUS + 3,
                center.getY() + ROOM_HEIGHT + 4,
                center.getZ() + ROOM_RADIUS + 3
        );

        for (Entity entity : level.getEntitiesOfClass(Entity.class, bounds, entity -> !(entity instanceof ServerPlayer))) {
            entity.discard();
        }
    }

    private static void prepareRoom(ServerLevel level, BlockPos center) {
        for (int x = -ROOM_RADIUS; x <= ROOM_RADIUS; x++) {
            for (int y = -8; y <= ROOM_HEIGHT; y++) {
                for (int z = -ROOM_RADIUS; z <= ROOM_RADIUS; z++) {
                    level.setBlockAndUpdate(center.offset(x, y, z), Blocks.AIR.defaultBlockState());
                }
            }
        }

        level.setBlockAndUpdate(center.below(), ModBlocks.EFBUI_VOID_BLOCK.get().defaultBlockState());
    }

    private static BlockPos getRoomCenter(ServerPlayer player) {
        UUID uuid = player.getUUID();
        long mix = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
        int x = (int) Math.floorMod(mix, 200_000L);
        int z = (int) Math.floorMod(mix >>> 16, 200_000L);
        return new BlockPos(x, 80, z);
    }

    private static CompoundTag getModData(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();

        if (!persistentData.contains(FearManager.MOD_DATA_KEY)) {
            persistentData.put(FearManager.MOD_DATA_KEY, new CompoundTag());
        }

        return persistentData.getCompound(FearManager.MOD_DATA_KEY);
    }

    private static long ensureDayWindowSchedule(ServerLevel level, CompoundTag data) {
        long now = level.getDayTime();
        long currentDayStart = (level.getDayTime() / 24000L) * 24000L;
        long storedTick = getStoredTime(data, DELAY_KEY);

        if (storedTick <= 0L || storedTick < currentDayStart) {
            long candidate = currentDayStart + 11000L + RANDOM.nextInt(4001);
            if (candidate <= now + 100L) {
                candidate = currentDayStart + 24000L + 11000L + RANDOM.nextInt(4001);
            }
            data.putLong(DELAY_KEY, candidate);
            return candidate;
        }

        return storedTick;
    }

    private static long getStoredTime(CompoundTag data, String key) {
        if (data.contains(key, Tag.TAG_LONG)) {
            return data.getLong(key);
        }
        if (data.contains(key, Tag.TAG_INT)) {
            return data.getInt(key);
        }
        return 0L;
    }
}
