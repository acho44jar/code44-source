package com.igore.code44.entity;

import com.igore.code44.event.CommonEvents;
import com.igore.code44.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class DarkHorseBaglanEntity extends Monster {
    private static final String WATCH_TARGET_KEY = "WatchTarget";
    private UUID watchedPlayer;

    public DarkHorseBaglanEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setCustomName(Component.literal("DarkHorseBaglan"));
        this.setCustomNameVisible(true);
        this.setNoAi(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    public void setWatchedPlayer(UUID watchedPlayer) {
        this.watchedPlayer = watchedPlayer;
    }

    @Override
    public void tick() {
        super.tick();
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);

        if (this.level().isClientSide) {
            return;
        }

        ServerPlayer target = resolveWatchedPlayer();
        if (target == null || !target.isAlive()) {
            return;
        }

        this.getLookControl().setLookAt(target, 80.0F, 80.0F);
        facePlayer(target);
    }

    private void facePlayer(ServerPlayer player) {
        double dx = player.getX() - this.getX();
        double dz = player.getZ() - this.getZ();
        float yaw = (float) (Mth.atan2(dz, dx) * (180.0F / (float) Math.PI)) - 90.0F;
        this.setYRot(yaw);
        this.yBodyRot = yaw;
        this.yHeadRot = yaw;
    }

    private ServerPlayer resolveWatchedPlayer() {
        if (!(this.level() instanceof ServerLevel serverLevel) || this.watchedPlayer == null) {
            return null;
        }
        return serverLevel.getServer().getPlayerList().getPlayer(this.watchedPlayer);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (!this.level().isClientSide && this.isDeadOrDying()) {
            if (source.getEntity() instanceof ServerPlayer serverPlayer && this.level() instanceof ServerLevel serverLevel) {
                serverPlayer.playNotifySound(ModSounds.FAKEPLAYERINCHATHELLO.get(), SoundSource.HOSTILE, 2.0F, 1.0F);
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 16, 0, false, true, true));
                serverPlayer.sendSystemMessage(
                        Component.literal("DarkHorseBaglan was slain by ")
                                .append(serverPlayer.getName().copy().withStyle(ChatFormatting.WHITE))
                );
                CommonEvents.startFakeHelloAftermath(serverPlayer);
            } else if (source.getEntity() instanceof Player player) {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 16, 0, false, true, true));
            }
        }
        return hurt;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.watchedPlayer != null) {
            tag.putUUID(WATCH_TARGET_KEY, this.watchedPlayer);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID(WATCH_TARGET_KEY)) {
            this.watchedPlayer = tag.getUUID(WATCH_TARGET_KEY);
        }
    }
}
