package net.dungeonescape.common;

import net.dungeonescape.dungeon.DungeonRoom;
import net.dungeonescape.dungeon.RoomLocation;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class DungeonPlayer {
    private final Player player;
    private final Location location;
    private final FakePlayer fakePlayer;
    private final List<RoomLocation> roomLocations = new ArrayList<>();
    private final BossBar bar;
    private boolean hasKey = true;

    public DungeonPlayer(Player player, Location location, FakePlayer fakePlayer) {
        this.player = player;
        this.location = location;
        this.fakePlayer = fakePlayer;
        this.bar = BossBar.bossBar(Component.text(player.getName() + "'s Completion").color(TextColor.color(0, 179, 255)), 0, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location.clone();
    }

    public FakePlayer getFakePlayer() {
        return fakePlayer;
    }

    public void addRoomLocation(RoomLocation roomLocation) {
        roomLocations.add(roomLocation);
    }

    public List<RoomLocation> getRoomLocations() {
        return roomLocations;
    }

    public DungeonRoom getDungeonRoom() {
        return roomLocations.stream().filter(room -> room.getBoundingBox().contains(player.getBoundingBox())).map(RoomLocation::getDungeonRoom).findAny().orElse(null);
    }

    public DungeonRoom getDungeonRoom(Location location) {
        return roomLocations.stream().filter(room -> room.getBoundingBox().contains(BoundingBox.of(location.getBlock()))).map(RoomLocation::getDungeonRoom).findAny().orElse(null);
    }

    public RoomLocation getRoomLocation(DungeonRoom dungeonRoom) {
        return roomLocations.stream().filter(room -> room.getDungeonRoom().equals(dungeonRoom)).findAny().orElse(null);
    }

    public void setRoomCompleted(DungeonRoom room) {
        RoomLocation roomLocation = getRoomLocation(room);
        roomLocation.setCompleted();
        room.onCompletion(this, roomLocation);
    }

    public int getRoomsCompleted() {
        return (int) roomLocations.stream().filter(RoomLocation::isCompleted).count();
    }

    public BossBar getBar() {
        return bar;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

    public boolean hasKey() {
        return hasKey;
    }
}
