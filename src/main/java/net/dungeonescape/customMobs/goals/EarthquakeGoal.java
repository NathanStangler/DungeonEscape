package net.dungeonescape.customMobs.goals;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.DungeonPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class EarthquakeGoal extends SpellcastingGoal {
    private final DungeonEscapeMain plugin;
    private final DungeonPlayer player;

    public EarthquakeGoal(DungeonEscapeMain plugin, Mob entity, DungeonPlayer player) {
        super(entity);
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void performSpellCasting() {
        LivingEntity entityliving = mob.getTarget();
        double low = Math.min(entityliving.getY(), mob.getY());
        double high = Math.max(entityliving.getY(), mob.getY()) + 1.0D;
        float f = (float) Mth.atan2(entityliving.getZ() - mob.getZ(), entityliving.getX() - mob.getX());

        for (int i = 0; i < 10; ++i) {
            double d2 = i + 1;
            createSpellEntity(mob.getX() + (double) Mth.cos(f) * d2, mob.getZ() + (double) Mth.sin(f) * d2, low, high, i);
            createSpellEntity((mob.getX() + 1) + (double) Mth.cos(f) * d2, (mob.getZ() + 1) + (double) Mth.sin(f) * d2, low, high, i);
            createSpellEntity((mob.getX() - 1) + (double) Mth.cos(f) * d2, (mob.getZ() - 1) + (double) Mth.sin(f) * d2, low, high, i);
        }
    }

    private void createSpellEntity(double x, double z, double maxY, double y, int warmup) {
        BlockPos blockposition = new BlockPos(x, y, z);
        double d4 = 0.0D;

        while (blockposition.getY() >= Mth.floor(maxY) - 1) {
            BlockPos below = blockposition.below();

            if (mob.level.getBlockState(below).isFaceSturdy(mob.level, below, Direction.UP)) {
                if (!mob.level.isEmptyBlock(blockposition)) {
                    BlockState iblockdata1 = mob.level.getBlockState(blockposition);
                    VoxelShape voxelshape = iblockdata1.getCollisionShape(mob.level, blockposition);
                    if (!voxelshape.isEmpty()) {
                        d4 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                Location location = new Location(mob.getBukkitEntity().getWorld(), x, blockposition.getY() + d4 - 1, z);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!plugin.isEnabled()) return;
                    plugin.getGroundShakeManager().run(location, player.getPlayer());
                }, warmup);
                return;
            }

            blockposition = blockposition.below();
        }
    }

    @Override
    public int getCastingInterval() {
        return 100;
    }
}
