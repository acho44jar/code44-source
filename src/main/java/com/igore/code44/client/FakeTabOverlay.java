package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = Code44Mod.MODID, value = Dist.CLIENT)
public final class FakeTabOverlay {
    private static final ResourceLocation EFBUI_FACE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "textures/entity/e44efbui.png");
    private static final int FACE_SIZE = 8;
    private static final int ROW_HEIGHT = 9;
    private static final int PING_WIDTH = 10;
    private static final int PADDING = 4;
    private static boolean fakeTabUnlocked;

    private FakeTabOverlay() {
    }

    @SubscribeEvent
    public static void onSystemChatReceived(ClientChatReceivedEvent.System event) {
        unlockFromMessage(event.getMessage().getString());
    }

    @SubscribeEvent
    public static void onPlayerChatReceived(ClientChatReceivedEvent.Player event) {
        unlockFromMessage(event.getMessage().getString());
    }

    private static void unlockFromMessage(String message) {
        String normalized = message.toLowerCase(Locale.ROOT);
        if (normalized.contains("efbui") && normalized.contains("joined")) {
            fakeTabUnlocked = true;
        }
    }

    @SubscribeEvent
    public static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        fakeTabUnlocked = false;
    }

    @SubscribeEvent
    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        fakeTabUnlocked = false;
    }

    @SubscribeEvent
    public static void onRenderPlayerListPre(RenderGuiOverlayEvent.Pre event) {
        if (!fakeTabUnlocked) {
            return;
        }

        if (!event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_LIST.id())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !minecraft.options.keyPlayerList.isDown()) {
            return;
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderHotbarPost(RenderGuiOverlayEvent.Post event) {
        if (!fakeTabUnlocked) {
            return;
        }

        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !minecraft.options.keyPlayerList.isDown()) {
            return;
        }

        AbstractClientPlayer player = minecraft.player;
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Component playerName = player.getName();
        Component efbuiName = Component.literal("efbui");
        int maxNameWidth = Math.max(minecraft.font.width(playerName), minecraft.font.width(efbuiName));
        int panelWidth = (PADDING * 2) + FACE_SIZE + 3 + maxNameWidth + 8 + PING_WIDTH;
        int panelHeight = (PADDING * 2) + (ROW_HEIGHT * 2);
        int x = (minecraft.getWindow().getGuiScaledWidth() - panelWidth) / 2;
        int y = 6;

        guiGraphics.fill(x, y, x + panelWidth, y + panelHeight, 0x88000000);

        drawEntry(guiGraphics, minecraft, player.getSkinTextureLocation(), playerName, x + PADDING, y + PADDING, panelWidth, 5);
        drawEntry(guiGraphics, minecraft, EFBUI_FACE_TEXTURE, efbuiName, x + PADDING, y + PADDING + ROW_HEIGHT, panelWidth, 5);
    }

    private static void drawEntry(
            GuiGraphics guiGraphics,
            Minecraft minecraft,
            ResourceLocation skinTexture,
            Component name,
            int x,
            int y,
            int panelWidth,
            int signalBars
    ) {
        PlayerFaceRenderer.draw(guiGraphics, skinTexture, x, y, FACE_SIZE);
        guiGraphics.drawString(minecraft.font, name, x + FACE_SIZE + 3, y, 0xFFFFFF, false);
        int pingX = x + panelWidth - (PADDING * 2) - PING_WIDTH;
        drawVanillaLikeSignalBars(guiGraphics, pingX, y + 1, signalBars);
    }

    private static void drawVanillaLikeSignalBars(GuiGraphics guiGraphics, int x, int y, int bars) {
        int clampedBars = Math.max(0, Math.min(5, bars));
        for (int i = 0; i < 5; i++) {
            int barHeight = 1 + i;
            int barLeft = x + (i * 2);
            int barTop = y + 5 - barHeight;
            int color = i < clampedBars ? 0xFF6BFF6B : 0xFF4A4A4A;
            guiGraphics.fill(barLeft, barTop, barLeft + 1, y + 5, color);
        }
    }
}