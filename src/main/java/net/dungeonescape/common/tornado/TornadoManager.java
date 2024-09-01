package net.dungeonescape.common.tornado;

import net.dungeonescape.DungeonEscapeMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TornadoManager {
    private final DungeonEscapeMain plugin;
    private final Set<Tornado> tornadoes = ConcurrentHashMap.newKeySet();
    private int task = -1;

    public TornadoManager(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    public void create(Location location) {
        tornadoes.add(new Tornado(location));
    }

    public void remove(Tornado tornado) {
        tornadoes.remove(tornado);
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> tornadoes.forEach(tornado -> {
            tornado.getLocation().getWorld().playSound(tornado.getLocation(), Sound.ENTITY_HORSE_BREATHE, 1, 1);
            tornado.getLocation().getNearbyPlayers(3).forEach(player -> {
                if (player.getLocation().distanceSquared(tornado.getLocation()) > 1.2) {
                    player.setVelocity(tornado.getLocation().toVector().subtract(player.getLocation().toVector()).normalize());
                } else {
                    player.setVelocity(new Vector(Math.cos(tornado.getTicks()) * 0.2D, 0.0D, Math.sin(tornado.getTicks()) * 0.2D));
                }
            });

            for (int i = 0; i < 6; i++) {
                for (double y = 0; y < 5; y += 0.5) {
                    double radius = y * 3 / 5;
                    double v = Math.toRadians(60F * i + y * 25 - tornado.getTicks());
                    tornado.getLocation().getWorld().spawnParticle(Particle.CLOUD, tornado.getLocation().clone().add(Math.cos(v) * radius, y, Math.sin(v) * radius), 0, 0, 1, 1, 0);
                }
            }

            if (tornado.incrementTicks() == 80) {
                remove(tornado);
            }
        }), 0L, 1L).getTaskId();
    }

    public void close() {
        Bukkit.getScheduler().cancelTask(task);
        task = -1;
        tornadoes.forEach(this::remove);
    }
}
