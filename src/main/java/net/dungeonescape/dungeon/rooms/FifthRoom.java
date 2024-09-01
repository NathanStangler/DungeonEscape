package net.dungeonescape.dungeon.rooms;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.DungeonItems;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.dungeon.DoorLocation;
import net.dungeonescape.dungeon.DungeonRoom;
import net.dungeonescape.dungeon.RoomLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FifthRoom extends DungeonRoom {
    public FifthRoom(DungeonEscapeMain plugin) {
        super(plugin, "FIFTH", new Vector(42, 0, 44), new BoundingBox(11, -32, 11, -11, 30, -11), new DoorLocation(new Vector(-11, 0, 0), 90), null);
    }

    @Override
    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        Location location1 = roomLocation.getLocation().add(new Location(dungeonPlayer.getPlayer().getWorld(), 3, -1, 3));
        Location location2 = roomLocation.getLocation().add(new Location(dungeonPlayer.getPlayer().getWorld(), -3, -9, -3));

        int lowX = Math.min(location1.getBlockX(), location2.getBlockX());
        int lowY = Math.min(location1.getBlockY(), location2.getBlockY());
        int lowZ = Math.min(location1.getBlockZ(), location2.getBlockZ());

        int highX = Math.max(location1.getBlockX(), location2.getBlockX());
        int highY = Math.max(location1.getBlockY(), location2.getBlockY());
        int highZ = Math.max(location1.getBlockZ(), location2.getBlockZ());

        List<Block> blocks = new ArrayList<>();

        for (int x = 0; x <= Math.abs(location1.getBlockX()-location2.getBlockX()); x++) {
            for (int y = 0; y <= Math.abs(location1.getBlockY()-location2.getBlockY()); y++) {
                for (int z = 0; z <= Math.abs(location1.getBlockZ() - location2.getBlockZ()); z++) {
                    Block block = new Location(dungeonPlayer.getPlayer().getWorld(), lowX + x, lowY + y, lowZ + z).getBlock();
                    block.setType(Material.DEEPSLATE);
                    if (block.getX() != lowX && block.getX() != highX && block.getY() != lowY && block.getY() != highY && block.getZ() != lowZ && block.getZ() != highZ) {
                        blocks.add(block);
                    }
                }
            }
        }
        blocks.get(ThreadLocalRandom.current().nextInt(blocks.size())).setType(Material.TUFF);
    }

    @Override
    public void onEnter(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        dungeonPlayer.getPlayer().getInventory().addItem(DungeonItems.PICKAXE);
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInMainHand(), false);
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInOffHand(), true);
    }

    @Override
    public void onLeave(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        dungeonPlayer.getPlayer().getInventory().remove(DungeonItems.PICKAXE);
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInMainHand(), false);
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInOffHand(), true);
    }
}
