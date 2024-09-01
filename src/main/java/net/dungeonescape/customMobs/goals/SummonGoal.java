package net.dungeonescape.customMobs.goals;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.DungeonPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;

public class SummonGoal extends SpellcastingGoal {
    private final DungeonEscapeMain plugin;
    private final DungeonPlayer player;

    public SummonGoal(DungeonEscapeMain plugin, Mob entity, DungeonPlayer player) {
        super(entity);
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public boolean canUse() {
        return plugin.getCustomMobManager().getEntities(player.getPlayer().getUniqueId()).size() < 10 && super.canUse();
    }

    @Override
    public void performSpellCasting() {
        Location location = new Location(mob.getBukkitEntity().getWorld(), mob.position().x(), mob.position().y(), mob.position().z());
        plugin.getCustomMobManager().stray(player, location).travel(new Vec3(mob.getRandom().nextInt(2), 0, mob.getRandom().nextInt(2)));
    }

    @Override
    public int getCastingInterval() {
        return 340;
    }
}
