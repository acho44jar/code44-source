package com.igore.code44.network.packet;

import com.igore.code44.client.TextJumpscareOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class LeaveNowScreamerPacket {
    public static LeaveNowScreamerPacket decode(FriendlyByteBuf buffer) {
        return new LeaveNowScreamerPacket();
    }

    public static void encode(LeaveNowScreamerPacket packet, FriendlyByteBuf buffer) {
    }

    public static void handle(LeaveNowScreamerPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(TextJumpscareOverlay::showLeaveNow);
        context.setPacketHandled(true);
    }
}
