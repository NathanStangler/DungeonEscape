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

public class TenthRoom extends DungeonRoom {
    public TenthRoom(DungeonEscapeMain plugin) {
        super(plugin, "TENTH", new Vector(-22, 0, 106), new BoundingBox(11, -32, 11, -11, 30, -11), new DoorLocation(new Vector(0, 0, 11), 180), null);
    }

    @Override
    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        UUID uuid = dungeonPlayer.getPlayer().getUniqueId();
        plugin.getBlinkingBlockManager().create(uuid, roomLocation.getLocation().add(5, 2, 0), Material.LIGHT_GRAY_CONCRETE);
        plugin.getBlinkingBlockManager().create(uuid, roomLocation.getLocation().add(-5, 9, 2), Material.LIGHT_GRAY_CONCRETE);
        plugin.getMovingBlockManager().create(uuid, Material.LIGHT_GRAY_CONCRETE, roomLocation.getLocation().add(5.5, 11.5, -0.5), roomLocation.getLocation().add(5.5, 11.5, -4.5));
        plugin.getKeyManager().create(roomLocation.getLocation().add(-1.5, 16, -1.5), this);
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
