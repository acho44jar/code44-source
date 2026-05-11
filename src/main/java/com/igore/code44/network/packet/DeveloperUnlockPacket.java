package com.igore.code44.network.packet;

import com.igore.code44.event.CommonEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class DeveloperUnlockPacket {
    public DeveloperUnlockPacket() {
    }

    public static void encode(DeveloperUnlockPacket packet, FriendlyByteBuf buffer) {
    }

    public static DeveloperUnlockPacket decode(FriendlyByteBuf buffer) {
        return new DeveloperUnlockPacket();
    }

    public static void handle(DeveloperUnlockPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                CommonEvents.enableDeveloperCommands(player);
            }
        });
        context.setPacketHandled(true);
    }
}
