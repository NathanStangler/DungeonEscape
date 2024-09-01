package net.dungeonescape.common.flame;

import net.dungeonescape.DungeonEscapeMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FlameManager {
    private final DungeonEscapeMain plugin;
    private final Set<Flame> flames = ConcurrentHashMap.newKeySet();
    private int task = -1;

    public FlameManager(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    public void create(Location location) {
        flames.add(new Flame(location));
    }

    public void remove(Flame flame) {
        flames.remove(flame);
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> flames.forEach(flame -> {
            Location location = flame.getLocation().clone();
            for (int x = 0; x < 3; x++) {
                for (int i = 0; i < 3; i++) {
                    double angle = flame.getTicks() * (Math.PI / 16) + (2 * Math.PI * i / 3);
                    Vector v = new Vector(Math.cos(angle) * 0.8, flame.getTicks() * 0.1, Math.sin(angle) * 0.8);
                    v.rotateAroundX((location.getPitch() + 90) * Math.PI / 180);
                    v.rotateAroundY(-location.getYaw() * Math.PI / 180);
                    location.add(v);
                    flame.getLocation().getWorld().spawnParticle(Particle.FLAME, location, 0, 0, 0, 0, 0);
                    if (i == 0 && flame.getTicks() % 9 == 0) location.getWorld().playSound(location, Sound.BLOCK_FIRE_AMBIENT, 1, 1);
                    location.getWorld().getNearbyLivingEntities(location, 0.6).stream().filter(livingEntity -> livingEntity.isValid() && livingEntity.getType().equals(EntityType.PLAYER)).forEach(livingEntity -> livingEntity.damage(6));
                    location.subtract(v);
                }
                flame.incrementTicks();
            }

            if (flame.getTicks() == 120) {
                remove(flame);
            }
        }), 0L, 1L).getTaskId();
    }

    public void close() {
        Bukkit.getScheduler().cancelTask(task);
        task = -1;
        flames.forEach(this::remove);
    }
}
