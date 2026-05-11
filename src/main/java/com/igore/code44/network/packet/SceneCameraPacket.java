package com.igore.code44.network.packet;

import com.igore.code44.client.SceneCameraController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class SceneCameraPacket {
    private final boolean active;
    private final int entityId;

    public SceneCameraPacket(boolean active, int entityId) {
        this.active = active;
        this.entityId = entityId;
    }

    public static SceneCameraPacket decode(FriendlyByteBuf buffer) {
        return new SceneCameraPacket(buffer.readBoolean(), buffer.readInt());
    }

    public static void encode(SceneCameraPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.active);
        buffer.writeInt(packet.entityId);
    }

    public static void handle(SceneCameraPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> SceneCameraController.handlePacket(packet.active, packet.entityId));
        context.setPacketHandled(true);
    }
}
