package net.dungeonescape.customMobs.goals;

import net.dungeonescape.DungeonEscapeMain;
import net.minecraft.world.entity.Mob;

public class ShootFireGoal extends SpellcastingGoal {
    private final DungeonEscapeMain plugin;

    public ShootFireGoal(DungeonEscapeMain plugin, Mob entity) {
        super(entity);
        this.plugin = plugin;
    }

    @Override
    public void performSpellCasting() {
        plugin.getFlameManager().create(mob.getBukkitLivingEntity().getEyeLocation());
    }

    @Override
    public int getCastingInterval() {
        return 100;
    }
}
