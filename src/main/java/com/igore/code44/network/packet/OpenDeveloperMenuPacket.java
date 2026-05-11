package com.igore.code44.network.packet;

import com.igore.code44.client.DeveloperUnlockScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class OpenDeveloperMenuPacket {
    public OpenDeveloperMenuPacket() {
    }

    public static void encode(OpenDeveloperMenuPacket packet, FriendlyByteBuf buffer) {
    }

    public static OpenDeveloperMenuPacket decode(FriendlyByteBuf buffer) {
        return new OpenDeveloperMenuPacket();
    }

    public static void handle(OpenDeveloperMenuPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                Minecraft.getInstance().setScreen(new DeveloperUnlockScreen())));
        context.setPacketHandled(true);
    }
}
