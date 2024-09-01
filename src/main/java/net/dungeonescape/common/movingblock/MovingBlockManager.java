package net.dungeonescape.common.movingblock;

import net.dungeonescape.DungeonEscapeMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Shulker;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MovingBlockManager {
    private final DungeonEscapeMain plugin;
    private final Map<UUID, Set<MovingBlock>> blocks = new ConcurrentHashMap<>();
    private int task = -1;

    public MovingBlockManager(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    public void create(UUID uuid, Material material, Location location1, Location location2) {
        World world = location1.getWorld();
        FallingBlock block = world.spawnFallingBlock(location1, material.createBlockData());
        block.setFallDistance(-1000000);
        block.shouldAutoExpire(false);
        block.setInvulnerable(true);
        Shulker shulker = (Shulker) world.spawnEntity(location1, EntityType.SHULKER);
        shulker.setAI(false);
        shulker.setInvulnerable(true);
        shulker.setInvisible(true);
        shulker.setSilent(true);
        ArmorStand stand = (ArmorStand) world.spawnEntity(location1.clone().subtract(0, 1, 0), EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setAI(false);
        stand.setInvulnerable(true);
        stand.addPassenger(shulker);
        stand.addPassenger(block);

        MovingBlock movingBlock = new MovingBlock(location1.clone(), block, shulker, stand, location1.clone().subtract(location2).multiply(1d / 60d).toVector());
        Set<MovingBlock> blocks = this.blocks.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet());
        blocks.add(movingBlock);
    }

    public void remove(UUID uuid) {
        Set<MovingBlock> blocks = this.blocks.remove(uuid);
        if (blocks == null) return;
        blocks.forEach(block -> {
            block.getStand().remove();
            block.getShulker().remove();
            block.getBlock().remove();
        });
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> blocks.values().forEach(blocks -> blocks.forEach(block -> {
            if (block.getDirection()) {
                block.getLocation().add(block.getIncrement());
            } else {
                block.getLocation().subtract(block.getIncrement());
            }
            block.getStand().getPassengers().forEach(p -> block.getStand().removePassenger(p));
            block.getStand().teleport(block.getLocation().clone().subtract(0, 1, 0));
            block.getStand().addPassenger(block.getShulker());
            block.getStand().addPassenger(block.getBlock());

            block.incrementTicks();
        })), 0L, 1L).getTaskId();
    }

    public void close() {
        Bukkit.getScheduler().cancelTask(task);
        task = -1;
        blocks.keySet().forEach(this::remove);
    }
}
