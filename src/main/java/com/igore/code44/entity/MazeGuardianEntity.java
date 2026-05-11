package com.igore.code44.entity;

import com.igore.code44.effect.MazeDimensionManager;
import com.igore.code44.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class MazeGuardianEntity extends Monster {
    private static final String WATCH_TARGET_KEY = "WatchTarget";
    private UUID watchingTarget;
    private int vanishTicks = -1;

    public MazeGuardianEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setCustomName(Component.literal("maze guardian"));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    public void setWatchingTarget(UUID watchingTarget) {
        this.watchingTarget = watchingTarget;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }

        ServerPlayer player = resolveWatchingPlayer();
        if (player == null || !player.isAlive()) {
            this.discard();
            return;
        }

        this.getLookControl().setLookAt(player, 60.0F, 60.0F);
        facePlayer(player);

        if (vanishTicks >= 0) {
            vanishTicks--;
            if (vanishTicks <= 0) {
                this.discard();
            }
            return;
        }

        if (isPlayerLookingAtMe(player)) {
            vanishImmediately(player);
        } else if (this.distanceTo(player) <= 4.0F) {
            startRetreatFrom(player);
        } else {
            this.getNavigation().stop();
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    private void facePlayer(ServerPlayer player) {
        double dx = player.getX() - this.getX();
        double dz = player.getZ() - this.getZ();
        float yaw = (float) (Mth.atan2(dz, dx) * (180.0F / (float) Math.PI)) - 90.0F;
        this.setYRot(yaw);
        this.yBodyRot = yaw;
        this.yHeadRot = yaw;
    }

    private boolean isPlayerLookingAtMe(ServerPlayer player) {
        Vec3 playerLook = player.getLookAngle().normalize();
        Vec3 toGuardian = this.position().add(0.0D, this.getBbHeight() * 0.6D, 0.0D).subtract(player.getEyePosition()).normalize();
        double dot = playerLook.dot(toGuardian);
        return dot > 0.92D && player.hasLineOfSight(this) && player.distanceTo(this) <= 40.0F;
    }

    private void startRetreatFrom(ServerPlayer player) {
        Vec3 away = this.position().subtract(player.position()).normalize();
        if (away.lengthSqr() < 1.0E-4D) {
            away = new Vec3(1.0D, 0.0D, 0.0D);
        }

        double retreatDistance = 3.0D + this.random.nextDouble();
        BlockPos targetPos = BlockPos.containing(
                this.getX() + (away.x * retreatDistance),
                this.getY(),
                this.getZ() + (away.z * retreatDistance)
        );

        if (!isWalkableTarget(targetPos)) {
            targetPos = this.blockPosition();
        }

        MazeDimensionManager.markGuardianRelocation(player, this.blockPosition());
        MazeDimensionManager.startGuardianCooldown(player);
        this.getNavigation().moveTo(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D, 1.1D);
        this.vanishTicks = 25;
    }

    private void vanishImmediately(ServerPlayer player) {
        MazeDimensionManager.markGuardianRelocation(player, this.blockPosition());
        MazeDimensionManager.startGuardianCooldown(player);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.MAZEGUARDIANUNSPAWNED.get(), SoundSource.HOSTILE, 1.8F, 1.0F);
        this.discard();
    }

    private boolean isWalkableTarget(BlockPos targetPos) {
        return this.level().getBlockState(targetPos).is(Blocks.STONE_BRICKS)
                && this.level().getBlockState(targetPos.above()).isAir()
                && this.level().getBlockState(targetPos.above(2)).isAir();
    }

    private ServerPlayer resolveWatchingPlayer() {
        if (!(this.level() instanceof ServerLevel serverLevel) || watchingTarget == null) {
            return null;
        }
        return serverLevel.getServer().getPlayerList().getPlayer(watchingTarget);
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
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (watchingTarget != null) {
            tag.putUUID(WATCH_TARGET_KEY, watchingTarget);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID(WATCH_TARGET_KEY)) {
            watchingTarget = tag.getUUID(WATCH_TARGET_KEY);
        }
    }
}
