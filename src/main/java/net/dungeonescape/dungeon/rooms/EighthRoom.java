package net.dungeonescape.dungeon.rooms;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.dungeon.DoorLocation;
import net.dungeonescape.dungeon.DungeonRoom;
import net.dungeonescape.dungeon.RoomLocation;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

public class EighthRoom extends DungeonRoom {
    public EighthRoom(DungeonEscapeMain plugin) {
        super(plugin, "EIGHTH", new Vector(20, 0, 128), new BoundingBox(11, -32, 11, -11, 30, -11), new DoorLocation(new Vector(0, 0, -11), 180), List.of(new DoorLocation(new Vector(-11, 0, 0), 90)));
    }

    @Override
    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        plugin.getCustomMobManager().vindicator(dungeonPlayer, roomLocation.getLocation().add(5, 0, -7.5));
        plugin.getCustomMobManager().vindicator(dungeonPlayer, roomLocation.getLocation().add(-6, 0, -3));
        plugin.getCustomMobManager().vindicator(dungeonPlayer, roomLocation.getLocation().add(5, 0, 3));
        plugin.getCustomMobManager().caveSpider(dungeonPlayer, roomLocation.getLocation().add(2, 0, 6));
        plugin.getCustomMobManager().caveSpider(dungeonPlayer, roomLocation.getLocation().add(-7, 0, 6));
    }

    @Override
    public void onCompletion(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        plugin.getCustomMobManager().getEntities(dungeonPlayer.getPlayer().getUniqueId()).forEach(Entity::remove);
    }
}
