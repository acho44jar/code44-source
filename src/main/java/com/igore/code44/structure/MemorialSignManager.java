package com.igore.code44.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

public final class MemorialSignManager {
    private MemorialSignManager() {
    }

    public static boolean tryPlaceMemorialSign(ServerLevel level, ServerPlayer player) {
        BlockPos signPos = findMemorialPosition(level, player);

        if (signPos == null) {
            return false;
        }

        BlockPos basePos = signPos.below();
        RandomSource random = level.random;
        String playerName = player.getGameProfile().getName();
        String birthDate = generateDate(random, 2000, 2009);
        String deathDate = generateDate(random, 2030, 2040);

        level.setBlock(basePos, Blocks.COBBLED_DEEPSLATE.defaultBlockState(), 3);
        level.setBlock(
                signPos,
                Blocks.OAK_SIGN.defaultBlockState().setValue(StandingSignBlock.ROTATION, random.nextInt(16)),
                3
        );

        BlockEntity blockEntity = level.getBlockEntity(signPos);

        if (blockEntity instanceof SignBlockEntity signBlockEntity) {
            SignText frontText = signBlockEntity.getFrontText()
                    .setMessage(0, Component.literal(playerName))
                    .setMessage(1, Component.literal(birthDate))
                    .setMessage(2, Component.literal("-"))
                    .setMessage(3, Component.literal(deathDate));
            signBlockEntity.setText(frontText, true);
            signBlockEntity.setWaxed(true);
            signBlockEntity.setChanged();
        }

        return true;
    }

    private static BlockPos findMemorialPosition(ServerLevel level, ServerPlayer player) {
        RandomSource random = level.random;

        for (int attempt = 0; attempt < 24; attempt++) {
            double angle = random.nextDouble() * (Math.PI * 2.0D);
            int distance = 6 + random.nextInt(9);
            int offsetX = (int) Math.round(Math.cos(angle) * distance);
            int offsetZ = (int) Math.round(Math.sin(angle) * distance);
            BlockPos basePos = player.blockPosition().offset(offsetX, 0, offsetZ);

            for (int yShift = 6; yShift >= -8; yShift--) {
                BlockPos checkPos = basePos.offset(0, yShift, 0);
                BlockPos signPos = checkPos.above();

                if (!level.getBlockState(checkPos).isSolidRender(level, checkPos)) {
                    continue;
                }

                if (!canSoftPlace(level, signPos) || !canSoftPlace(level, signPos.above())) {
                    continue;
                }

                clearSoft(level, signPos);
                clearSoft(level, signPos.above());
                return signPos;
            }
        }

        return null;
    }

    private static String generateDate(RandomSource random, int startYear, int endYear) {
        int year = startYear + random.nextInt(endYear - startYear + 1);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        return String.format("%04d/%02d/%02d", year, month, day);
    }

    private static boolean canSoftPlace(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }

    private static void clearSoft(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (!state.isAir() && state.canBeReplaced()) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }
}
