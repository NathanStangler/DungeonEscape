package net.dungeonescape.dungeon.rooms;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.DungeonItems;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.dungeon.DoorLocation;
import net.dungeonescape.dungeon.DungeonRoom;
import net.dungeonescape.dungeon.RoomLocation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

public class NinthRoom extends DungeonRoom {
    public NinthRoom(DungeonEscapeMain plugin) {
        super(plugin, "NINTH", new Vector(-12, 0, 138), new BoundingBox(21, -32, 21, -21, 30, -21), new DoorLocation(new Vector(21, 0, -10), -90), List.of(new DoorLocation(new Vector(10, 0, 21), 0), new DoorLocation(new Vector(-10, 0, -21), 180)));
    }

    @Override
    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        plugin.getCustomMobManager().warden(dungeonPlayer, roomLocation.getLocation().subtract(0, 9, 0), this);
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
}
