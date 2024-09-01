package net.dungeonescape.dungeon;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.DungeonPlayer;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

public class DungeonRoom {
    protected final DungeonEscapeMain plugin;
    private final String name;
    private final Vector shift;
    private final BoundingBox boundingBox;
    private final DoorLocation entrance;
    private final List<DoorLocation> doors;

    public DungeonRoom(DungeonEscapeMain plugin, String name, Vector shift, BoundingBox boundingBox, DoorLocation entrance, List<DoorLocation> doors) {
        this.plugin = plugin;
        this.name = name;
        this.shift = shift;
        this.boundingBox = boundingBox;
        this.entrance = entrance;
        this.doors = doors == null ? List.of() : doors;
    }

    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {

    }

    public void onCompletion(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {

    }

    public void onEnter(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {

    }

    public void onLeave(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {

    }

    public String getName() {
        return name;
    }

    public Vector getShift() {
            return shift.clone();
    }

    public BoundingBox getBoundingBox() {
        return boundingBox.clone();
    }

    public DoorLocation getEntrance() {
        return entrance;
    }

    public List<DoorLocation> getDoors() {
        return doors;
    }
}
