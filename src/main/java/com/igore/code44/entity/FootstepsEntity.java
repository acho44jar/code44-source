package com.igore.code44.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.Vec3;

public class FootstepsEntity extends Monster {
    private static final int LIFETIME_TICKS = 220;
    private int nextMoveTick;
    private int nextStepTick;

    public FootstepsEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = false;
        this.setInvisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            return;
        }

        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }

        if (this.tickCount >= LIFETIME_TICKS) {
            this.discard();
            return;
        }

        ServerPlayer player = resolvePlayer(level);
        if (player == null) {
            this.discard();
            return;
        }

        if (this.tickCount >= this.nextMoveTick) {
            moveAroundPlayer(level, player);
            this.nextMoveTick = this.tickCount + 20 + level.random.nextInt(25);
        }

        if (this.tickCount >= this.nextStepTick) {
            playFootstep(level);
            this.nextStepTick = this.tickCount + 10 + level.random.nextInt(8);
        }
    }

    private ServerPlayer resolvePlayer(ServerLevel level) {
        if (this.getTarget() instanceof ServerPlayer player && player.isAlive()) {
            return player;
        }

        Player nearest = level.getNearestPlayer(this, 24.0D);
        if (nearest instanceof ServerPlayer player && player.isAlive()) {
            this.setTarget(player);
            return player;
        }

        return null;
    }

    private void moveAroundPlayer(ServerLevel level, ServerPlayer player) {
        RandomSource random = level.random;
        double angle = random.nextDouble() * (Math.PI * 2.0D);
        int distance = 3 + random.nextInt(6);
        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;
        double y = player.getY();

        this.getNavigation().moveTo(x, y, z, 1.15D);
    }

    private void playFootstep(ServerLevel level) {
        BlockPos below = this.blockPosition().below();
        BlockState belowState = level.getBlockState(below);
        SoundEvent stepSound = belowState.getSoundType(level, below, this).getStepSound();

        if (stepSound == null || belowState.isAir() || belowState.instrument() == NoteBlockInstrument.HARP && belowState.getSoundType().getStepSound() == null) {
            stepSound = net.minecraft.sounds.SoundEvents.STONE_STEP;
        }

        level.playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                stepSound,
                SoundSource.HOSTILE,
                0.75F,
                0.85F + level.random.nextFloat() * 0.3F
        );
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
        return false;
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
    public boolean isNoGravity() {
        return false;
    }
}
