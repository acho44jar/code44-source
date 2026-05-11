package com.igore.code44.entity;

import com.igore.code44.entity.goal.FixedReachMeleeAttackGoal;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class E44efbuiEntity extends Monster {
    private static final int LIFETIME_TICKS = 300;
    private int attackCooldownTicks;
    private boolean scriptedScene;
    private int stareOnlyDurationTicks;

    public E44efbuiEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
        this.setCustomName(Component.literal("efbui"));
        this.setCustomNameVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.42D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FixedReachMeleeAttackGoal(this, 1.35D, false, 3.5D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.stareOnlyDurationTicks > 0) {
            Player nearestPlayer = this.level().getNearestPlayer(this, 32.0D);
            if (nearestPlayer != null) {
                this.getLookControl().setLookAt(nearestPlayer, 60.0F, 60.0F);
                this.lookAt(nearestPlayer, 60.0F, 60.0F);
                this.setYHeadRot(this.getYRot());
                this.yBodyRot = this.getYRot();
            }

            if (!this.level().isClientSide && this.tickCount >= this.stareOnlyDurationTicks) {
                this.discard();
                return;
            }
        }

        if (this.attackCooldownTicks > 0) {
            this.attackCooldownTicks--;
        }

        if (!this.level().isClientSide && this.stareOnlyDurationTicks <= 0 && !this.scriptedScene && this.tickCount >= LIFETIME_TICKS) {
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
    public boolean canAttack(LivingEntity target) {
        return target instanceof Player;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    public void setScriptedScene(boolean scriptedScene) {
        this.scriptedScene = scriptedScene;
    }

    public void configureStareOnly(int durationTicks) {
        this.stareOnlyDurationTicks = durationTicks;
        this.setNoAi(true);
        this.setCustomNameVisible(false);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        if (this.attackCooldownTicks > 0) {
            return false;
        }

        boolean hurt = super.doHurtTarget(target);

        if (hurt) {
            this.attackCooldownTicks = 60;
        }

        return hurt;
    }
}
