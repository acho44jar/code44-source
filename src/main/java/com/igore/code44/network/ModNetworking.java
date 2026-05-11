package com.igore.code44.network;

import com.igore.code44.Code44Mod;
import com.igore.code44.network.packet.DeveloperUnlockPacket;
import com.igore.code44.network.packet.LateGameClientEffectPacket;
import com.igore.code44.network.packet.LeaveNowScreamerPacket;
import com.igore.code44.network.packet.OpenDeveloperMenuPacket;
import com.igore.code44.network.packet.SceneCameraPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();
    private static int nextId;

    private ModNetworking() {
    }

    public static void register() {
        CHANNEL.registerMessage(
                nextId++,
                LeaveNowScreamerPacket.class,
                LeaveNowScreamerPacket::encode,
                LeaveNowScreamerPacket::decode,
                LeaveNowScreamerPacket::handle
        );
        CHANNEL.registerMessage(
                nextId++,
                SceneCameraPacket.class,
                SceneCameraPacket::encode,
                SceneCameraPacket::decode,
                SceneCameraPacket::handle
        );
        CHANNEL.registerMessage(
                nextId++,
                OpenDeveloperMenuPacket.class,
                OpenDeveloperMenuPacket::encode,
                OpenDeveloperMenuPacket::decode,
                OpenDeveloperMenuPacket::handle
        );
        CHANNEL.registerMessage(
                nextId++,
                DeveloperUnlockPacket.class,
                DeveloperUnlockPacket::encode,
                DeveloperUnlockPacket::decode,
                DeveloperUnlockPacket::handle
        );
        CHANNEL.registerMessage(
                nextId++,
                LateGameClientEffectPacket.class,
                LateGameClientEffectPacket::encode,
                LateGameClientEffectPacket::decode,
                LateGameClientEffectPacket::handle
        );
    }
}
