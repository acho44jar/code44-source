package com.igore.code44.effect;

import com.igore.code44.network.ModNetworking;
import com.igore.code44.network.packet.LateGameClientEffectPacket;
import com.igore.code44.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class LateGameEventManager {
    private static final int SCREEN_BLINK_DURATION_TICKS = 2;
    private static final int PHOTO_SCREAMER_DURATION_TICKS = 10;
    private static final Map<UUID, BlockRestoreData> CHUNK_DISTORTIONS = new HashMap<>();
    private static final Map<UUID, BlockRestoreData> HOME_REPLACEMENTS = new HashMap<>();

    private LateGameEventManager() {
    }

    public static void tick(ServerLevel level, ServerPlayer player) {
        restoreBlocksIfNeeded(level, player, HOME_REPLACEMENTS);
    }

    public static void triggerMinimizeWindow(ServerPlayer player) {
        ModNetworking.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new LateGameClientEffectPacket(LateGameClientEffectPacket.Type.MINIMIZE_WINDOW, 0)
        );
    }

    public static void triggerInvertMouse(ServerPlayer player) {
        ModNetworking.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new LateGameClientEffectPacket(LateGameClientEffectPacket.Type.INVERT_MOUSE, getRandomClientEffectDuration(player))
        );
    }

    public static void triggerSensitivitySpike(ServerPlayer player) {
        ModNetworking.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new LateGameClientEffectPacket(LateGameClientEffectPacket.Type.SENSITIVITY_SPIKE, getRandomClientEffectDuration(player))
        );
    }

    public static void triggerMasterVolumeDrop(ServerPlayer player) {
        ModNetworking.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new LateGameClientEffectPacket(LateGameClientEffectPacket.Type.MASTER_VOLUME_DROP, getRandomClientEffectDuration(player))
        );
    }

    public static void triggerScreenBlink(ServerPlayer player) {
        ModNetworking.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new LateGameClientEffectPacket(LateGameClientEffectPacket.Type.SCREEN_BLINK, SCREEN_BLINK_DURATION_TICKS)
        );
    }

    public static void triggerPhotoScreamer(ServerPlayer player) {
        ModNetworking.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new LateGameClientEffectPacket(LateGameClientEffectPacket.Type.PHOTO_SCREAMER, PHOTO_SCREAMER_DURATION_TICKS)
        );
    }

    public static void triggerLookDown(ServerPlayer player) {
        ModNetworking.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new LateGameClientEffectPacket(LateGameClientEffectPacket.Type.LOOK_DOWN, 0)
        );
    }

    public static boolean triggerInventoryDistortion(ServerPlayer player) {
        List<Integer> occupiedSlots = new ArrayList<>();
        List<ItemStack> shuffled = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                occupiedSlots.add(i);
                shuffled.add(stack.copy());
            }
        }

        if (occupiedSlots.isEmpty()) {
            return false;
        }

        if (occupiedSlots.size() == 1) {
            int sourceSlot = occupiedSlots.get(0);
            int targetSlot = sourceSlot;

            while (targetSlot == sourceSlot) {
                targetSlot = player.level().random.nextInt(player.getInventory().getContainerSize());
            }

            ItemStack sourceStack = player.getInventory().getItem(sourceSlot).copy();
            ItemStack targetStack = player.getInventory().getItem(targetSlot).copy();
            player.getInventory().setItem(sourceSlot, targetStack);
            player.getInventory().setItem(targetSlot, sourceStack);
            player.containerMenu.broadcastChanges();
            return true;
        }

        List<ItemStack> originalOrder = new ArrayList<>(shuffled);
        Random random = new Random(player.level().getGameTime() + player.getId());

        for (int attempt = 0; attempt < 5; attempt++) {
            Collections.shuffle(shuffled, random);
            if (!sameOrder(originalOrder, shuffled)) {
                break;
            }
        }

        for (int i = 0; i < occupiedSlots.size(); i++) {
            player.getInventory().setItem(occupiedSlots.get(i), shuffled.get(i));
        }

        player.containerMenu.broadcastChanges();
        return true;
    }

    public static boolean triggerChunkDistortion(ServerLevel level, ServerPlayer player, int chunkCount) {
        if (CHUNK_DISTORTIONS.containsKey(player.getUUID())) {
            return false;
        }

        List<ChunkPos> chunkTargets = collectChunkTargets(level, player, Math.max(1, Math.min(10, chunkCount)));
        if (chunkTargets.isEmpty()) {
            return false;
        }

        List<BlockSnapshot> snapshots = new ArrayList<>();
        int minBuildY = level.getMinBuildHeight();
        int targetBaseY = Math.max(player.blockPosition().getY() + 80, 180);

        for (ChunkPos chunkPos : chunkTargets) {
            int baseX = chunkPos.getMinBlockX();
            int baseZ = chunkPos.getMinBlockZ();
            int highestY = minBuildY;

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int worldX = baseX + x;
                    int worldZ = baseZ + z;
                    int columnTop = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, worldX, worldZ);
                    highestY = Math.max(highestY, Math.min(columnTop + 8, level.getMaxBuildHeight() - 1));
                }
            }

            int verticalOffset = targetBaseY - minBuildY;
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int worldX = baseX + x;
                    int worldZ = baseZ + z;
                    for (int y = minBuildY; y <= highestY; y++) {
                        BlockPos sourcePos = new BlockPos(worldX, y, worldZ);
                        BlockPos targetPos = new BlockPos(worldX, y + verticalOffset, worldZ);
                        snapshots.add(new BlockSnapshot(targetPos, level.getBlockState(targetPos)));
                        BlockState sourceState = level.getBlockState(sourcePos);
                        level.setBlockAndUpdate(targetPos, sourceState);
                    }
                }
            }
        }

        return true;
    }

    private static List<ChunkPos> collectChunkTargets(ServerLevel level, ServerPlayer player, int requestedCount) {
        List<ChunkPos> candidates = new ArrayList<>();
        ChunkPos playerChunk = player.chunkPosition();
        ServerChunkCache chunkSource = level.getChunkSource();

        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = -7; dz <= 7; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                ChunkPos chunkPos = new ChunkPos(playerChunk.x + dx, playerChunk.z + dz);
                double centerX = chunkPos.getMiddleBlockX();
                double centerZ = chunkPos.getMiddleBlockZ();
                double dist = Math.sqrt(player.distanceToSqr(centerX, player.getY(), centerZ));
                if (dist > 100.0D || dist < 32.0D) {
                    continue;
                }

                if (chunkSource.hasChunk(chunkPos.x, chunkPos.z)) {
                    candidates.add(chunkPos);
                }
            }
        }

        Collections.shuffle(candidates, new Random(level.getGameTime() ^ player.getUUID().getLeastSignificantBits()));
        if (candidates.size() > requestedCount) {
            return new ArrayList<>(candidates.subList(0, requestedCount));
        }
        return candidates;
    }

    public static boolean triggerHomeReplacement(ServerLevel level, ServerPlayer player) {
        if (HOME_REPLACEMENTS.containsKey(player.getUUID())) {
            return false;
        }

        BlockPos center = resolveHomeCenter(level, player);
        if (center == null) {
            return false;
        }

        List<BlockPos> householdBlocks = findBlocks(level, center, 10, state ->
                state.getBlock() instanceof FurnaceBlock
                        || state.getBlock() instanceof CraftingTableBlock
                        || state.getBlock() instanceof BedBlock
                        || state.getBlock() instanceof DoorBlock
                        || state.is(Blocks.CHEST)
                        || state.is(Blocks.BARREL)
                        || state.is(Blocks.SMOKER)
                        || state.is(Blocks.BLAST_FURNACE)
                        || state.is(Blocks.FLETCHING_TABLE)
                        || state.getBlock() instanceof TorchBlock
                        || state.is(Blocks.WALL_TORCH)
        );
        if (householdBlocks.isEmpty()) {
            return false;
        }

        Collections.shuffle(householdBlocks, new Random(level.getGameTime() ^ player.getUUID().getLeastSignificantBits()));
        int changes = Math.min(8, householdBlocks.size());
        boolean changedAny = false;

        for (int i = 0; i < changes; i++) {
            BlockPos sourcePos = householdBlocks.get(i);
            BlockState sourceState = level.getBlockState(sourcePos);
            if (sourceState.isAir() || sourceState.is(Blocks.BEDROCK)) {
                continue;
            }

            int mode = level.random.nextInt(3);
            if (mode == 0 && i + 1 < householdBlocks.size()) {
                BlockPos targetPos = householdBlocks.get(i + 1);
                BlockState targetState = level.getBlockState(targetPos);
                if (!targetState.isAir() && !targetState.is(Blocks.BEDROCK)) {
                    level.setBlockAndUpdate(sourcePos, targetState);
                    level.setBlockAndUpdate(targetPos, sourceState);
                    changedAny = true;
                }
            } else {
                int yShift = mode == 1 ? 1 : -1;
                BlockPos movedPos = sourcePos.offset(0, yShift, 0);
                if (level.getBlockState(movedPos).canBeReplaced()) {
                    level.setBlockAndUpdate(movedPos, sourceState);
                    level.setBlockAndUpdate(sourcePos, Blocks.AIR.defaultBlockState());
                    changedAny = true;
                }
            }
        }

        return changedAny;
    }

    private static BlockPos resolveHomeCenter(ServerLevel level, ServerPlayer player) {
        if (player.getRespawnPosition() != null && player.getRespawnDimension() == level.dimension()) {
            return player.getRespawnPosition();
        }

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos playerPos = player.blockPosition();
        for (int x = -16; x <= 16; x++) {
            for (int y = -6; y <= 6; y++) {
                for (int z = -16; z <= 16; z++) {
                    cursor.set(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                    if (level.getBlockState(cursor).getBlock() instanceof BedBlock) {
                        return cursor.immutable();
                    }
                }
            }
        }

        return null;
    }

    private static void restoreBlocksIfNeeded(ServerLevel level, ServerPlayer player, Map<UUID, BlockRestoreData> map) {
        BlockRestoreData data = map.get(player.getUUID());
        if (data == null || player.tickCount < data.restoreTick) {
            return;
        }

        for (BlockSnapshot snapshot : data.snapshots) {
            level.setBlockAndUpdate(snapshot.pos, snapshot.state);
        }

        map.remove(player.getUUID());
    }

    private static List<BlockPos> findBlocks(ServerLevel level, BlockPos center, int radius, java.util.function.Predicate<BlockState> predicate) {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    cursor.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (predicate.test(level.getBlockState(cursor))) {
                        positions.add(cursor.immutable());
                    }
                }
            }
        }

        return positions;
    }

    private static BlockPos findNearestBlock(ServerLevel level, BlockPos center, int radius, java.util.function.Predicate<BlockState> predicate) {
        BlockPos bestPos = null;
        double bestDist = Double.MAX_VALUE;
        for (BlockPos pos : findBlocks(level, center, radius, predicate)) {
            double dist = pos.distSqr(center);
            if (dist < bestDist) {
                bestDist = dist;
                bestPos = pos;
            }
        }
        return bestPos;
    }

    private static boolean sameOrder(List<ItemStack> first, List<ItemStack> second) {
        if (first.size() != second.size()) {
            return false;
        }

        for (int i = 0; i < first.size(); i++) {
            if (!ItemStack.isSameItemSameTags(first.get(i), second.get(i)) || first.get(i).getCount() != second.get(i).getCount()) {
                return false;
            }
        }

        return true;
    }

    private static int getRandomClientEffectDuration(ServerPlayer player) {
        return 60 + player.level().random.nextInt(41);
    }

    private record BlockRestoreData(int restoreTick, List<BlockSnapshot> snapshots) {
    }

    private record BlockSnapshot(BlockPos pos, BlockState state) {
    }
}

