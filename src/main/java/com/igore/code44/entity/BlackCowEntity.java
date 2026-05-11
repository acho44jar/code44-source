package com.igore.code44.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BlackCowEntity extends Cow {
    private int attackCooldownTicks;

    public BlackCowEntity(EntityType<? extends Cow> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Cow.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.42D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 24.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.attackCooldownTicks > 0) {
            this.attackCooldownTicks--;
        }

        if (this.level().isClientSide) {
            return;
        }

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Player nearestPlayer = serverLevel.getNearestPlayer(this, 24.0D);
        if (nearestPlayer == null || !this.hasLineOfSight(nearestPlayer)) {
            return;
        }

        this.setTarget(nearestPlayer);
        this.getNavigation().moveTo(nearestPlayer.getX(), nearestPlayer.getY(), nearestPlayer.getZ(), 1.65D);
        this.getLookControl().setLookAt(nearestPlayer, 60.0F, 60.0F);

        if (this.attackCooldownTicks <= 0 && this.distanceToSqr(nearestPlayer) <= 2.4D * 2.4D) {
            nearestPlayer.hurt(this.level().damageSources().mobAttack(this), 4.0F);
            this.attackCooldownTicks = 20;
        }
    }
}
