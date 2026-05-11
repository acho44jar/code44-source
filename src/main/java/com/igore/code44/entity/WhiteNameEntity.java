package com.igore.code44.entity;

import com.igore.code44.sound.HorrorSoundManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WhiteNameEntity extends Monster {
    private static final double DESPAWN_DISTANCE = 5.0D;
    private static final Component GLITCHED_NAME =
            Component.literal("efbuiefbuiefbuiefbuiefbuiefbuiefbuiefbuiefbuiefbui")
                    .withStyle(ChatFormatting.WHITE, ChatFormatting.OBFUSCATED);
    private boolean skyJumpscareTriggered = false;
    private boolean skyJumpscareResolved = false;
    private int skyJumpscareTicks = 0;

    public WhiteNameEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 0;
        this.setNoGravity(true);
        this.setCustomName(GLITCHED_NAME);
        this.setCustomNameVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
        this.setXRot(0.0F);

        if (!this.level().isClientSide) {
            Player nearestPlayer = this.level().getNearestPlayer(this, 64.0D);

            if (nearestPlayer != null) {
                this.lookAt(nearestPlayer, 180.0F, 180.0F);
                this.setYHeadRot(this.getYRot());
                this.yBodyRot = this.getYRot();
            }

            if (skyJumpscareTriggered) {
                tickSkyJumpscare(nearestPlayer);
                return;
            }

            if (shouldDisappearByDistance()) {
                this.discard();
                return;
            }

            if (shouldDisappearByLook(nearestPlayer)) {
                triggerSkyJumpscare(nearestPlayer);
            }
        }
    }

    private boolean shouldDisappearByDistance() {
        BlockState belowState = this.level().getBlockState(this.blockPosition().below());
        return belowState.isSolidRender(this.level(), this.blockPosition().below())
                && this.level().getNearestPlayer(this, DESPAWN_DISTANCE) != null;
    }

    private boolean shouldDisappearByLook(Player player) {
        if (player == null) {
            return false;
        }

        BlockState belowState = this.level().getBlockState(this.blockPosition().below());
        if (belowState.isSolidRender(this.level(), this.blockPosition().below())) {
            return false;
        }

        Vec3 toEntity = this.getEyePosition().subtract(player.getEyePosition()).normalize();
        double dot = player.getViewVector(1.0F).normalize().dot(toEntity);
        return dot > 0.9975D && player.hasLineOfSight(this);
    }

    private void triggerSkyJumpscare(Player player) {
        if (player == null || skyJumpscareTriggered) {
            return;
        }

        skyJumpscareTriggered = true;
        skyJumpscareResolved = false;
        skyJumpscareTicks = 30;
    }

    private void tickSkyJumpscare(Player player) {
        if (player != null && player.isAlive()) {
            Vec3 eyePos = player.getEyePosition();
            Vec3 targetPos = eyePos.add(0.0D, -0.9D, 0.0D);
            Vec3 delta = targetPos.subtract(this.position());
            double distance = delta.length();

            if (distance > 0.001D) {
                Vec3 move = delta.normalize().scale(Math.min(2.6D, distance));
                Vec3 nextPos = this.position().add(move);
                this.moveTo(nextPos.x, nextPos.y, nextPos.z, player.getYRot() + 180.0F, 0.0F);
            }

            this.lookAt(player, 180.0F, 180.0F);
            this.setYHeadRot(this.getYRot());
            this.yBodyRot = this.getYRot();

            if (!skyJumpscareResolved && distance <= 1.35D) {
                skyJumpscareResolved = true;

                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    HorrorSoundManager.playFakeSkyScreamer(serverPlayer);
                    serverPlayer.hurt(serverPlayer.level().damageSources().magic(), serverPlayer.getMaxHealth() * 0.5F);
                }

                skyJumpscareTicks = 8;
            }
        }

        if (--skyJumpscareTicks <= 0) {
            this.discard();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }
}
