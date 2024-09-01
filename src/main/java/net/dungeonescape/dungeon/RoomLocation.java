package net.dungeonescape.dungeon;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;

public class RoomLocation {
    private final BoundingBox boundingBox;
    private final DungeonRoom dungeonRoom;
    private final Location location;
    private boolean unlocked = false;
    private boolean completed = false;

    public RoomLocation(DungeonRoom dungeonRoom, Location location) {
        this.boundingBox = dungeonRoom.getBoundingBox().shift(location);
        this.dungeonRoom = dungeonRoom;
        this.location = location;
    }

    public void setUnlocked() {
        this.unlocked = true;
    }

    public void setCompleted() {
        this.completed = true;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public DungeonRoom getDungeonRoom() {
        return dungeonRoom;
    }

    public Location getLocation() {
        return location.clone();
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public boolean isCompleted() {
        return completed;
    }
}
