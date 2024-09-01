package net.dungeonescape.customMobs.goals;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;

import java.util.EnumSet;

public class CustomStrollToSpawn extends Goal {
    private final PathfinderMob mob;
    private final Vec3i vector;
    private Vec3 target;

    public CustomStrollToSpawn(PathfinderMob mob, Location location) {
        this.mob = mob;
        this.vector = new Vec3i(location.getX(), location.getY(), location.getZ());
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getTarget() != null) {
            return false;
        } else if (mob.blockPosition().distSqr(vector) < 12) {
            return false;
        } else {
            Vec3 vec3 = DefaultRandomPos.getPosTowards(this.mob, 3, 2, Vec3.atCenterOf(vector), Math.PI / 2);
            if (vec3 == null) {
                return false;
            }

            target = vec3;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && this.mob.getTarget() == null;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(target.x(), target.y(), target.z(), 1);
    }
}
