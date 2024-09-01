package net.dungeonescape.customMobs.goals;

import net.dungeonescape.DungeonEscapeMain;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Location;

public class TornadoPullGoal extends SpellcastingGoal {
    private final DungeonEscapeMain plugin;

    public TornadoPullGoal(DungeonEscapeMain plugin, Mob entity) {
        super(entity);
        this.plugin = plugin;
    }

    @Override
    public void performSpellCasting() {
        LivingEntity entityliving = mob.getTarget();
        double low = Math.min(entityliving.getY(), mob.getY());
        double high = Math.max(entityliving.getY(), mob.getY()) + 1.0D;
        float f = (float) Mth.atan2(entityliving.getZ() - mob.getZ(), entityliving.getX() - mob.getX());
        double x = mob.getX() + (double) Mth.cos(f) * 5;
        double z = mob.getZ() + (double) Mth.sin(f) * 5;
        BlockPos blockposition = new BlockPos(x, high, z);
        double d4 = 0.0D;

        while (blockposition.getY() >= Mth.floor(low) - 1) {
            BlockPos below = blockposition.below();

            if (mob.level.getBlockState(below).isFaceSturdy(mob.level, below, Direction.UP)) {
                if (!mob.level.isEmptyBlock(blockposition)) {
                    BlockState iblockdata1 = mob.level.getBlockState(blockposition);
                    VoxelShape voxelshape = iblockdata1.getCollisionShape(mob.level, blockposition);
                    if (!voxelshape.isEmpty()) {
                        d4 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                plugin.getTornadoManager().create(new Location(mob.getBukkitEntity().getWorld(), x, blockposition.getY() + d4 - 1, z));
                return;
            }

            blockposition = blockposition.below();
        }
    }

    @Override
    public int getCastingInterval() {
        return 300;
    }
}
