package safro.aqualine.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class DistanceMeleeGoal extends MeleeAttackGoal {
    private final PathfinderMob mob;
    private final float distance;

    public DistanceMeleeGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, float distance) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.mob = mob;
        this.distance = distance;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = mob.getTarget();
        if (super.canUse()) {
            if (livingEntity != null && livingEntity.isAlive()) {
                return mob.distanceTo(livingEntity) < distance;
            }
        }
        return false;
    }
}
