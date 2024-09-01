package net.dungeonescape.common.blinkingblock;

import net.dungeonescape.DungeonEscapeMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlinkingBlockManager {
    private final DungeonEscapeMain plugin;
    private final Map<UUID, Set<BlinkingBlock>> blocks = new ConcurrentHashMap<>();
    private int task = -1;

    public BlinkingBlockManager(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    public void create(UUID uuid, Location location, Material material) {
        BlinkingBlock blinkingBlock = new BlinkingBlock(location, material);
        Set<BlinkingBlock> blocks = this.blocks.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet());
        blocks.add(blinkingBlock);
    }

    public void remove(UUID uuid) {
        Set<BlinkingBlock> blocks = this.blocks.remove(uuid);
        if (blocks == null) return;
        blocks.forEach(block -> block.getLocation().getBlock().setType(block.getMaterial()));
    }

    public void start() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> this.blocks.values().forEach(blocks -> blocks.forEach(block -> {
            Block b = block.getLocation().getBlock();
            if (b.getType().equals(block.getMaterial())) {
                b.setType(Material.AIR);
            } else {
                b.setType(block.getMaterial());
            }
        })), 0L, 30L);
    }

    public void close() {
        Bukkit.getScheduler().cancelTask(task);
        task = -1;
        blocks.keySet().forEach(this::remove);
    }
}
