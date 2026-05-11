package com.igore.code44.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class FixedReachMeleeAttackGoal extends MeleeAttackGoal {
    private final double attackReachSqr;

    public FixedReachMeleeAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, double attackReach) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.attackReachSqr = attackReach * attackReach;
    }

    @Override
    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return this.attackReachSqr;
    }
}
