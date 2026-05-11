package com.igore.code44.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Random;

public final class FreeHouseManager {
    private static final Random RANDOM = new Random();
    private static final int HOUSE_HALF_WIDTH = 2;
    private static final int HOUSE_DEPTH = 5;

    private FreeHouseManager() {
    }

    public static boolean tryPlaceFreeHouse(ServerLevel level, ServerPlayer player) {
        BlockPos origin = findHousePosition(level, player);
        if (origin == null) {
            return false;
        }

        buildHouse(level, origin, resolveFacingFromPlayer(player, origin));
        return true;
    }

    private static void buildHouse(ServerLevel level, BlockPos origin, Direction facing) {
        Direction left = facing.getClockWise();
        BlockPos frontCenter = origin;

        for (int dz = 0; dz < HOUSE_DEPTH; dz++) {
            for (int dx = -HOUSE_HALF_WIDTH; dx <= HOUSE_HALF_WIDTH; dx++) {
                BlockPos floorPos = frontCenter.relative(facing.getOpposite(), dz).relative(left, dx);

                for (int clearY = 0; clearY <= 5; clearY++) {
                    level.setBlockAndUpdate(floorPos.above(clearY), Blocks.AIR.defaultBlockState());
                }

                BlockPos supportPos = floorPos.below();
                for (int depth = 0; depth < 8; depth++) {
                    if (level.getBlockState(supportPos).isFaceSturdy(level, supportPos, Direction.UP)) {
                        break;
                    }
                    level.setBlockAndUpdate(supportPos, Blocks.OAK_PLANKS.defaultBlockState());
                    supportPos = supportPos.below();
                }
            }
        }

        for (int dz = 0; dz < HOUSE_DEPTH; dz++) {
            for (int dx = -HOUSE_HALF_WIDTH; dx <= HOUSE_HALF_WIDTH; dx++) {
                BlockPos floorPos = frontCenter.relative(facing.getOpposite(), dz).relative(left, dx);
                level.setBlockAndUpdate(floorPos, Blocks.OAK_PLANKS.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(), Blocks.TNT.defaultBlockState());
                level.setBlockAndUpdate(floorPos.below(2), Blocks.DIRT.defaultBlockState());
            }
        }

        for (int dz = -1; dz <= HOUSE_DEPTH; dz++) {
            for (int dx = -HOUSE_HALF_WIDTH - 1; dx <= HOUSE_HALF_WIDTH + 1; dx++) {
                BlockPos shellPos = frontCenter.relative(facing.getOpposite(), dz).relative(left, dx);
                if (dz >= 0 && dz < HOUSE_DEPTH && dx >= -HOUSE_HALF_WIDTH && dx <= HOUSE_HALF_WIDTH) {
                    continue;
                }

                if (level.getBlockState(shellPos.below()).canBeReplaced() || level.getBlockState(shellPos.below()).isAir()) {
                    level.setBlockAndUpdate(shellPos.below(), Blocks.DIRT.defaultBlockState());
                }
                if (level.getBlockState(shellPos.below(2)).canBeReplaced() || level.getBlockState(shellPos.below(2)).isAir()) {
                    level.setBlockAndUpdate(shellPos.below(2), Blocks.DIRT.defaultBlockState());
                }
            }
        }

        for (int y = 1; y <= 3; y++) {
            for (int dz = 0; dz < HOUSE_DEPTH; dz++) {
                for (int dx = -HOUSE_HALF_WIDTH; dx <= HOUSE_HALF_WIDTH; dx++) {
                    BlockPos pos = frontCenter.relative(facing.getOpposite(), dz).relative(left, dx).above(y);
                    boolean isWall = dx == -HOUSE_HALF_WIDTH || dx == HOUSE_HALF_WIDTH || dz == 0 || dz == HOUSE_DEPTH - 1;
                    if (!isWall) {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        continue;
                    }

                    boolean doorway = dz == 0 && dx == 0 && y <= 2;
                    boolean windowLeft = dz == 2 && dx == -HOUSE_HALF_WIDTH && y == 2;
                    boolean windowRight = dz == 2 && dx == HOUSE_HALF_WIDTH && y == 2;
                    boolean windowBack = dz == HOUSE_DEPTH - 1 && dx == 0 && y == 2;
                    if (doorway) {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    } else if (windowLeft || windowRight || windowBack) {
                        level.setBlockAndUpdate(pos, Blocks.GLASS_PANE.defaultBlockState());
                    } else {
                        level.setBlockAndUpdate(pos, Blocks.OAK_PLANKS.defaultBlockState());
                    }
                }
            }
        }

        for (int dz = 0; dz < HOUSE_DEPTH; dz++) {
            for (int dx = -HOUSE_HALF_WIDTH; dx <= HOUSE_HALF_WIDTH; dx++) {
                BlockPos roofPos = frontCenter.relative(facing.getOpposite(), dz).relative(left, dx).above(4);
                level.setBlockAndUpdate(roofPos, Blocks.OAK_PLANKS.defaultBlockState());
            }
        }

        BlockPos doorLower = frontCenter.above();
        BlockState lowerDoor = Blocks.OAK_DOOR.defaultBlockState()
                .setValue(HorizontalDirectionalBlock.FACING, facing)
                .setValue(net.minecraft.world.level.block.DoorBlock.HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER);
        BlockState upperDoor = lowerDoor.setValue(net.minecraft.world.level.block.DoorBlock.HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER);
        level.setBlockAndUpdate(doorLower, lowerDoor);
        level.setBlockAndUpdate(doorLower.above(), upperDoor);

        BlockPos signBackPos = frontCenter.relative(facing, 1).above(3);
        level.setBlockAndUpdate(signBackPos, Blocks.OAK_PLANKS.defaultBlockState());
        BlockPos signPos = signBackPos.relative(facing);
        BlockState signState = Blocks.OAK_WALL_SIGN.defaultBlockState()
                .setValue(WallSignBlock.FACING, facing);
        level.setBlockAndUpdate(signPos, signState);
        BlockEntity signEntity = level.getBlockEntity(signPos);
        if (signEntity instanceof SignBlockEntity signBlockEntity) {
            SignText text = signBlockEntity.getFrontText()
                    .setMessage(0, Component.literal("free house"))
                    .setMessage(1, Component.literal(":)"));
            signBlockEntity.setText(text, true);
            signBlockEntity.setWaxed(true);
            signBlockEntity.setChanged();
        }

        BlockPos chestPos = frontCenter.relative(facing.getOpposite(), 2).above();
        BlockState chestState = Blocks.TRAPPED_CHEST.defaultBlockState()
                .setValue(TrappedChestBlock.FACING, facing.getOpposite());
        level.setBlockAndUpdate(chestPos, chestState);
        BlockEntity blockEntity = level.getBlockEntity(chestPos);
        if (blockEntity instanceof Container container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                container.setItem(i, ItemStack.EMPTY);
            }
            container.setItem(13, new ItemStack(Items.DIAMOND, 1));
        }
    }

    private static BlockPos findHousePosition(ServerLevel level, ServerPlayer player) {
        BlockPos center = player.blockPosition();

        for (int attempt = 0; attempt < 80; attempt++) {
            double angle = RANDOM.nextDouble() * (Math.PI * 2.0D);
            int distance = 30 + RANDOM.nextInt(21);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            BlockPos surface = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, center.offset(offsetX, 0, offsetZ));
            BlockPos origin = surface;

            return origin;
        }

        BlockPos fallbackSurface = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                center.offset(40, 0, 0)
        );
        return fallbackSurface;
    }

    private static Direction resolveFacingFromPlayer(ServerPlayer player, BlockPos origin) {
        int dx = player.blockPosition().getX() - origin.getX();
        int dz = player.blockPosition().getZ() - origin.getZ();
        if (Math.abs(dx) > Math.abs(dz)) {
            return dx >= 0 ? Direction.EAST : Direction.WEST;
        }
        return dz >= 0 ? Direction.SOUTH : Direction.NORTH;
    }
}
