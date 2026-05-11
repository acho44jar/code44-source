package com.igore.code44.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.UUID;

public class SkinwalkerEntity extends Monster {
    private static final EntityDataAccessor<Optional<UUID>> MIMIC_UUID =
            SynchedEntityData.defineId(SkinwalkerEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final int ACTION_DELAY_TICKS = 12;
    private final ArrayDeque<ActionSnapshot> actionQueue = new ArrayDeque<>();
    private boolean saidCloseLine = false;
    private boolean noticedPlayer = false;

    public SkinwalkerEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MIMIC_UUID, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            Player player = getMimickedPlayer();
            if (player != null) {
                syncAppearance(player);
                capturePlayerAction(player);
                applyDelayedAction(player);
                tickCloseLine(player);
            }
        }
    }

    public void mimicPlayer(Player player) {
        this.entityData.set(MIMIC_UUID, Optional.of(player.getUUID()));
        syncAppearance(player);
    }

    public UUID getMimickedPlayerUuid() {
        return this.entityData.get(MIMIC_UUID).orElse(null);
    }

    public Player getMimickedPlayer() {
        UUID uuid = getMimickedPlayerUuid();
        return uuid == null ? null : this.level().getPlayerByUUID(uuid);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected Component getTypeName() {
        return Component.literal("skinwalker");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        UUID uuid = getMimickedPlayerUuid();
        if (uuid != null) {
            tag.putUUID("MimicUuid", uuid);
        }
        tag.putBoolean("SaidCloseLine", saidCloseLine);
        tag.putBoolean("NoticedPlayer", noticedPlayer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("MimicUuid")) {
            this.entityData.set(MIMIC_UUID, Optional.of(tag.getUUID("MimicUuid")));
        }
        saidCloseLine = tag.getBoolean("SaidCloseLine");
        noticedPlayer = tag.getBoolean("NoticedPlayer");
    }

    private void syncAppearance(Player player) {
        this.setCustomName(player.getName().copy());
        this.setCustomNameVisible(true);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setItemSlot(slot, player.getItemBySlot(slot).copy());
        }
    }

    private void capturePlayerAction(Player player) {
        Vec3 horizontal = new Vec3(player.getDeltaMovement().x, 0.0D, player.getDeltaMovement().z);
        actionQueue.addLast(new ActionSnapshot(
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYRot(),
                player.getXRot(),
                player.isCrouching(),
                player.isSprinting(),
                player.swinging,
                player.isUsingItem(),
                player.getDeltaMovement().y > 0.2D,
                horizontal.length()
        ));

        while (actionQueue.size() > ACTION_DELAY_TICKS + 4) {
            actionQueue.removeFirst();
        }
    }

    private void applyDelayedAction(Player player) {
        if (!noticedPlayer) {
            if (isPlayerLookingAtMe(player)) {
                noticedPlayer = true;
            } else {
                this.getNavigation().stop();
                this.setDeltaMovement(Vec3.ZERO);
                this.lookAt(player, 180.0F, 180.0F);
                this.setYHeadRot(this.getYRot());
                this.yBodyRot = this.getYRot();
                return;
            }
        }

        if (actionQueue.size() <= ACTION_DELAY_TICKS) {
            this.lookAt(player, 180.0F, 180.0F);
            this.setYHeadRot(this.getYRot());
            this.yBodyRot = this.getYRot();
            return;
        }

        ActionSnapshot snapshot = actionQueue.removeFirst();
        this.setShiftKeyDown(snapshot.crouching());
        this.setSprinting(snapshot.sprinting());
        this.lookAt(player, 180.0F, 180.0F);
        this.setYHeadRot(this.getYRot());
        this.yBodyRot = this.getYRot();
        this.setXRot(snapshot.xRot() * 0.35F);

        double distanceToPlayer = this.distanceToSqr(player);
        this.getNavigation().moveTo(player, snapshot.sprinting() ? 1.1D : 0.92D);

        if (snapshot.swinging() && this.swingTime == 0) {
            this.swing(InteractionHand.MAIN_HAND);
        }

        if (snapshot.usingItem()) {
            this.startUsingItem(InteractionHand.MAIN_HAND);
        } else if (this.isUsingItem()) {
            this.stopUsingItem();
        }

        if (snapshot.jumping() && this.onGround()) {
            this.jumpFromGround();
        }
    }

    private void tickCloseLine(Player player) {
        if (!saidCloseLine && this.distanceTo(player) <= 5.0F) {
            player.sendSystemMessage(Component.literal("Hey, is that me?"));
            saidCloseLine = true;
        }

        if (noticedPlayer && this.distanceTo(player) <= 2.75F) {
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                com.igore.code44.sound.HorrorSoundManager.playSkinwalker(serverPlayer);
            }
            this.discard();
        }
    }

    private boolean isPlayerLookingAtMe(Player player) {
        Vec3 playerEyes = player.getEyePosition();
        Vec3 toSkinwalker = this.getEyePosition().subtract(playerEyes).normalize();
        double dot = player.getViewVector(1.0F).normalize().dot(toSkinwalker);
        return dot > 0.95D && player.hasLineOfSight(this);
    }

    private record ActionSnapshot(
            double x,
            double y,
            double z,
            float yRot,
            float xRot,
            boolean crouching,
            boolean sprinting,
            boolean swinging,
            boolean usingItem,
            boolean jumping,
            double horizontalSpeed
    ) {
    }
}
