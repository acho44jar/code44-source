package com.igore.code44.effect;

import com.igore.code44.entity.InvisibleFortyFourEntity;
import com.igore.code44.network.ModNetworking;
import com.igore.code44.network.packet.SceneCameraPacket;
import com.igore.code44.registry.ModEntities;
import com.igore.code44.sound.HorrorSoundManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class YourEndEventManager {
    private static final int DURATION_TICKS = 200;
    private static final Map<UUID, ActiveYourEndScene> ACTIVE_SCENES = new HashMap<>();

    private YourEndEventManager() {
    }

    public static boolean isSceneActive(ServerPlayer player) {
        return ACTIVE_SCENES.containsKey(player.getUUID());
    }

    public static boolean trigger(ServerLevel level, ServerPlayer player) {
        if (ACTIVE_SCENES.containsKey(player.getUUID())) {
            return false;
        }

        InvisibleFortyFourEntity anchor = ModEntities.INVISIBLE_FORTY_FOUR.get().create(level);
        if (anchor == null) {
            return false;
        }

        Vec3 lockedPos = player.position();
        Vec3 look = player.getViewVector(1.0F).normalize();
        Vec3 lateral = new Vec3(-look.z, 0.0D, look.x).normalize().scale(level.random.nextBoolean() ? 4.5D : -4.5D);
        Vec3 anchorPos = lockedPos
                .add(look.scale(18.0D))
                .add(lateral)
                .add(0.0D, 18.0D, 0.0D);
        double anchorY = Math.min(Math.max(anchorPos.y, player.getY() + 14.0D), level.getMaxBuildHeight() - 8.0D);
        anchor.moveTo(anchorPos.x, anchorY, anchorPos.z, 0.0F, 0.0F);
        level.addFreshEntity(anchor);

        ACTIVE_SCENES.put(player.getUUID(), new ActiveYourEndScene(anchor.getUUID(), player.tickCount + DURATION_TICKS, lockedPos));
        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SceneCameraPacket(true, anchor.getId()));
        HorrorSoundManager.playYourEnd(player);
        return true;
    }

    public static void tick(ServerLevel level, ServerPlayer player) {
        ActiveYourEndScene scene = ACTIVE_SCENES.get(player.getUUID());
        if (scene == null) {
            return;
        }

        Entity anchor = level.getEntity(scene.anchorUuid());
        if (anchor == null || !anchor.isAlive()) {
            end(player, null);
            return;
        }

        player.setNoGravity(true);
        player.teleportTo(scene.lockedPos().x, scene.lockedPos().y, scene.lockedPos().z);
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;

        for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(128.0D), Mob::isAlive)) {
            mob.setTarget(null);
            mob.getNavigation().stop();
            mob.setDeltaMovement(Vec3.ZERO);
            mob.getLookControl().setLookAt(anchor, 90.0F, 90.0F);
        }

        if (player.tickCount >= scene.endTick()) {
            end(player, anchor);
        }
    }

    private static void end(ServerPlayer player, Entity anchor) {
        ACTIVE_SCENES.remove(player.getUUID());
        player.setNoGravity(false);
        player.setDeltaMovement(Vec3.ZERO);
        ModNetworking.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SceneCameraPacket(false, -1));
        if (anchor != null) {
            anchor.discard();
        }
    }

    private record ActiveYourEndScene(UUID anchorUuid, int endTick, Vec3 lockedPos) {
    }
}
