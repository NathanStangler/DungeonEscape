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

public class EleventhRoom extends DungeonRoom {
    public EleventhRoom(DungeonEscapeMain plugin) {
        super(plugin, "ELEVENTH", new Vector(8, 0, 170), new BoundingBox(21, -32, 11, -21, 30, -11), new DoorLocation(new Vector(-10, 0, -11), 180), List.of(new DoorLocation(new Vector(10, 0, 11), 0)));
    }

    @Override
    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        plugin.getCustomMobManager().strider(dungeonPlayer, roomLocation.getLocation().add(0, -1, 0));
        plugin.getCustomMobManager().strider(dungeonPlayer, roomLocation.getLocation().add(-11, -1, 0));
        plugin.getCustomMobManager().strider(dungeonPlayer, roomLocation.getLocation().add(11, -1, 0));
        plugin.getCustomMobManager().zombie(dungeonPlayer, roomLocation.getLocation().add(-6.5, 2, 0));
        plugin.getCustomMobManager().silverfish(dungeonPlayer, roomLocation.getLocation().add(-16, 0, -6));
        plugin.getCustomMobManager().silverfish(dungeonPlayer, roomLocation.getLocation().add(16, 0, 6));
    }

    @Override
    public void onCompletion(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        plugin.getCustomMobManager().getEntities(dungeonPlayer.getPlayer().getUniqueId()).forEach(Entity::remove);
    }
}
