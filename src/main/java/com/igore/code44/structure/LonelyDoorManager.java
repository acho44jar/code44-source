package com.igore.code44.structure;

import com.igore.code44.Code44Mod;
import com.igore.code44.sound.HorrorSoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class LonelyDoorManager {
    private static final ResourceKey<Level> LONELY_VOID = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "lonely_void")
    );
    private static final Map<UUID, DoorPlacement> ACTIVE_DOORS = new HashMap<>();
    private static final Map<UUID, FallingScene> ACTIVE_SCENES = new HashMap<>();
    private static final Random RANDOM = new Random();
    private static final double SCENE_X = 0.0D;
    private static final double SCENE_Z = 0.0D;
    private static final double SCENE_Y = 444444.0D;
    private static final int SCENE_DURATION_TICKS = 400;
    private static final int HUM_INTERVAL_TICKS = 110;

    private LonelyDoorManager() {
    }

    public static boolean tryPlaceLonelyDoor(ServerLevel level, ServerPlayer player) {
        if (ACTIVE_DOORS.containsKey(player.getUUID()) || ACTIVE_SCENES.containsKey(player.getUUID())) {
            return false;
        }

        BlockPos doorPos = findDoorPosition(level, player);
        if (doorPos == null) {
            return false;
        }

        Direction facing = resolveFacingFromPlayer(player, doorPos);
        BlockState lower = Blocks.OAK_DOOR.defaultBlockState()
                .setValue(DoorBlock.FACING, facing)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER);
        BlockState upper = lower.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);

        level.setBlockAndUpdate(doorPos, lower);
        level.setBlockAndUpdate(doorPos.above(), upper);
        ACTIVE_DOORS.put(player.getUUID(), new DoorPlacement(level.dimension(), doorPos.immutable()));
        return true;
    }

    public static void tick(ServerLevel level, ServerPlayer player) {
        FallingScene scene = ACTIVE_SCENES.get(player.getUUID());
        if (scene != null) {
            tickScene(player, scene);
            return;
        }

        DoorPlacement placement = ACTIVE_DOORS.get(player.getUUID());
        if (placement == null || placement.dimension() != level.dimension()) {
            return;
        }

        BlockPos lower = placement.lowerDoorPos();
        if (!(level.getBlockState(lower).getBlock() instanceof DoorBlock)) {
            ACTIVE_DOORS.remove(player.getUUID());
            return;
        }

        if (player.position().distanceTo(Vec3.atCenterOf(lower)) <= 2.15D) {
            startScene(level, player, lower);
        }
    }

    public static boolean isSceneActive(ServerPlayer player) {
        return ACTIVE_SCENES.containsKey(player.getUUID());
    }

    public static boolean isManagedDoor(ServerLevel level, BlockPos pos) {
        for (DoorPlacement placement : ACTIVE_DOORS.values()) {
            if (placement.dimension() == level.dimension()
                    && (placement.lowerDoorPos().equals(pos) || placement.lowerDoorPos().above().equals(pos))) {
                return true;
            }
        }
        return false;
    }

    public static void clearSceneData(ServerPlayer player) {
        ACTIVE_SCENES.remove(player.getUUID());
        ACTIVE_DOORS.remove(player.getUUID());
    }

    public static void recoverPlayerIfNeeded(ServerPlayer player) {
        FallingScene scene = ACTIVE_SCENES.remove(player.getUUID());
        if (scene == null) {
            return;
        }

        ServerLevel returnLevel = player.server.getLevel(scene.returnDimension());
        if (returnLevel != null) {
            player.teleportTo(returnLevel, scene.returnX(), scene.returnY(), scene.returnZ(), scene.returnYaw(), scene.returnPitch());
            placeAftermathSign(returnLevel, scene.doorPos());
        }
    }

    private static void startScene(ServerLevel level, ServerPlayer player, BlockPos lowerDoorPos) {
        ServerLevel lonelyLevel = level.getServer().getLevel(LONELY_VOID);
        if (lonelyLevel == null) {
            return;
        }

        ACTIVE_DOORS.remove(player.getUUID());
        ACTIVE_SCENES.put(
                player.getUUID(),
                new FallingScene(
                        level.dimension(),
                        lowerDoorPos.immutable(),
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        player.getYRot(),
                        player.getXRot(),
                        player.tickCount + SCENE_DURATION_TICKS,
                        player.tickCount + 10
                )
        );

        player.teleportTo(lonelyLevel, SCENE_X, SCENE_Y, SCENE_Z, player.getYRot(), player.getXRot());
        player.setNoGravity(false);
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;
        player.connection.resetPosition();
    }

    private static void tickScene(ServerPlayer player, FallingScene scene) {
        if (!player.isAlive()) {
            finishScene(player, scene);
            return;
        }

        if (player.tickCount >= scene.nextHumTick()) {
            HorrorSoundManager.playLonelyDoorVoidHum(player);
            ACTIVE_SCENES.put(player.getUUID(), scene.withNextHumTick(player.tickCount + HUM_INTERVAL_TICKS));
            scene = ACTIVE_SCENES.get(player.getUUID());
        }

        if (player.tickCount >= scene.returnTick()) {
            finishScene(player, scene);
            return;
        }

    }

    private static void finishScene(ServerPlayer player, FallingScene scene) {
        ACTIVE_SCENES.remove(player.getUUID());
        ServerLevel returnLevel = player.server.getLevel(scene.returnDimension());
        if (returnLevel == null) {
            returnLevel = player.server.getLevel(Level.OVERWORLD);
        }

        if (returnLevel == null) {
            return;
        }

        player.teleportTo(returnLevel, scene.returnX(), scene.returnY(), scene.returnZ(), scene.returnYaw(), scene.returnPitch());
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;
        player.connection.resetPosition();
        placeAftermathSign(returnLevel, scene.doorPos());
    }

    private static void placeAftermathSign(ServerLevel level, BlockPos doorPos) {
        level.setBlockAndUpdate(doorPos.above(), Blocks.AIR.defaultBlockState());
        level.setBlock(
                doorPos,
                Blocks.OAK_SIGN.defaultBlockState().setValue(StandingSignBlock.ROTATION, 0),
                3
        );

        BlockEntity blockEntity = level.getBlockEntity(doorPos);
        if (blockEntity instanceof SignBlockEntity signBlockEntity) {
            SignText text = signBlockEntity.getFrontText()
                    .setMessage(0, Component.literal("they suffered too"));
            signBlockEntity.setText(text, true);
            signBlockEntity.setWaxed(true);
            signBlockEntity.setChanged();
        }
    }

    private static BlockPos findDoorPosition(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();

        for (int attempt = 0; attempt < 36; attempt++) {
            double angle = RANDOM.nextDouble() * (Math.PI * 2.0D);
            int distance = 18 + RANDOM.nextInt(13);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            BlockPos basePos = center.offset(offsetX, 0, offsetZ);

            for (int yShift = 8; yShift >= -10; yShift--) {
                BlockPos checkPos = basePos.offset(0, yShift, 0);
                BlockPos groundPos = checkPos.below();

                if (!level.getBlockState(groundPos).isFaceSturdy(level, groundPos, Direction.UP)) {
                    continue;
                }

                level.setBlockAndUpdate(checkPos, Blocks.AIR.defaultBlockState());
                level.setBlockAndUpdate(checkPos.above(), Blocks.AIR.defaultBlockState());
                clearSoft(level, checkPos);
                clearSoft(level, checkPos.above());
                return checkPos;
            }
        }

        BlockPos fallbackBase = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                center.offset(24, 0, 0)
        ).above();
        clearSoft(level, fallbackBase);
        clearSoft(level, fallbackBase.above());
        return fallbackBase;
    }

    private static Direction resolveFacingFromPlayer(ServerPlayer player, BlockPos doorPos) {
        int dx = player.blockPosition().getX() - doorPos.getX();
        int dz = player.blockPosition().getZ() - doorPos.getZ();

        if (Math.abs(dx) > Math.abs(dz)) {
            return dx >= 0 ? Direction.EAST : Direction.WEST;
        }

        return dz >= 0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static void clearSoft(ServerLevel level, BlockPos pos) {
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    private record DoorPlacement(ResourceKey<Level> dimension, BlockPos lowerDoorPos) {
    }

    private record FallingScene(
            ResourceKey<Level> returnDimension,
            BlockPos doorPos,
            double returnX,
            double returnY,
            double returnZ,
            float returnYaw,
            float returnPitch,
            int returnTick,
            int nextHumTick
    ) {
        private FallingScene withNextHumTick(int newNextHumTick) {
            return new FallingScene(returnDimension, doorPos, returnX, returnY, returnZ, returnYaw, returnPitch, returnTick, newNextHumTick);
        }
    }
}
