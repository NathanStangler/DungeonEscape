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

public class SecondRoom extends DungeonRoom {
    public SecondRoom(DungeonEscapeMain plugin) {
        super(plugin, "SECOND", new Vector(0, 0, 22), new BoundingBox(11, -32, 11, -11, 30, -11), new DoorLocation(new Vector(0, 0, -11), 180), List.of(new DoorLocation(new Vector(-11, 0, 0), 90), new DoorLocation(new Vector(0, 0, 11), 0)));
    }

    @Override
    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        plugin.getCustomMobManager().silverfish(dungeonPlayer, roomLocation.getLocation().add(-3, 1, -6));
        plugin.getCustomMobManager().silverfish(dungeonPlayer, roomLocation.getLocation().add(1, 1, 5));
        plugin.getCustomMobManager().evoker(dungeonPlayer, roomLocation.getLocation().add(-7, 0, 3));
        plugin.getCustomMobManager().evoker(dungeonPlayer, roomLocation.getLocation().add(7, 1, -2));
        plugin.getCustomMobManager().zombie(dungeonPlayer, roomLocation.getLocation().add(4, -2, 3));
    }

    @Override
    public void onCompletion(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        plugin.getCustomMobManager().getEntities(dungeonPlayer.getPlayer().getUniqueId()).forEach(Entity::remove);
    }
}
