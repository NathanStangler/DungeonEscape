package net.dungeonescape.dungeon.rooms;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.dungeon.DoorLocation;
import net.dungeonescape.dungeon.DungeonRoom;
import net.dungeonescape.dungeon.RoomLocation;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

public class FourthRoom extends DungeonRoom {
    public FourthRoom(DungeonEscapeMain plugin) {
        super(plugin, "FOURTH", new Vector(10, 0, 54), new BoundingBox(21, -32, 21, -21, 30, -21), new DoorLocation(new Vector(-10, 0, -21), 180), List.of(new DoorLocation(new Vector(21, 0, -10), -90), new DoorLocation(new Vector(-21, 0, 10), 90), new DoorLocation(new Vector(10, 0, 21), 0)));
    }

    @Override
    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        plugin.getKeyManager().create(roomLocation.getLocation().add(-1.5, 2, 2.5), this);
    }
}
