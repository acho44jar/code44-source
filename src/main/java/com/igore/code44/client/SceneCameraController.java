package com.igore.code44.client;

import com.igore.code44.Code44Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Code44Mod.MODID, value = Dist.CLIENT)
public final class SceneCameraController {
    private static boolean active;
    private static int targetEntityId = -1;

    private SceneCameraController() {
    }

    public static void handlePacket(boolean sceneActive, int entityId) {
        Minecraft minecraft = Minecraft.getInstance();
        active = sceneActive;
        targetEntityId = sceneActive ? entityId : -1;

        if (!sceneActive && minecraft.player != null) {
            minecraft.setCameraEntity(minecraft.player);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        if (!active) {
            if (minecraft.getCameraEntity() != player) {
                minecraft.setCameraEntity(player);
            }
            return;
        }

        Entity target = player.level().getEntity(targetEntityId);
        if (minecraft.getCameraEntity() != player) {
            minecraft.setCameraEntity(player);
        }

        if (target != null) {
            Vec3 playerEyes = player.getEyePosition();
            Vec3 targetEyes = target.getEyePosition();
            Vec3 delta = targetEyes.subtract(playerEyes);
            double horizontal = Math.sqrt((delta.x * delta.x) + (delta.z * delta.z));
            float yaw = (float) (Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0D);
            float pitch = (float) (-Math.toDegrees(Math.atan2(delta.y, horizontal)));

            player.setYRot(yaw);
            player.setYHeadRot(yaw);
            player.setYBodyRot(yaw);
            player.setXRot(pitch);
            player.yRotO = yaw;
            player.xRotO = pitch;
        }

        player.input.leftImpulse = 0.0F;
        player.input.forwardImpulse = 0.0F;
        player.input.jumping = false;
        player.input.shiftKeyDown = false;
        minecraft.options.keyUp.setDown(false);
        minecraft.options.keyDown.setDown(false);
        minecraft.options.keyLeft.setDown(false);
        minecraft.options.keyRight.setDown(false);
        minecraft.options.keyJump.setDown(false);
        minecraft.options.keyShift.setDown(false);
        minecraft.options.keySprint.setDown(false);
    }
}
