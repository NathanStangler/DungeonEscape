package net.dungeonescape.common.laser;

import net.dungeonescape.DungeonEscapeMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.*;

public class LaserManager {
    private final DungeonEscapeMain plugin;
    private final Map<UUID, Set<Laser>> lasers = new HashMap<>();
    private int task = -1;

    public LaserManager(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    public void create(UUID uuid, Location startFirst, Location endFirst, Location startSecond, Location endSecond, int ticks) {
        Laser laser = new Laser(startFirst.clone(), endFirst.clone(), ticks, startFirst.clone().subtract(startSecond).multiply(1d / Math.max(ticks, 1)).toVector(), endFirst.clone().subtract(endSecond).multiply(1d / Math.max(ticks, 1)).toVector());
        Set<Laser> lasers = this.lasers.computeIfAbsent(uuid, k -> new HashSet<>());
        lasers.add(laser);
    }

    public void remove(UUID uuid) {
        this.lasers.remove(uuid);
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> this.lasers.values().forEach(lasers -> lasers.forEach(laser -> {
            Location startPoint = laser.getStart().clone();
            Location endPoint = laser.getEnd().clone();
            double distance = laser.getStart().distance(endPoint);
            Vector vector = endPoint.toVector().subtract(startPoint.toVector());

            for (double i = 1; i <= distance; i += 0.5) {
                vector.multiply(i);
                startPoint.add(vector);
                startPoint.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, startPoint, 1, 0, 0, 0, 0);
                Bukkit.getOnlinePlayers().forEach(player -> {
                    Location position = player.getLocation();
                    if (position.distanceSquared(startPoint) < 8 && player.getBoundingBox().contains(startPoint.toVector())) Bukkit.getScheduler().runTask(plugin, () -> player.damage(10));
                });
                startPoint.subtract(vector);
                vector.normalize();
            }

            if (laser.getTicks() != 0) {
                if (laser.getDirection()) {
                    laser.getStart().add(laser.getStartIncrement());
                    laser.getEnd().add(laser.getEndIncrement());
                } else {
                    laser.getStart().subtract(laser.getStartIncrement());
                    laser.getEnd().subtract(laser.getEndIncrement());
                }

                laser.incrementTicks();
            }
        })), 0L, 1L).getTaskId();
    }

    public void close() {
        Bukkit.getScheduler().cancelTask(task);
        task = -1;
    }
}
