package net.dungeonescape.customMobs.goals;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class SpellcastingGoal extends Goal {
    protected final Mob mob;
    protected boolean castingSpell;
    protected int attackWarmupDelay;
    protected int nextAttackTickCount;

    public SpellcastingGoal(Mob mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        LivingEntity entityliving = mob.getTarget();
        return entityliving != null && entityliving.isAlive() && (!castingSpell && mob.tickCount >= nextAttackTickCount);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity entityliving = mob.getTarget();
        return entityliving != null && entityliving.isAlive() && attackWarmupDelay > 0;
    }

    @Override
    public void start() {
        attackWarmupDelay = adjustedTickDelay(20);
        nextAttackTickCount = mob.tickCount + getCastingInterval();
        mob.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0F, 1.0F);
        castingSpell = true;
    }

    @Override
    public void stop() {
        super.stop();
        castingSpell = false;
    }

    @Override
    public void tick() {
        --attackWarmupDelay;
        if (attackWarmupDelay == 0) {
            performSpellCasting();
            mob.playSound(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0F, 1.0F);
        }
    }

    public abstract void performSpellCasting();

    public abstract int getCastingInterval();
}
