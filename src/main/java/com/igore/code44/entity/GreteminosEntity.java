package com.igore.code44.entity;

import com.igore.code44.entity.goal.FixedReachMeleeAttackGoal;
import com.igore.code44.registry.ModSounds;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.phys.Vec3;

public class GreteminosEntity extends Monster {
    private static final EntityDataAccessor<Boolean> ENRAGED =
            SynchedEntityData.defineId(GreteminosEntity.class, EntityDataSerializers.BOOLEAN);
    private static final int LIFETIME_TICKS = 20 * 40;
    private int rushCooldownTicks;

    public GreteminosEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setCustomName(Component.literal("Greteminos"));
        this.setCustomNameVisible(false);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.35D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ENRAGED, false);
    }

    public boolean isEnraged() {
        return this.entityData.get(ENRAGED);
    }

    private void setEnraged(boolean enraged) {
        this.entityData.set(ENRAGED, enraged);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FixedReachMeleeAttackGoal(this, 1.5D, false, 2.6D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 48.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.rushCooldownTicks > 0) {
            this.rushCooldownTicks--;
        }

        if (!this.level().isClientSide && this.tickCount % 2 == 0 && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.BLACK_CONCRETE_POWDER.defaultBlockState()),
                    this.getX(),
                    this.getY() + 1.1D,
                    this.getZ(),
                    14,
                    0.7D,
                    1.05D,
                    0.7D,
                    0.02D
            );
        }

        if (!this.level().isClientSide && this.tickCount >= LIFETIME_TICKS) {
            this.discard();
            return;
        }

        if (!(this.level() instanceof ServerLevel) || !(this.getTarget() instanceof Player player)) {
            if (!this.level().isClientSide) {
                this.setEnraged(false);
            }
            return;
        }

        this.getLookControl().setLookAt(player, 60.0F, 60.0F);

        boolean shouldEnrage = this.hasLineOfSight(player)
                && this.distanceToSqr(player) <= (20.0D * 20.0D)
                && this.rushCooldownTicks <= 18;

        if (!this.level().isClientSide) {
            this.setEnraged(shouldEnrage);
        }

        if (this.rushCooldownTicks <= 0 && this.hasLineOfSight(player) && this.distanceToSqr(player) <= (18.0D * 18.0D)) {
            Vec3 dash = player.position().subtract(this.position()).normalize().scale(1.15D);
            this.setDeltaMovement(dash.x, 0.12D, dash.z);
            this.hasImpulse = true;
            this.rushCooldownTicks = 35;
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt) {
            this.rushCooldownTicks = 45;
            if (!this.level().isClientSide) {
                this.setEnraged(true);
                this.discard();
            }
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

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GRETEMINOS.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 100;
    }
}
