package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.registry.ModSounds;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Code44Mod.MODID, value = Dist.CLIENT)
public final class TextJumpscareOverlay {
    private static final long LEAVE_NOW_DURATION_MS = 6000L;
    private static final long LEAVE_NOW_BLINK_INTERVAL_MS = 180L;
    private static final long LEAVE_NOW_SOUND_REPEAT_MS = 2000L;
    private static long leaveNowUntilMs;
    private static long nextLeaveNowSoundMs;

    private TextJumpscareOverlay() {
    }

    public static void showLeaveNow() {
        leaveNowUntilMs = Util.getMillis() + LEAVE_NOW_DURATION_MS;
        nextLeaveNowSoundMs = 0L;
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        long now = Util.getMillis();

        if (now >= leaveNowUntilMs) {
            return;
        }

        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null && now >= nextLeaveNowSoundMs) {
            minecraft.player.playSound(ModSounds.SOS.get(), 1.0F, 1.0F);
            nextLeaveNowSoundMs = now + LEAVE_NOW_SOUND_REPEAT_MS;
        }

        if (((now / LEAVE_NOW_BLINK_INTERVAL_MS) % 2L) == 1L) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Component text = Component.literal("LEAVE NOW");
        int textWidth = minecraft.font.width(text);
        int x = minecraft.getWindow().getGuiScaledWidth() - (textWidth * 3) - 28;
        int y = 28;
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        poseStack.translate(x, y, 0.0F);
        poseStack.scale(3.0F, 3.0F, 1.0F);
        guiGraphics.drawString(minecraft.font, text, 0, 0, 0xFFFFFF, true);
        poseStack.popPose();
    }
}
