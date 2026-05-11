package com.igore.code44.entity;

import com.igore.code44.effect.PastMistakesFieldManager;
import com.igore.code44.registry.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class WhiteDoorEntity extends Monster {
    private static final int REVEAL_DELAY_TICKS = 20 * 60;
    private static final int SOUND_INTERVAL_TICKS = 20 * 8;
    private static final int FIRST_SOUND_TICK = 20;
    private static final double SOUND_TRIGGER_RADIUS = 15.0D;

    public WhiteDoorEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 0;
        this.noPhysics = true;
        this.setNoGravity(true);
        this.setInvisible(true);
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
        this.setInvisible(this.tickCount < REVEAL_DELAY_TICKS);

        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            if (this.tickCount < REVEAL_DELAY_TICKS) {
                return;
            }

            if (this.tickCount % 5 == 0) {
                Player nearestPlayer = serverLevel.getNearestPlayer(this, 2.8D);
                if (nearestPlayer instanceof ServerPlayer player && PastMistakesFieldManager.exitScene(player)) {
                    return;
                }
            }

            if (this.tickCount >= FIRST_SOUND_TICK
                    && this.tickCount % SOUND_INTERVAL_TICKS == FIRST_SOUND_TICK
                    && serverLevel.getNearestPlayer(this, SOUND_TRIGGER_RADIUS) != null) {
                serverLevel.playSound(null, this.getX(), this.getY() + 1.0D, this.getZ(), ModSounds.WHITEDOOR.get(), SoundSource.AMBIENT, 2.3F, 1.0F);
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
