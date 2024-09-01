package net.dungeonescape.dungeon.rooms;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.dungeon.DoorLocation;
import net.dungeonescape.dungeon.DungeonRoom;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

public class StartRoom extends DungeonRoom {
    public StartRoom(DungeonEscapeMain plugin) {
        super(plugin, "START", new Vector(), new BoundingBox(11, -32, 11, -11, 30, -11), null, List.of(new DoorLocation(new Vector(0, 0, 11), 0)));
    }
}
