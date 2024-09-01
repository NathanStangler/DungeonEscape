package net.dungeonescape.customMobs.goals;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class WardenBossRandomStroll extends RandomStroll {
    private final Vec3i vector;

    public WardenBossRandomStroll(float speed, Location location) {
        super(speed);
        this.vector = new Vec3i(location.getX(), location.getY(), location.getZ());
    }

    @Nullable
    @Override
    protected Vec3 getTargetPos(PathfinderMob entity) {
        if (entity.blockPosition().distSqr(vector) < 12) {
            return entity.position();
        } else {
            return Vec3.atCenterOf(vector);
        }
    }
}
