package com.igore.code44.network.packet;

import com.igore.code44.client.LateGameOverlayController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class LateGameClientEffectPacket {
    private final Type type;
    private final int durationTicks;

    public LateGameClientEffectPacket(Type type, int durationTicks) {
        this.type = type;
        this.durationTicks = durationTicks;
    }

    public static LateGameClientEffectPacket decode(FriendlyByteBuf buffer) {
        return new LateGameClientEffectPacket(buffer.readEnum(Type.class), buffer.readInt());
    }

    public static void encode(LateGameClientEffectPacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.type);
        buffer.writeInt(packet.durationTicks);
    }

    public static void handle(LateGameClientEffectPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> LateGameOverlayController.trigger(packet.type, packet.durationTicks));
        context.setPacketHandled(true);
    }

    public enum Type {
        MINIMIZE_WINDOW,
        INVERT_MOUSE,
        SENSITIVITY_SPIKE,
        MASTER_VOLUME_DROP,
        SCREEN_BLINK,
        PHOTO_SCREAMER,
        LOOK_DOWN
    }
}

