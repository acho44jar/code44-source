package com.igore.code44.entity;

import com.igore.code44.effect.PastMistakesFieldManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class PastMistakesDoorEntity extends Monster {
    private static final Component LABEL = Component.literal("your past mistakes");

    public PastMistakesDoorEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 0;
        this.noPhysics = true;
        this.setNoGravity(true);
        this.setCustomName(LABEL);
        this.setCustomNameVisible(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.FOLLOW_RANGE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(0.0D, 0.0D, 0.0D);
        this.setXRot(0.0F);
        this.setYHeadRot(this.getYRot());
        this.yBodyRot = this.getYRot();

        if (!this.level().isClientSide && this.tickCount % 5 == 0 && this.level() instanceof ServerLevel serverLevel) {
            Player nearestPlayer = serverLevel.getNearestPlayer(this, 2.8D);
            if (nearestPlayer instanceof ServerPlayer player && PastMistakesFieldManager.startScene(serverLevel, player)) {
                this.discard();
            }
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

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
}
