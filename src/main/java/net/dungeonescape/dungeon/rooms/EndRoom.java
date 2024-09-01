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
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class EndRoom extends DungeonRoom {
    public EndRoom(DungeonEscapeMain plugin) {
        super(plugin, "END", new Vector(28, 0, 202), new BoundingBox(21, -32, 21, -21, 30, -21), new DoorLocation(new Vector(-10, 0, -21), 180), null);
    }

    @Override
    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        plugin.getCustomMobManager().dragon(dungeonPlayer, roomLocation.getLocation(), this);
        plugin.getCustomMobManager().crystal(dungeonPlayer, roomLocation.getLocation().add(-8.5, 3, -8.5));
        plugin.getCustomMobManager().crystal(dungeonPlayer, roomLocation.getLocation().add(9.5, 3, -8.5));
        plugin.getCustomMobManager().crystal(dungeonPlayer, roomLocation.getLocation().add(9.5, 3, 9.5));
        plugin.getCustomMobManager().crystal(dungeonPlayer, roomLocation.getLocation().add(-8.5, 3, 9.5));
        plugin.getCustomMobManager().enderman(dungeonPlayer, roomLocation.getLocation().add(-14, -1, -9));
        plugin.getCustomMobManager().enderman(dungeonPlayer, roomLocation.getLocation().add(7, -3, -8));
        plugin.getCustomMobManager().enderman(dungeonPlayer, roomLocation.getLocation().add(1, 4, 8));
    }

    @Override
    public void onEnter(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        dungeonPlayer.getPlayer().getInventory().addItem(DungeonItems.BOW);
        dungeonPlayer.getPlayer().getInventory().setItem(35, new ItemStack(Material.ARROW));
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInMainHand(), false);
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInOffHand(), true);
    }

    @Override
    public void onLeave(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        dungeonPlayer.getPlayer().getInventory().remove(DungeonItems.BOW);
        dungeonPlayer.getPlayer().getInventory().remove(new ItemStack(Material.ARROW));
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInMainHand(), false);
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInOffHand(), true);
    }

    public void onKill(DungeonPlayer dungeonPlayer) {
        plugin.getCustomMobManager().getEntities(dungeonPlayer.getPlayer().getUniqueId()).forEach(Entity::remove);

        RoomLocation roomLocation = dungeonPlayer.getRoomLocation(this);
        Location first = roomLocation.getLocation().add(2, -4, 2);
        Location second = roomLocation.getLocation().add(-2, -4, -2);
        int lowX = Math.min(first.getBlockX(), second.getBlockX());
        int lowY = Math.min(first.getBlockY(), second.getBlockY());
        int lowZ = Math.min(first.getBlockZ(), second.getBlockZ());

        for (int x = 0; x <= Math.abs(first.getBlockX()-second.getBlockX()); x++) {
            for (int y = 0; y <= Math.abs(first.getBlockY()-second.getBlockY()); y++) {
                for (int z = 0; z <= Math.abs(first.getBlockZ() - second.getBlockZ()); z++) {
                    Block block = new Location(dungeonPlayer.getPlayer().getWorld(), lowX + x, lowY + y, lowZ + z).getBlock();
                    if (block.getType().equals(Material.AIR)) {
                        block.setType(Material.END_PORTAL);
                    }
                }
            }
        }
    }
}
