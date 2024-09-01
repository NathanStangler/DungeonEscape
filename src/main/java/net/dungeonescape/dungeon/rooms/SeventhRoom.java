package net.dungeonescape.dungeon.rooms;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.dungeon.DoorLocation;
import net.dungeonescape.dungeon.DungeonRoom;
import net.dungeonescape.dungeon.RoomLocation;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

public class SeventhRoom extends DungeonRoom {
    public SeventhRoom(DungeonEscapeMain plugin) {
        super(plugin, "SEVENTH", new Vector(20, 0, 96), new BoundingBox(11, -32, 21, -11, 30, -21), new DoorLocation(new Vector(0, 0, -21), 180), List.of(new DoorLocation(new Vector(0, 0, 21), 0)));
    }

    @Override
    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        plugin.getKeyManager().create(roomLocation.getLocation().add(0.5, 0, 18.5), this);
    }
}
