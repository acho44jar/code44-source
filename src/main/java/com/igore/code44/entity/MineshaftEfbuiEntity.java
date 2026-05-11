package com.igore.code44.entity;

import com.igore.code44.entity.goal.FixedReachMeleeAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MineshaftEfbuiEntity extends Monster {
    private static final int LIFETIME_TICKS = 600;
    private static final int DIG_INTERVAL_TICKS = 12;
    private int nextDigTick;

    public MineshaftEfbuiEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 0;
        this.setCustomName(Component.literal("efbui"));
        this.setCustomNameVisible(false);
        equipLoadout();
    }

    private void equipLoadout() {
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_PICKAXE));
        this.setDropChance(EquipmentSlot.HEAD, 0.0F);
        this.setDropChance(EquipmentSlot.CHEST, 0.0F);
        this.setDropChance(EquipmentSlot.LEGS, 0.0F);
        this.setDropChance(EquipmentSlot.FEET, 0.0F);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FixedReachMeleeAttackGoal(this, 1.35D, false, 2.4D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 32.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.tickCount >= LIFETIME_TICKS) {
            this.discard();
            return;
        }

        if (!this.level().isClientSide && this.getTarget() instanceof ServerPlayer player) {
            if (this.tickCount >= this.nextDigTick) {
                digTunnelTowards((ServerLevel) this.level(), player);
                this.nextDigTick = this.tickCount + DIG_INTERVAL_TICKS;
            }
            this.getNavigation().moveTo(player, 0.85D);
        }
    }

    private void digTunnelTowards(ServerLevel level, ServerPlayer player) {
        Vec3 direction = new Vec3(
                player.getX() - this.getX(),
                0.0D,
                player.getZ() - this.getZ()
        );

        if (direction.lengthSqr() < 0.001D) {
            direction = this.getLookAngle();
        } else {
            direction = direction.normalize();
        }

        BlockPos aheadPos = BlockPos.containing(this.getX() + direction.x * 1.2D, this.getY(), this.getZ() + direction.z * 1.2D);
        BlockPos belowAhead = aheadPos.below();
        BlockState belowState = level.getBlockState(belowAhead);

        if (belowState.liquid()) {
            level.setBlockAndUpdate(belowAhead, Blocks.COBBLESTONE.defaultBlockState());
        }

        boolean brokeBlock = false;

        for (BlockPos pos : BlockPos.betweenClosed(
                aheadPos.getX(), aheadPos.getY(), aheadPos.getZ(),
                aheadPos.getX(), aheadPos.getY() + 1, aheadPos.getZ())) {
            BlockState state = level.getBlockState(pos);

            if (state.isAir() || state.liquid()) {
                continue;
            }

            if (state.is(Blocks.BEDROCK) || state.is(Blocks.BARRIER) || state.is(Blocks.END_PORTAL_FRAME)) {
                continue;
            }

            if (state.getDestroySpeed(level, pos) < 0.0F) {
                continue;
            }

            level.destroyBlock(pos, false, this);
            brokeBlock = true;
        }

        BlockPos tunnelFloor = aheadPos.below();
        if (level.getBlockState(tunnelFloor).isAir()) {
            level.setBlockAndUpdate(tunnelFloor, Blocks.COBBLESTONE.defaultBlockState());
        }

        if (brokeBlock) {
            this.swing(InteractionHand.MAIN_HAND);
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        if (target instanceof ServerPlayer player) {
            player.connection.disconnect(Component.literal("It's not your world"));
            this.discard();
            return true;
        }

        return super.doHurtTarget(target);
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
}
