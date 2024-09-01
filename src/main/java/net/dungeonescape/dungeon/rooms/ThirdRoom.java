package net.dungeonescape.dungeon.rooms;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.dungeon.DoorLocation;
import net.dungeonescape.dungeon.DungeonRoom;
import net.dungeonescape.dungeon.RoomLocation;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.UUID;

public class ThirdRoom extends DungeonRoom {
    public ThirdRoom(DungeonEscapeMain plugin) {
        super(plugin, "THIRD", new Vector(-22, 0, 22), new BoundingBox(11, -32, 11, -11, 30, -11), new DoorLocation(new Vector(11, 0, 0), -90), null);
    }

    @Override
    public void onUnlock(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        UUID uuid = dungeonPlayer.getPlayer().getUniqueId();
        Location room = roomLocation.getLocation();
        addLaser(uuid, room, new Vector(9.5, 0.5, 2.5), new Vector(5, 0.5, 2.5), new Vector(9.5, 0.5, 4.5), new Vector(5, 0.5, 4.5), 10);
        addLaser(uuid, room, new Vector(4.5, 0.5, 9.5), new Vector(4.5, 0.5, 5), new Vector(), new Vector(), 0);
        addLaser(uuid, room, new Vector(2.5, 0.5, 9.5), new Vector(2.5, 0.5, 5), new Vector(2.5, 4.5, 9.5), new Vector(2.5, 4.5, 5), 15);
        addLaser(uuid, room, new Vector(0.5, 0.5, 9.5), new Vector(0.5, 0.5, 5), new Vector(), new Vector(), 0);
        addLaser(uuid, room, new Vector(-1.5, 0.5, 9.5), new Vector(-1.5, 0.5, 5), new Vector(-3.5, 0.5, 9.5), new Vector(-3.5, 0.5, 5), 20);
        addLaser(uuid, room, new Vector(-8.5, 2.5, 3.5), new Vector(-4, 2.5, 3.5), new Vector(-8.5, 5.5, 3.5), new Vector(-4, 5.5, 3.5), 20);
        addLaser(uuid, room, new Vector(-8.5, 8.5, -2.5), new Vector(-4, 8.5, -2.5), new Vector(-8.5, 11.5, -2.5), new Vector(-4, 11.5, -2.5), 30);
        addLaser(uuid, room, new Vector(-3.5, 7.5, -7), new Vector(-3.5, 13.5, -7), new Vector(-3.5, 7.5, -4), new Vector(-3.5, 13.5, -4), 15);
        addLaser(uuid, room, new Vector(-2.5, 7.5, -7), new Vector(-2.5, 13.5, -7), new Vector(-2.5, 7.5, -4), new Vector(-2.5, 13.5, -4), 20);
        addLaser(uuid, room, new Vector(2.5, 9.5, -3.5), new Vector(-2, 9.5, -3.5), new Vector(2.5, 9.5, 4.5), new Vector(-2, 9.5, 4.5), 60);
        addLaser(uuid, room, new Vector(1, 7.5, -1.5), new Vector(1, 13.5, -1.5), new Vector(-2, 7.5, -1.5), new Vector(-2, 13.5, -1.5), 20);
        addLaser(uuid, room, new Vector(1, 7.5, 0.5), new Vector(1, 13.5, 0.5), new Vector(-2, 7.5, 0.5), new Vector(-2, 13.5, 0.5), 30);
        addLaser(uuid, room, new Vector(1, 7.5, 2.5), new Vector(1, 13.5, 2.5), new Vector(-2, 7.5, 2.5), new Vector(-2, 13.5, 2.5), 40);
        addLaser(uuid, room, new Vector(1.5, 10.7, 3.5), new Vector(1.5, 10.7, 8), new Vector(4.5, 10.7, 3.5), new Vector(4.5, 10.7, 8), 20);
        addLaser(uuid, room, new Vector(3.5, 7.5, 3.5), new Vector(8, 13.5, 3.5), new Vector(9.5, 7.5, 3.5), new Vector(5, 13.5, 3.5), 10);
        plugin.getKeyManager().create(roomLocation.getLocation().add(6.5, 9, 0.5), this);
    }

    @Override
    public void onCompletion(DungeonPlayer dungeonPlayer, RoomLocation roomLocation) {
        plugin.getLaserManager().remove(dungeonPlayer.getPlayer().getUniqueId());
    }

    private void addLaser(UUID uuid, Location room, Vector start1, Vector end1, Vector start2, Vector end2, int ticks) {
        plugin.getLaserManager().create(uuid, room.clone().add(start1), room.clone().add(end1), room.clone().add(start2), room.clone().add(end2), ticks);
    }
}
