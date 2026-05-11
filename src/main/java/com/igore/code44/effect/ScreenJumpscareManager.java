package com.igore.code44.effect;

import com.igore.code44.network.ModNetworking;
import com.igore.code44.network.packet.LeaveNowScreamerPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public final class ScreenJumpscareManager {
    private ScreenJumpscareManager() {
    }

    public static void showLeaveNow(ServerPlayer player) {
        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new LeaveNowScreamerPacket());
    }

    public static void showText(ServerPlayer player, Component text) {
        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new LeaveNowScreamerPacket());
    }
}
