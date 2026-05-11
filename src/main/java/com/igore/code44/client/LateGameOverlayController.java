package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import com.igore.code44.network.packet.LateGameClientEffectPacket;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Code44Mod.MODID, value = Dist.CLIENT)
public final class LateGameOverlayController {
    private static final ResourceLocation PHOTO_SCREAMER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Code44Mod.MODID, "textures/gui/photo_screamer.jpg");
    private static long effectUntilMs;
    private static LateGameClientEffectPacket.Type activeType;
    private static Boolean previousInvertMouse;
    private static Double previousSensitivity;
    private static Double previousMasterVolume;

    private LateGameOverlayController() {
    }

    public static void trigger(LateGameClientEffectPacket.Type type, int durationTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        if (type == LateGameClientEffectPacket.Type.LOOK_DOWN) {
            if (minecraft.player != null) {
                minecraft.player.setXRot(90.0F);
                minecraft.player.setYHeadRot(minecraft.player.getYRot());
                minecraft.player.setYBodyRot(minecraft.player.getYRot());
            }
            return;
        }

        if (type == LateGameClientEffectPacket.Type.MINIMIZE_WINDOW) {
            GLFW.glfwIconifyWindow(minecraft.getWindow().getWindow());
            return;
        }

        if (type == LateGameClientEffectPacket.Type.INVERT_MOUSE) {
            OptionInstance<Boolean> invertOption = minecraft.options.invertYMouse();
            previousInvertMouse = invertOption.get();
            invertOption.set(!previousInvertMouse);
            minecraft.options.save();
        } else if (type == LateGameClientEffectPacket.Type.SENSITIVITY_SPIKE) {
            OptionInstance<Double> sensitivityOption = minecraft.options.sensitivity();
            previousSensitivity = sensitivityOption.get();
            sensitivityOption.set(1.0D);
            minecraft.options.save();
        } else if (type == LateGameClientEffectPacket.Type.MASTER_VOLUME_DROP) {
            OptionInstance<Double> masterVolumeOption = minecraft.options.getSoundSourceOptionInstance(SoundSource.MASTER);
            previousMasterVolume = masterVolumeOption.get();
            masterVolumeOption.set(0.0D);
            minecraft.options.save();
        }

        activeType = type;
        effectUntilMs = Util.getMillis() + (durationTicks * 50L);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (activeType == null || Util.getMillis() < effectUntilMs) {
            return;
        }
        clearTemporaryOptions();
    }

    @SubscribeEvent
    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clearTemporaryOptions();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        long now = Util.getMillis();
        if (activeType == null || now >= effectUntilMs) {
            return;
        }

        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();

        if (activeType == LateGameClientEffectPacket.Type.SCREEN_BLINK) {
            guiGraphics.fill(0, 0, width, height, 0xFF000000);
            return;
        }

        if (activeType == LateGameClientEffectPacket.Type.PHOTO_SCREAMER) {
            guiGraphics.blit(PHOTO_SCREAMER_TEXTURE, 0, 0, 0.0F, 0.0F, width, height, width, height);
            return;
        }
    }

    private static void clearTemporaryOptions() {
        Minecraft minecraft = Minecraft.getInstance();
        if (activeType == LateGameClientEffectPacket.Type.INVERT_MOUSE && previousInvertMouse != null) {
            minecraft.options.invertYMouse().set(previousInvertMouse);
            previousInvertMouse = null;
            minecraft.options.save();
        }
        if (activeType == LateGameClientEffectPacket.Type.SENSITIVITY_SPIKE && previousSensitivity != null) {
            minecraft.options.sensitivity().set(previousSensitivity);
            previousSensitivity = null;
            minecraft.options.save();
        }
        if (activeType == LateGameClientEffectPacket.Type.MASTER_VOLUME_DROP && previousMasterVolume != null) {
            minecraft.options.getSoundSourceOptionInstance(SoundSource.MASTER).set(previousMasterVolume);
            previousMasterVolume = null;
            minecraft.options.save();
        }
        activeType = null;
    }

}

