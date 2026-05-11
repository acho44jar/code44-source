package com.igore.code44.entity;

import com.igore.code44.entity.goal.FixedReachMeleeAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ObserverEntity extends Monster {
    private boolean rushOnSeen;
    private boolean rushing;

    public ObserverEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FixedReachMeleeAttackGoal(this, 1.8D, false, 2.2D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 48.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            return;
        }

        if (!(this.getTarget() instanceof ServerPlayer player)) {
            Player nearestPlayer = ((ServerLevel) this.level()).getNearestPlayer(this, 64.0D);
            if (nearestPlayer instanceof ServerPlayer serverPlayer) {
                this.setTarget(serverPlayer);
            }
            return;
        }

        this.getLookControl().setLookAt(player, 50.0F, 50.0F);
        if (this.rushOnSeen) {
            if (!this.rushing && this.hasLineOfSight(player)) {
                this.rushing = true;
                if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.95D);
                }
            }

            if (this.rushing) {
                this.getNavigation().moveTo(player, 1.5D);
            } else {
                this.getNavigation().stop();
                this.setDeltaMovement(Vec3.ZERO);
            }
            return;
        }

        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hurt = super.doHurtTarget(target);

        if (hurt && target instanceof ServerPlayer player) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 1, false, false));
            player.sendSystemMessage(Component.literal("You'll never measure up to us..."));
            this.discard();
        }

        return hurt;
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
    public boolean canAttack(LivingEntity target) {
        return target instanceof Player;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    public void configureRushOnSeen(boolean enabled) {
        this.rushOnSeen = enabled;
        this.rushing = false;
    }
}
