package net.dungeonescape.dungeon.rooms;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.DungeonItems;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.dungeon.DoorLocation;
import net.dungeonescape.dungeon.DungeonRoom;
import net.dungeonescape.dungeon.RoomLocation;
import org.bukkit.Material;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.UUID;

public class SixthRoom extends DungeonRoom {
    public SixthRoom(DungeonEscapeMain plugin) {
        super(plugin, "SIXTH", new Vector(-32, 0, 64), new BoundingBox(21, -32, 11, -21, 30, -11), new DoorLocation(new Vector(21, 0, 0), -90), null);
    }

    @Override
    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        UUID uuid = dungeonPlayer.getPlayer().getUniqueId();
        plugin.getBlinkingBlockManager().create(uuid, roomLocation.getLocation().add(3, 3, 6), Material.BLUE_CONCRETE);
        plugin.getBlinkingBlockManager().create(uuid, roomLocation.getLocation().add(-15, 12, -3), Material.BLUE_CONCRETE);
        plugin.getMovingBlockManager().create(uuid, Material.BLUE_CONCRETE, roomLocation.getLocation().add(-12.5, 11.5, -5.5), roomLocation.getLocation().add(-6.5, 11.5, -5.5));
        plugin.getBlinkingBlockManager().create(uuid, roomLocation.getLocation().add(5.5, 13.5, -5.5), Material.BLUE_CONCRETE);
        plugin.getMovingBlockManager().create(uuid, Material.BLUE_CONCRETE, roomLocation.getLocation().add(7.5, 15.5, 4.5), roomLocation.getLocation().add(2.5, 15.5, 4.5));
        plugin.getKeyManager().create(roomLocation.getLocation().add(-0.5, 23, -1.5), this);
    }

    @Override
    public void onEnter(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        dungeonPlayer.getPlayer().getInventory().addItem(DungeonItems.LEAPER);
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInMainHand(), false);
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInOffHand(), true);
    }

    @Override
    public void onLeave(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        dungeonPlayer.getPlayer().getInventory().remove(DungeonItems.LEAPER);
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInMainHand(), false);
        dungeonPlayer.getFakePlayer().hold(dungeonPlayer.getPlayer().getInventory().getItemInOffHand(), true);
    }

    @Override
    public void onCompletion(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        UUID uuid = dungeonPlayer.getPlayer().getUniqueId();
        plugin.getBlinkingBlockManager().remove(uuid);
        plugin.getMovingBlockManager().remove(uuid);
        plugin.getHeightManager().remove(dungeonPlayer);
    }
}
