package com.igore.code44.entity;

import com.igore.code44.entity.goal.FixedReachMeleeAttackGoal;
import com.igore.code44.sound.HorrorSoundManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Zer000Entity extends Monster implements GeoEntity {
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("animation.zer000.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int attackUnlockTick;

    public Zer000Entity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 25;
    }

    public void setAttackDelayTicks(int delayTicks) {
        this.attackUnlockTick = delayTicks;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.ATTACK_DAMAGE, 1000.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new FixedReachMeleeAttackGoal(this, 1.45D, false, 1.6D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 24.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.tickCount % 40 == 1 && this.getTarget() instanceof net.minecraft.server.level.ServerPlayer player) {
            HorrorSoundManager.startZer000Sound(player, this);
        }

        if (!this.level().isClientSide && this.getTarget() != null) {
            breakObstaclesAhead();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "zer000_idle", 5, this::idleController));
    }

    private PlayState idleController(AnimationState<Zer000Entity> state) {
        return state.setAndContinue(IDLE_ANIMATION);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    private void breakObstaclesAhead() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 direction = this.getTarget().position().subtract(this.position());

        if (direction.lengthSqr() < 0.001D) {
            direction = this.getLookAngle();
        } else {
            direction = direction.normalize();
        }

        BlockPos aheadPos = BlockPos.containing(this.getX() + direction.x * 1.6D, this.getY(), this.getZ() + direction.z * 1.6D);
        BlockPos belowAhead = aheadPos.below();
        BlockState belowState = serverLevel.getBlockState(belowAhead);

        if (belowState.liquid()) {
            serverLevel.setBlockAndUpdate(belowAhead, Blocks.STONE.defaultBlockState());
        }

        AABB destroyBox = this.getBoundingBox()
                .expandTowards(direction.scale(1.5D))
                .inflate(0.3D, 0.6D, 0.3D);

        int minX = Mth.floor(destroyBox.minX);
        int maxX = Mth.floor(destroyBox.maxX);
        int minY = Mth.floor(destroyBox.minY);
        int maxY = Mth.floor(destroyBox.maxY);
        int minZ = Mth.floor(destroyBox.minZ);
        int maxZ = Mth.floor(destroyBox.maxZ);

        for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
            BlockState state = serverLevel.getBlockState(pos);

            if (state.isAir() || state.liquid()) {
                continue;
            }

            if (state.is(Blocks.BEDROCK) || state.is(Blocks.BARRIER) || state.is(Blocks.END_PORTAL_FRAME)) {
                continue;
            }

            if (state.getDestroySpeed(serverLevel, pos) < 0.0F) {
                continue;
            }

            if (!state.isFaceSturdy(serverLevel, pos, Direction.UP) || pos.getY() >= this.blockPosition().getY()) {
                serverLevel.destroyBlock(pos, false, this);
            }
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return target instanceof Player && this.tickCount >= this.attackUnlockTick;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
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
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        if (this.tickCount < this.attackUnlockTick) {
            return false;
        }

        boolean hurt = super.doHurtTarget(target);

        if (hurt && target instanceof net.minecraft.server.level.ServerPlayer player) {
            if (this.level() instanceof ServerLevel serverLevel) {
                Component killerName = Component.literal("efbuiefbuiefbuiefbuiefbuiefbuiefbuiefbui")
                        .setStyle(Style.EMPTY
                                .withColor(ChatFormatting.WHITE)
                                .withObfuscated(true)
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("sore eyes"))));
                Component message = Component.literal(player.getGameProfile().getName() + " was killed by ")
                        .append(killerName);
                serverLevel.players().forEach(serverPlayer -> serverPlayer.sendSystemMessage(message));
            }
            HorrorSoundManager.stopZer000Sound(player);
            this.discard();
        }

        return hurt;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide && this.getTarget() instanceof net.minecraft.server.level.ServerPlayer player) {
            HorrorSoundManager.stopZer000Sound(player);
        }

        super.remove(reason);
    }
}
