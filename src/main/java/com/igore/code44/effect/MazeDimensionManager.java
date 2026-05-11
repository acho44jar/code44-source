package com.igore.code44.effect;

import com.igore.code44.Code44Mod;
import com.igore.code44.entity.MazeGuardianEntity;
import com.igore.code44.registry.ModBlocks;
import com.igore.code44.registry.ModEntities;
import com.igore.code44.registry.ModSounds;
import com.igore.code44.sound.HorrorSoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public final class MazeDimensionManager {
    private static final ResourceKey<Level> MAZE_DIMENSION = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "maze_void_v2")
    );
    private static final String ACTIVE_KEY = "code44MazeDimensionActive";
    private static final String LAST_TRIGGER_TICK_KEY = "code44MazeDimensionLastTriggerTick";
    private static final String RETURN_X_KEY = "code44MazeDimensionReturnX";
    private static final String RETURN_Y_KEY = "code44MazeDimensionReturnY";
    private static final String RETURN_Z_KEY = "code44MazeDimensionReturnZ";
    private static final String RETURN_YAW_KEY = "code44MazeDimensionReturnYaw";
    private static final String RETURN_PITCH_KEY = "code44MazeDimensionReturnPitch";
    private static final String RETURN_DIMENSION_KEY = "code44MazeDimensionReturnDimension";
    private static final String ENTER_TICK_KEY = "code44MazeDimensionEnterTick";
    private static final String GUARDIAN_COOLDOWN_UNTIL_KEY = "code44MazeGuardianCooldownUntil";
    private static final String GUARDIAN_LAST_X_KEY = "code44MazeGuardianLastX";
    private static final String GUARDIAN_LAST_Y_KEY = "code44MazeGuardianLastY";
    private static final String GUARDIAN_LAST_Z_KEY = "code44MazeGuardianLastZ";
    private static final String GUARDIAN_RELOCATE_PENDING_KEY = "code44MazeGuardianRelocatePending";
    private static final String GUARDIAN_RELOCATE_FROM_X_KEY = "code44MazeGuardianRelocateFromX";
    private static final String GUARDIAN_RELOCATE_FROM_Y_KEY = "code44MazeGuardianRelocateFromY";
    private static final String GUARDIAN_RELOCATE_FROM_Z_KEY = "code44MazeGuardianRelocateFromZ";
    private static final String GUARDIAN_SPAWN_TICK_KEY = "code44MazeGuardianSpawnTick";
    private static final String MAZE_AMBIENT_NEXT_TICK_KEY = "code44MazeAmbientNextTick";
    private static final String DEBUG_ENABLED_KEY = "code44DebugEventsEnabled";
    private static final int TRIGGER_COOLDOWN_TICKS = 20 * 120;
    private static final int MAZE_Y = 80;
    private static final int CELL_COUNT = 31;
    private static final int PASSAGE_WIDTH = 6;
    private static final int WALL_HEIGHT = 9;
    private static final int GUARDIAN_SPAWN_INTERVAL_TICKS = 70;
    private static final int ROOM_ATTEMPTS = 10;
    private static final int FAKE_EXIT_ATTEMPTS = 8;
    private static final int MAZE_STAY_TICKS = 20 * 60 * 5;
    private static final int MAZE_AMBIENT_INTERVAL_TICKS = 20 * 55;
    private static final Random RANDOM = new Random();

    private MazeDimensionManager() {
    }

    public static boolean isSceneActive(ServerPlayer player) {
        return getModData(player).getBoolean(ACTIVE_KEY);
    }

    public static boolean isMazeDimension(ServerLevel level) {
        return level.dimension() == MAZE_DIMENSION;
    }

    public static boolean startScene(ServerLevel level, ServerPlayer player, BlockPos altarPos) {
        CompoundTag data = getModData(player);
        if (isSceneActive(player)) {
            return false;
        }

        long now = level.getGameTime();
        if (now < data.getLong(LAST_TRIGGER_TICK_KEY)) {
            return false;
        }

        ServerLevel mazeLevel = level.getServer().getLevel(MAZE_DIMENSION);
        if (mazeLevel == null) {
            return false;
        }

        BlockPos origin = getMazeOrigin(player);
        prepareMaze(mazeLevel, origin);
        clearSceneEntities(mazeLevel, origin);

        data.putBoolean(ACTIVE_KEY, true);
        data.putLong(LAST_TRIGGER_TICK_KEY, now + TRIGGER_COOLDOWN_TICKS);
        data.putDouble(RETURN_X_KEY, player.getX());
        data.putDouble(RETURN_Y_KEY, player.getY());
        data.putDouble(RETURN_Z_KEY, player.getZ());
        data.putFloat(RETURN_YAW_KEY, player.getYRot());
        data.putFloat(RETURN_PITCH_KEY, player.getXRot());
        data.putString(RETURN_DIMENSION_KEY, level.dimension().location().toString());
        data.putInt(ENTER_TICK_KEY, player.tickCount);
        data.putInt(MAZE_AMBIENT_NEXT_TICK_KEY, player.tickCount + MAZE_AMBIENT_INTERVAL_TICKS);

        level.setBlockAndUpdate(altarPos, Blocks.AIR.defaultBlockState());
        player.teleportTo(mazeLevel, origin.getX() + 6.5D, origin.getY() + 1.0D, origin.getZ() + 6.5D, 180.0F, 0.0F);
        player.setDeltaMovement(Vec3.ZERO);
        player.connection.resetPosition();

        playMazeAmbient(mazeLevel, player);
        BlockPos guardianSpawnPos = spawnMazeGuardianInternal(mazeLevel, player, true);
        if (guardianSpawnPos != null) {
            startGuardianCooldown(player);
        }
        return true;
    }

    public static void tick(ServerLevel level, ServerPlayer player) {
        if (!isSceneActive(player)) {
            return;
        }

        if (player.serverLevel().dimension() != MAZE_DIMENSION) {
            clearSceneData(player);
            return;
        }

        if (!player.isAlive()) {
            clearSceneData(player);
            return;
        }

        CompoundTag data = getModData(player);
        tickMazeAmbient(level, player, data);
        int enterTick = data.getInt(ENTER_TICK_KEY);
        if (enterTick > 0 && player.tickCount - enterTick >= MAZE_STAY_TICKS) {
            returnPlayerToWorld(player);
            return;
        }

        MazeGuardianEntity existingGuardian = findExistingGuardian(level, player);
        if (existingGuardian != null) {
            int spawnTick = data.getInt(GUARDIAN_SPAWN_TICK_KEY);
            if (spawnTick > 0 && player.tickCount - spawnTick >= 600) {
                markGuardianRelocation(player, existingGuardian.blockPosition());
                existingGuardian.discard();
                BlockPos behindSpawn = findGuardianBehindPlayerSpawn(level, player);
                if (behindSpawn != null) {
                    spawnMazeGuardianAt(level, player, behindSpawn);
                    startGuardianCooldown(player);
                }
            }
            return;
        }

        if (player.tickCount % GUARDIAN_SPAWN_INTERVAL_TICKS == 0 && shouldAttemptGuardianSpawn(player)) {
            if (data.getBoolean(GUARDIAN_RELOCATE_PENDING_KEY)) {
                spawnMazeGuardianInternal(level, player, true);
            } else {
                spawnMazeGuardian(level, player);
            }
        }
    }

    public static void clearSceneData(ServerPlayer player) {
        CompoundTag data = getModData(player);
        HorrorSoundManager.stopMazeAmbient(player);
        data.remove(ACTIVE_KEY);
        data.remove(RETURN_X_KEY);
        data.remove(RETURN_Y_KEY);
        data.remove(RETURN_Z_KEY);
        data.remove(RETURN_YAW_KEY);
        data.remove(RETURN_PITCH_KEY);
        data.remove(RETURN_DIMENSION_KEY);
        data.remove(ENTER_TICK_KEY);
        data.remove(GUARDIAN_COOLDOWN_UNTIL_KEY);
        data.remove(GUARDIAN_LAST_X_KEY);
        data.remove(GUARDIAN_LAST_Y_KEY);
        data.remove(GUARDIAN_LAST_Z_KEY);
        data.remove(GUARDIAN_RELOCATE_PENDING_KEY);
        data.remove(GUARDIAN_RELOCATE_FROM_X_KEY);
        data.remove(GUARDIAN_RELOCATE_FROM_Y_KEY);
        data.remove(GUARDIAN_RELOCATE_FROM_Z_KEY);
        data.remove(GUARDIAN_SPAWN_TICK_KEY);
        data.remove(MAZE_AMBIENT_NEXT_TICK_KEY);
    }

    private static void prepareMaze(ServerLevel level, BlockPos origin) {
        boolean[][] maze = generateMaze();
        int gridSize = maze.length;
        int worldSize = gridSize * PASSAGE_WIDTH + 2;
        BlockState tunnelStone = ModBlocks.TUNNEL_STONE.get().defaultBlockState();

        for (int z = 0; z < worldSize; z++) {
            for (int x = 0; x < worldSize; x++) {
                BlockPos floor = origin.offset(x, 0, z);
                level.setBlockAndUpdate(floor, tunnelStone);
                level.setBlockAndUpdate(floor.below(), tunnelStone);

                for (int y = 1; y < WALL_HEIGHT; y++) {
                    level.setBlockAndUpdate(floor.above(y), pickWallBlock(x, y, z));
                }
                level.setBlockAndUpdate(floor.above(WALL_HEIGHT), tunnelStone);
            }
        }

        for (int gz = 0; gz < gridSize; gz++) {
            for (int gx = 0; gx < gridSize; gx++) {
                if (!maze[gz][gx]) {
                    continue;
                }

                int worldX = gx * PASSAGE_WIDTH + 1;
                int worldZ = gz * PASSAGE_WIDTH + 1;
                for (int dz = 0; dz < PASSAGE_WIDTH; dz++) {
                    for (int dx = 0; dx < PASSAGE_WIDTH; dx++) {
                        BlockPos corridorFloor = origin.offset(worldX + dx, 0, worldZ + dz);
                        for (int y = 1; y < WALL_HEIGHT; y++) {
                            level.setBlockAndUpdate(corridorFloor.above(y), Blocks.AIR.defaultBlockState());
                        }
                    }
                }

                placeCorridorLighting(level, origin, worldX, worldZ);
                decorateCorridorWalls(level, origin, worldX, worldZ);
                placeCorridorOvergrowth(level, origin, worldX, worldZ);
            }
        }

        carveSpawnRoom(level, origin);
        carveSpawnExit(level, origin);
        carveSideRooms(level, origin, maze);
        placeFakeExits(level, origin, maze);
        decorateSpawnRoom(level, origin);
    }

    private static void carveSpawnRoom(ServerLevel level, BlockPos origin) {
        for (int z = 1; z <= 12; z++) {
            for (int x = 1; x <= 12; x++) {
                BlockPos floor = origin.offset(x, 0, z);
                for (int y = 1; y < WALL_HEIGHT; y++) {
                    level.setBlockAndUpdate(floor.above(y), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private static void carveSpawnExit(ServerLevel level, BlockPos origin) {
        for (int z = 4; z <= 9; z++) {
            for (int x = 13; x <= 18; x++) {
                BlockPos floor = origin.offset(x, 0, z);
                for (int y = 1; y < WALL_HEIGHT; y++) {
                    level.setBlockAndUpdate(floor.above(y), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private static void carveSideRooms(ServerLevel level, BlockPos origin, boolean[][] maze) {
        List<int[]> candidates = new ArrayList<>();
        for (int gz = 2; gz < maze.length - 2; gz++) {
            for (int gx = 2; gx < maze[gz].length - 2; gx++) {
                if (maze[gz][gx]) {
                    candidates.add(new int[]{gx, gz});
                }
            }
        }

        Collections.shuffle(candidates, RANDOM);
        int carved = 0;
        for (int[] candidate : candidates) {
            if (carved >= ROOM_ATTEMPTS) {
                return;
            }

            int gx = candidate[0];
            int gz = candidate[1];
            int roomOriginX = gx * PASSAGE_WIDTH - 2;
            int roomOriginZ = gz * PASSAGE_WIDTH - 2;
            carveRoom(level, origin, roomOriginX, roomOriginZ, PASSAGE_WIDTH + 4, PASSAGE_WIDTH + 4);
            placeRoomDetails(level, origin.offset(roomOriginX, 0, roomOriginZ), PASSAGE_WIDTH + 4, PASSAGE_WIDTH + 4);
            carved++;
        }
    }

    private static void carveRoom(ServerLevel level, BlockPos origin, int roomOriginX, int roomOriginZ, int width, int depth) {
        for (int dz = 0; dz < depth; dz++) {
            for (int dx = 0; dx < width; dx++) {
                BlockPos floor = origin.offset(roomOriginX + dx, 0, roomOriginZ + dz);
                level.setBlockAndUpdate(floor, ModBlocks.TUNNEL_STONE.get().defaultBlockState());
                for (int y = 1; y < WALL_HEIGHT; y++) {
                    level.setBlockAndUpdate(floor.above(y), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private static void placeRoomDetails(ServerLevel level, BlockPos roomOrigin, int width, int depth) {
        BlockPos center = roomOrigin.offset(width / 2, 0, depth / 2);
        level.setBlockAndUpdate(center.above(WALL_HEIGHT - 2), Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        level.setBlockAndUpdate(roomOrigin.offset(1, 0, 1).above(WALL_HEIGHT - 2), Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        level.setBlockAndUpdate(roomOrigin.offset(width - 2, 0, depth - 2).above(WALL_HEIGHT - 2), Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        placePlantCluster(level, roomOrigin.offset(1, 0, depth - 2), 2);
        placePlantCluster(level, roomOrigin.offset(width - 2, 0, 1), 2);
    }

    private static void placeFakeExits(ServerLevel level, BlockPos origin, boolean[][] maze) {
        List<int[]> deadEnds = new ArrayList<>();
        for (int gz = 1; gz < maze.length - 1; gz++) {
            for (int gx = 1; gx < maze[gz].length - 1; gx++) {
                if (!maze[gz][gx]) {
                    continue;
                }

                int exits = 0;
                if (maze[gz - 1][gx]) exits++;
                if (maze[gz + 1][gx]) exits++;
                if (maze[gz][gx - 1]) exits++;
                if (maze[gz][gx + 1]) exits++;
                if (exits == 1) {
                    deadEnds.add(new int[]{gx, gz});
                }
            }
        }

        Collections.shuffle(deadEnds, RANDOM);
        int placed = 0;
        for (int[] deadEnd : deadEnds) {
            if (placed >= FAKE_EXIT_ATTEMPTS) {
                return;
            }

            Direction direction = getDeadEndExitDirection(maze, deadEnd[0], deadEnd[1]);
            if (direction == null) {
                continue;
            }

            int worldX = deadEnd[0] * PASSAGE_WIDTH + 1;
            int worldZ = deadEnd[1] * PASSAGE_WIDTH + 1;
            BlockPos base = origin.offset(worldX, 0, worldZ);
            placeFakeExitAt(level, base, direction);
            placed++;
        }
    }

    private static Direction getDeadEndExitDirection(boolean[][] maze, int gx, int gz) {
        if (maze[gz - 1][gx]) return Direction.SOUTH;
        if (maze[gz + 1][gx]) return Direction.NORTH;
        if (maze[gz][gx - 1]) return Direction.EAST;
        if (maze[gz][gx + 1]) return Direction.WEST;
        return null;
    }

    private static void placeFakeExitAt(ServerLevel level, BlockPos center, Direction direction) {
        BlockPos doorBase = center.relative(direction, 2).above();
        BlockPos nicheBase = doorBase.relative(direction);
        carveFakeExitNiche(level, nicheBase);

        Direction left = direction.getClockWise();
        Direction right = direction.getCounterClockWise();
        for (int y = 0; y <= 3; y++) {
            level.setBlockAndUpdate(doorBase.relative(left).above(y), Blocks.STONE_BRICKS.defaultBlockState());
            level.setBlockAndUpdate(doorBase.relative(right).above(y), Blocks.STONE_BRICKS.defaultBlockState());
        }
        level.setBlockAndUpdate(doorBase.above(2), Blocks.STONE_BRICKS.defaultBlockState());

        level.setBlockAndUpdate(doorBase, Blocks.OAK_DOOR.defaultBlockState().setValue(net.minecraft.world.level.block.DoorBlock.FACING, direction));
        level.setBlockAndUpdate(doorBase.above(), Blocks.OAK_DOOR.defaultBlockState()
                .setValue(net.minecraft.world.level.block.DoorBlock.FACING, direction)
                .setValue(net.minecraft.world.level.block.DoorBlock.HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER));
    }

    private static void carveFakeExitNiche(ServerLevel level, BlockPos nicheBase) {
        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                BlockPos floor = nicheBase.offset(dx, 0, dz);
                level.setBlockAndUpdate(floor, ModBlocks.TUNNEL_STONE.get().defaultBlockState());
                for (int y = 1; y <= 3; y++) {
                    level.setBlockAndUpdate(floor.above(y), Blocks.AIR.defaultBlockState());
                }
            }
        }

        level.setBlockAndUpdate(nicheBase.above(3), Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
    }

    private static boolean[][] generateMaze() {
        int size = CELL_COUNT * 2 + 1;
        boolean[][] carved = new boolean[size][size];
        boolean[][] visited = new boolean[CELL_COUNT][CELL_COUNT];
        ArrayDeque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{0, 0});
        visited[0][0] = true;
        carveCell(carved, 0, 0);

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int cx = current[0];
            int cz = current[1];

            int[][] shuffled = dirs.clone();
            for (int i = 0; i < shuffled.length; i++) {
                int swap = RANDOM.nextInt(shuffled.length);
                int[] tmp = shuffled[i];
                shuffled[i] = shuffled[swap];
                shuffled[swap] = tmp;
            }

            boolean moved = false;
            for (int[] dir : shuffled) {
                int nx = cx + dir[0];
                int nz = cz + dir[1];
                if (nx < 0 || nz < 0 || nx >= CELL_COUNT || nz >= CELL_COUNT || visited[nz][nx]) {
                    continue;
                }

                visited[nz][nx] = true;
                carveCell(carved, nx, nz);
                carveConnector(carved, cx, cz, nx, nz);
                stack.push(new int[]{nx, nz});
                moved = true;
                break;
            }

            if (!moved) {
                stack.pop();
            }
        }

        return carved;
    }

    private static void carveCell(boolean[][] carved, int cellX, int cellZ) {
        carved[cellZ * 2 + 1][cellX * 2 + 1] = true;
    }

    private static void carveConnector(boolean[][] carved, int fromX, int fromZ, int toX, int toZ) {
        carved[fromZ + toZ + 1][fromX + toX + 1] = true;
    }

    private static void clearSceneEntities(ServerLevel level, BlockPos origin) {
        int size = (CELL_COUNT * 2 + 1) * PASSAGE_WIDTH + 2;
        AABB bounds = new AABB(origin.getX(), origin.getY() - 2, origin.getZ(), origin.getX() + size, origin.getY() + WALL_HEIGHT + 4, origin.getZ() + size);
        for (Entity entity : level.getEntitiesOfClass(Entity.class, bounds, entity -> !(entity instanceof ServerPlayer))) {
            entity.discard();
        }
    }

    public static boolean spawnMazeGuardian(ServerLevel level, ServerPlayer player) {
        return spawnMazeGuardianInternal(level, player, false) != null;
    }

    public static boolean spawnMazeGuardianForCommand(ServerLevel level, ServerPlayer player) {
        return spawnMazeGuardianInternal(level, player, true) != null;
    }

    private static BlockPos spawnMazeGuardianInternal(ServerLevel level, ServerPlayer player, boolean forced) {
        if (player.serverLevel().dimension() != MAZE_DIMENSION) {
            return null;
        }

        if (forced) {
            for (Entity entity : level.getEntities(ModEntities.MAZE_GUARDIAN.get(), player.getBoundingBox().inflate(128.0D), entity -> true)) {
                entity.discard();
            }
        } else if (!level.getEntities(ModEntities.MAZE_GUARDIAN.get(), player.getBoundingBox().inflate(48.0D), entity -> true).isEmpty()) {
            return null;
        }

        BlockPos spawnPos = forced
                ? findGuardianForcedSpawnPosition(level, player)
                : findGuardianSpawnPosition(level, player);
        if (spawnPos == null) {
            return null;
        }

        return spawnMazeGuardianAt(level, player, spawnPos);
    }

    private static BlockPos findGuardianCommandSpawnPosition(ServerLevel level, ServerPlayer player) {
        return findGuardianCandidate(level, player, 20, false, false);
    }

    private static BlockPos findGuardianForcedSpawnPosition(ServerLevel level, ServerPlayer player) {
        return findGuardianCandidate(level, player, 20, false, false);
    }

    private static BlockPos findGuardianSpawnPosition(ServerLevel level, ServerPlayer player) {
        return findGuardianCandidate(level, player, 20, true, true);
    }

    private static BlockPos findGuardianCandidate(ServerLevel level, ServerPlayer player, int radius, boolean requireOutsideDirectView, boolean requireClearSight) {
        List<BlockPos> nearbyCandidates = collectGuardianCandidates(level, player, radius, requireOutsideDirectView, requireClearSight);
        if (!nearbyCandidates.isEmpty()) {
            return nearbyCandidates.get(RANDOM.nextInt(nearbyCandidates.size()));
        }

        List<BlockPos> fullMazeCandidates = collectFullMazeGuardianCandidates(level, player, requireOutsideDirectView, requireClearSight);
        if (!fullMazeCandidates.isEmpty()) {
            return fullMazeCandidates.get(RANDOM.nextInt(fullMazeCandidates.size()));
        }

        return null;
    }

    private static List<BlockPos> collectGuardianCandidates(ServerLevel level, ServerPlayer player, int radius, boolean requireOutsideDirectView, boolean requireClearSight) {
        List<BlockPos> candidates = new ArrayList<>();
        BlockPos center = player.blockPosition();
        BlockPos lastSpawn = getLastGuardianSpawn(player);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                if (center.distSqr(pos) < 64.0D) {
                    continue;
                }

                ensureClearSpace(level, pos);
                if (!isFarEnoughFromLastSpawn(lastSpawn, pos)) {
                    continue;
                }
                if (!(isMazeWalkable(level, pos) || isWalkableForGuardian(level, pos))) {
                    continue;
                }
                if (requireOutsideDirectView && !isOutsideDirectView(player, pos)) {
                    continue;
                }
                if (requireClearSight && !hasClearSight(level, player, pos)) {
                    continue;
                }

                candidates.add(pos.immutable());
            }
        }

        return candidates;
    }

    private static List<BlockPos> collectFullMazeGuardianCandidates(ServerLevel level, ServerPlayer player, boolean requireOutsideDirectView, boolean requireClearSight) {
        List<BlockPos> candidates = new ArrayList<>();
        BlockPos origin = getMazeOrigin(player);
        BlockPos center = player.blockPosition();
        BlockPos lastSpawn = getLastGuardianSpawn(player);
        int worldSize = (CELL_COUNT * 2 + 1) * PASSAGE_WIDTH + 2;

        for (int x = 1; x < worldSize - 1; x++) {
            for (int z = 1; z < worldSize - 1; z++) {
                BlockPos pos = origin.offset(x, 0, z);
                if (center.distSqr(pos) < 64.0D) {
                    continue;
                }
                if (!isFarEnoughFromLastSpawn(lastSpawn, pos)) {
                    continue;
                }
                if (!(isMazeWalkable(level, pos) || isWalkableForGuardian(level, pos))) {
                    continue;
                }
                if (requireOutsideDirectView && !isOutsideDirectView(player, pos)) {
                    continue;
                }
                if (requireClearSight && !hasClearSight(level, player, pos)) {
                    continue;
                }

                candidates.add(pos.immutable());
            }
        }

        return candidates;
    }

    private static boolean isMazeWalkable(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.TUNNEL_STONE.get())
                && level.getBlockState(pos.above()).isAir()
                && level.getBlockState(pos.above(2)).isAir()
                && level.getBlockState(pos.above(3)).isAir()
                && level.getBlockState(pos.above(4)).isAir()
                && level.getBlockState(pos.above(5)).isAir()
                && level.getBlockState(pos.above(6)).isAir()
                && level.getBlockState(pos.above(7)).isAir()
                && level.getBlockState(pos.above(9)).is(ModBlocks.TUNNEL_STONE.get());
    }

    private static boolean isWalkableForGuardian(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.TUNNEL_STONE.get())
                && level.getBlockState(pos.above()).isAir()
                && level.getBlockState(pos.above(2)).isAir()
                && level.getBlockState(pos.above(3)).isAir()
                && level.getBlockState(pos.above(4)).isAir();
    }

    private static void ensureClearSpace(ServerLevel level, BlockPos pos) {
        for (int y = 1; y <= 8; y++) {
            BlockPos clearPos = pos.above(y);
            if (!level.getBlockState(clearPos).isAir() && level.getBlockState(clearPos).canBeReplaced()) {
                level.setBlockAndUpdate(clearPos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    private static boolean hasClearSight(ServerLevel level, ServerPlayer player, BlockPos pos) {
        HitResult hitResult = level.clip(new ClipContext(
                player.getEyePosition(),
                new Vec3(pos.getX() + 0.5D, pos.getY() + 2.0D, pos.getZ() + 0.5D),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));
        return hitResult.getType() == HitResult.Type.MISS;
    }

    private static boolean isOutsideDirectView(ServerPlayer player, BlockPos pos) {
        Vec3 look = player.getLookAngle().normalize();
        Vec3 toPos = new Vec3(
                pos.getX() + 0.5D - player.getX(),
                0.0D,
                pos.getZ() + 0.5D - player.getZ()
        ).normalize();

        if (toPos.lengthSqr() < 1.0E-4D) {
            return false;
        }

        double dot = look.dot(toPos);
        return dot < 0.55D;
    }

    private static boolean shouldAttemptGuardianSpawn(ServerPlayer player) {
        CompoundTag data = getModData(player);
        return player.tickCount >= data.getInt(GUARDIAN_COOLDOWN_UNTIL_KEY);
    }

    public static void startGuardianCooldown(ServerPlayer player) {
        getModData(player).putInt(GUARDIAN_COOLDOWN_UNTIL_KEY, player.tickCount + 200);
    }

    public static void markGuardianRelocation(ServerPlayer player, BlockPos fromPos) {
        CompoundTag data = getModData(player);
        data.putBoolean(GUARDIAN_RELOCATE_PENDING_KEY, true);
        data.putInt(GUARDIAN_RELOCATE_FROM_X_KEY, fromPos.getX());
        data.putInt(GUARDIAN_RELOCATE_FROM_Y_KEY, fromPos.getY());
        data.putInt(GUARDIAN_RELOCATE_FROM_Z_KEY, fromPos.getZ());
    }

    private static void rememberGuardianSpawn(ServerPlayer player, BlockPos pos) {
        CompoundTag data = getModData(player);
        data.putInt(GUARDIAN_LAST_X_KEY, pos.getX());
        data.putInt(GUARDIAN_LAST_Y_KEY, pos.getY());
        data.putInt(GUARDIAN_LAST_Z_KEY, pos.getZ());
        data.putInt(GUARDIAN_SPAWN_TICK_KEY, player.tickCount);
    }

    private static BlockPos getLastGuardianSpawn(ServerPlayer player) {
        CompoundTag data = getModData(player);
        if (!data.contains(GUARDIAN_LAST_X_KEY) || !data.contains(GUARDIAN_LAST_Y_KEY) || !data.contains(GUARDIAN_LAST_Z_KEY)) {
            return null;
        }
        return new BlockPos(data.getInt(GUARDIAN_LAST_X_KEY), data.getInt(GUARDIAN_LAST_Y_KEY), data.getInt(GUARDIAN_LAST_Z_KEY));
    }

    private static boolean isFarEnoughFromLastSpawn(BlockPos lastSpawn, BlockPos candidate) {
        return lastSpawn == null || lastSpawn.distSqr(candidate) >= 81.0D;
    }

    private static MazeGuardianEntity findExistingGuardian(ServerLevel level, ServerPlayer player) {
        return level.getEntitiesOfClass(
                MazeGuardianEntity.class,
                player.getBoundingBox().inflate(128.0D),
                Entity::isAlive
        ).stream().findFirst().orElse(null);
    }

    private static BlockPos findGuardianBehindPlayerSpawn(ServerLevel level, ServerPlayer player) {
        Vec3 look = player.getLookAngle().normalize();
        Vec3 behind = look.scale(-1.0D);
        BlockPos lastSpawn = getLastGuardianSpawn(player);

        for (int distance = 8; distance <= 20; distance += 2) {
            BlockPos pos = BlockPos.containing(
                    player.getX() + behind.x * distance,
                    player.getY(),
                    player.getZ() + behind.z * distance
            );
            ensureClearSpace(level, pos);
            if (isFarEnoughFromLastSpawn(lastSpawn, pos)
                    && (isMazeWalkable(level, pos) || isWalkableForGuardian(level, pos))) {
                return pos;
            }
        }

        return findGuardianForcedSpawnPosition(level, player);
    }

    private static BlockPos spawnMazeGuardianAt(ServerLevel level, ServerPlayer player, BlockPos spawnPos) {
        MazeGuardianEntity guardian = ModEntities.MAZE_GUARDIAN.get().create(level);
        if (guardian == null) {
            return null;
        }

        guardian.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY() + 1.15D, spawnPos.getZ() + 0.5D, 0.0F, 0.0F);
        guardian.setWatchingTarget(player.getUUID());
        level.addFreshEntity(guardian);
        rememberGuardianSpawn(player, spawnPos);
        sendGuardianRelocationDebug(player, spawnPos);
        return spawnPos;
    }

    private static void sendGuardianRelocationDebug(ServerPlayer player, BlockPos spawnPos) {
        if (!player.getPersistentData().getBoolean(DEBUG_ENABLED_KEY)) {
            return;
        }

        CompoundTag data = getModData(player);
        if (!data.getBoolean(GUARDIAN_RELOCATE_PENDING_KEY)) {
            return;
        }
        data.remove(GUARDIAN_RELOCATE_PENDING_KEY);
        data.remove(GUARDIAN_RELOCATE_FROM_X_KEY);
        data.remove(GUARDIAN_RELOCATE_FROM_Y_KEY);
        data.remove(GUARDIAN_RELOCATE_FROM_Z_KEY);
    }

    private static BlockPos getMazeOrigin(ServerPlayer player) {
        UUID uuid = player.getUUID();
        long mix = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();
        int x = (int) Math.floorMod(mix, 250_000L);
        int z = (int) Math.floorMod(mix >>> 19, 250_000L);
        return new BlockPos(x, MAZE_Y, z);
    }

    private static void returnPlayerToWorld(ServerPlayer player) {
        CompoundTag data = getModData(player);
        String dimensionId = data.getString(RETURN_DIMENSION_KEY);
        ResourceLocation location = ResourceLocation.tryParse(dimensionId);
        if (location == null) {
            clearSceneData(player);
            return;
        }

        ResourceKey<Level> returnDimension = ResourceKey.create(Registries.DIMENSION, location);
        ServerLevel returnLevel = player.server.getLevel(returnDimension);
        if (returnLevel == null) {
            clearSceneData(player);
            return;
        }

        double returnX = data.getDouble(RETURN_X_KEY);
        double returnY = data.getDouble(RETURN_Y_KEY);
        double returnZ = data.getDouble(RETURN_Z_KEY);
        float returnYaw = data.getFloat(RETURN_YAW_KEY);
        float returnPitch = data.getFloat(RETURN_PITCH_KEY);

        player.teleportTo(returnLevel, returnX, returnY, returnZ, returnYaw, returnPitch);
        player.setDeltaMovement(Vec3.ZERO);
        player.connection.resetPosition();
        clearSceneData(player);
    }

    private static CompoundTag getModData(ServerPlayer player) {
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains(FearManager.MOD_DATA_KEY)) {
            persistentData.put(FearManager.MOD_DATA_KEY, new CompoundTag());
        }
        return persistentData.getCompound(FearManager.MOD_DATA_KEY);
    }

    private static void tickMazeAmbient(ServerLevel level, ServerPlayer player, CompoundTag data) {
        if (player.tickCount < data.getInt(MAZE_AMBIENT_NEXT_TICK_KEY)) {
            return;
        }

        playMazeAmbient(level, player);
        data.putInt(MAZE_AMBIENT_NEXT_TICK_KEY, player.tickCount + MAZE_AMBIENT_INTERVAL_TICKS);
    }

    private static void playMazeAmbient(ServerLevel level, ServerPlayer player) {
        HorrorSoundManager.playMazeAmbient(player);
    }

    private static BlockState pickWallBlock(int x, int y, int z) {
        int pattern = Math.floorMod(x * 31 + y * 17 + z * 13, 11);
        if (pattern == 0) {
            return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        }
        if (pattern <= 2) {
            return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        }
        if (pattern == 3) {
            return Blocks.COBBLESTONE.defaultBlockState();
        }
        return Blocks.STONE_BRICKS.defaultBlockState();
    }

    private static void placeCorridorLighting(ServerLevel level, BlockPos origin, int worldX, int worldZ) {
        int centerX = worldX + (PASSAGE_WIDTH / 2);
        int centerZ = worldZ + (PASSAGE_WIDTH / 2);

        if (((worldX / PASSAGE_WIDTH) + (worldZ / PASSAGE_WIDTH)) % 3 != 0) {
            return;
        }

        BlockPos lanternPos = origin.offset(centerX, WALL_HEIGHT - 1, centerZ);
        level.setBlockAndUpdate(lanternPos, Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
    }

    private static void decorateCorridorWalls(ServerLevel level, BlockPos origin, int worldX, int worldZ) {
        for (int offset = 0; offset < PASSAGE_WIDTH; offset++) {
            decorateWallSegment(level, origin.offset(worldX + offset, 0, worldZ - 1), Direction.SOUTH, worldX + worldZ + offset);
            decorateWallSegment(level, origin.offset(worldX + offset, 0, worldZ + PASSAGE_WIDTH), Direction.NORTH, worldX + worldZ + offset + 7);
            decorateWallSegment(level, origin.offset(worldX - 1, 0, worldZ + offset), Direction.EAST, worldX + worldZ + offset + 13);
            decorateWallSegment(level, origin.offset(worldX + PASSAGE_WIDTH, 0, worldZ + offset), Direction.WEST, worldX + worldZ + offset + 19);
        }
    }

    private static void decorateWallSegment(ServerLevel level, BlockPos wallBase, Direction facing, int seed) {
        if (level.getBlockState(wallBase.above()).isAir()) {
            return;
        }

        int variant = Math.floorMod(seed, 4);
        level.setBlockAndUpdate(wallBase.above(), Blocks.STONE_BRICK_WALL.defaultBlockState());
        level.setBlockAndUpdate(wallBase.above(2), pickWallBlock(wallBase.getX(), 2, wallBase.getZ()));
        level.setBlockAndUpdate(wallBase.above(3), pickWallBlock(wallBase.getX(), 3, wallBase.getZ()));
        level.setBlockAndUpdate(
                wallBase.above(4),
                Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                        .setValue(StairBlock.FACING, facing)
                        .setValue(StairBlock.HALF, Half.TOP)
        );

        if (variant == 0) {
            level.setBlockAndUpdate(wallBase.above(5), Blocks.STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, net.minecraft.world.level.block.state.properties.SlabType.BOTTOM));
        } else {
            level.setBlockAndUpdate(wallBase.above(5), pickWallBlock(wallBase.getX(), 5, wallBase.getZ()));
        }

        level.setBlockAndUpdate(
                wallBase.above(6),
                Blocks.STONE_BRICK_STAIRS.defaultBlockState()
                        .setValue(StairBlock.FACING, facing)
        );

        if (variant == 1) {
            level.setBlockAndUpdate(wallBase.above(7), Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState());
        }
    }

    private static void placeCorridorOvergrowth(ServerLevel level, BlockPos origin, int worldX, int worldZ) {
        int seed = worldX * 17 + worldZ * 23;
        if (Math.floorMod(seed, 4) == 0) {
            placePlantCluster(level, origin.offset(worldX + 1, 0, worldZ + 1), 1);
        }
        if (Math.floorMod(seed, 5) == 0) {
            placePlantCluster(level, origin.offset(worldX + PASSAGE_WIDTH - 2, 0, worldZ + PASSAGE_WIDTH - 2), 1);
        }
    }

    private static void decorateSpawnRoom(ServerLevel level, BlockPos origin) {
        for (int x = 1; x <= 12; x += 2) {
            decorateWallSegment(level, origin.offset(x, 0, 0), Direction.SOUTH, 100 + x);
            decorateWallSegment(level, origin.offset(x, 0, 13), Direction.NORTH, 120 + x);
        }

        for (int z = 1; z <= 12; z += 2) {
            decorateWallSegment(level, origin.offset(0, 0, z), Direction.EAST, 140 + z);
            decorateWallSegment(level, origin.offset(13, 0, z), Direction.WEST, 160 + z);
        }

        placePlantCluster(level, origin.offset(2, 0, 2), 2);
        placePlantCluster(level, origin.offset(10, 0, 2), 2);
        placePlantCluster(level, origin.offset(2, 0, 10), 2);
        placePlantCluster(level, origin.offset(10, 0, 10), 2);

        level.setBlockAndUpdate(origin.offset(6, WALL_HEIGHT - 1, 6), Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        level.setBlockAndUpdate(origin.offset(4, WALL_HEIGHT - 1, 6), Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
        level.setBlockAndUpdate(origin.offset(8, WALL_HEIGHT - 1, 6), Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
    }

    private static void placePlantCluster(ServerLevel level, BlockPos center, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (Math.abs(dx) + Math.abs(dz) > radius + 1) {
                    continue;
                }

                BlockPos floorPos = center.offset(dx, 0, dz);
                BlockPos plantPos = floorPos.above();
                if (!level.getBlockState(floorPos).is(ModBlocks.TUNNEL_STONE.get()) || !level.getBlockState(plantPos).isAir()) {
                    continue;
                }

                int pattern = Math.floorMod(floorPos.getX() * 5 + floorPos.getZ() * 3, 6);
                if (pattern == 0) {
                    level.setBlockAndUpdate(plantPos, Blocks.MOSS_CARPET.defaultBlockState());
                } else if (pattern <= 2) {
                    level.setBlockAndUpdate(plantPos, Blocks.GRASS.defaultBlockState());
                } else if (pattern == 3) {
                    level.setBlockAndUpdate(plantPos, Blocks.FERN.defaultBlockState());
                } else {
                    level.setBlockAndUpdate(plantPos, Blocks.AZALEA_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true));
                }
            }
        }
    }
}

