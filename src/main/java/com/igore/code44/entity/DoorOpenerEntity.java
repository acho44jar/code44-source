package com.igore.code44.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

public class DoorOpenerEntity extends Monster {
    private static final int TOGGLE_DURATION_TICKS = 200;
    private BlockPos doorBasePos;
    private int nextToggleTick;
    private int nextDoorSearchTick;
    private int toggleEndTick = -1;

    public DoorOpenerEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setInvisible(true);
        this.noPhysics = false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
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

        if (this.doorBasePos == null || this.tickCount >= this.nextDoorSearchTick) {
            this.doorBasePos = EntitySpawnManager.findNearestDoor(level, this.blockPosition(), 20);
            this.nextDoorSearchTick = this.tickCount + 20;
        }

        if (this.doorBasePos == null) {
            this.discard();
            return;
        }

        double distanceToDoor = this.position().distanceToSqr(
                this.doorBasePos.getX() + 0.5D,
                this.doorBasePos.getY(),
                this.doorBasePos.getZ() + 0.5D
        );

        if (distanceToDoor > 3.0D) {
            this.getNavigation().moveTo(
                    this.doorBasePos.getX() + 0.5D,
                    this.doorBasePos.getY(),
                    this.doorBasePos.getZ() + 0.5D,
                    0.75D
            );
            return;
        }

        this.getNavigation().stop();

        if (this.toggleEndTick < 0) {
            this.toggleEndTick = this.tickCount + TOGGLE_DURATION_TICKS;
        }

        if (this.tickCount >= this.toggleEndTick) {
            this.discard();
            return;
        }

        if (this.tickCount >= this.nextToggleTick) {
            if (!toggleDoor(level, this.doorBasePos)) {
                this.doorBasePos = null;
                return;
            }

            this.nextToggleTick = this.tickCount + 4;
        }
    }

    public void bindToDoor(BlockPos doorPos) {
        this.doorBasePos = doorPos;
    }

    private boolean toggleDoor(ServerLevel level, BlockPos candidatePos) {
        BlockPos lowerPos = resolveLowerDoorPos(level, candidatePos);
        if (lowerPos == null) {
            return false;
        }

        BlockState lowerState = level.getBlockState(lowerPos);
        BlockState upperState = level.getBlockState(lowerPos.above());
        if (!(lowerState.getBlock() instanceof DoorBlock) || !(upperState.getBlock() instanceof DoorBlock)) {
            return false;
        }

        boolean isOpen = lowerState.getValue(DoorBlock.OPEN);
        BlockState newLower = lowerState.setValue(DoorBlock.OPEN, !isOpen);
        BlockState newUpper = upperState.setValue(DoorBlock.OPEN, !isOpen);

        level.setBlock(lowerPos, newLower, 10);
        level.setBlock(lowerPos.above(), newUpper, 10);
        level.playSound(
                null,
                lowerPos,
                isOpen ? SoundEvents.WOODEN_DOOR_CLOSE : SoundEvents.WOODEN_DOOR_OPEN,
                SoundSource.BLOCKS,
                1.1F,
                0.95F + (level.random.nextFloat() * 0.15F)
        );
        return true;
    }

    public static BlockPos resolveLowerDoorPos(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof DoorBlock)) {
            return null;
        }

        return state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
    }

    public static BlockPos findDoorwaySpawnPos(Level level, BlockPos doorPos, Direction playerFacing) {
        BlockPos lowerPos = resolveLowerDoorPos(level, doorPos);
        if (lowerPos == null) {
            return null;
        }

        BlockState state = level.getBlockState(lowerPos);
        Direction facing = state.getValue(DoorBlock.FACING);
        DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);
        Direction axisDirection = facing.getClockWise();
        if (hinge == DoorHingeSide.RIGHT) {
            axisDirection = axisDirection.getOpposite();
        }

        BlockPos doorwayPos = lowerPos.relative(axisDirection);
        if (!level.getBlockState(doorwayPos).canBeReplaced() && !level.getBlockState(doorwayPos).isAir()) {
            doorwayPos = lowerPos;
        }

        return doorwayPos;
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

    @Override
    public boolean isPushable() {
        return false;
    }
}
