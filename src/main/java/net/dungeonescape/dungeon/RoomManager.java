package net.dungeonescape.dungeon;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.dungeon.rooms.*;

import java.util.HashMap;
import java.util.List;

public class RoomManager {
    private final HashMap<String, DungeonRoom> rooms = new HashMap<>();

    public RoomManager(DungeonEscapeMain plugin) {
        addRoom(new StartRoom(plugin));
        addRoom(new SecondRoom(plugin));
        addRoom(new ThirdRoom(plugin));
        addRoom(new FourthRoom(plugin));
        addRoom(new FifthRoom(plugin));
        addRoom(new SixthRoom(plugin));
        addRoom(new SeventhRoom(plugin));
        addRoom(new EighthRoom(plugin));
        addRoom(new NinthRoom(plugin));
        addRoom(new TenthRoom(plugin));
        addRoom(new EleventhRoom(plugin));
        addRoom(new EndRoom(plugin));
    }

    private void addRoom(DungeonRoom dungeonRoom) {
        rooms.put(dungeonRoom.getName(), dungeonRoom);
    }

    public DungeonRoom getRoom(String name) {
        return rooms.get(name);
    }

    public List<DungeonRoom> getRooms() {
        return rooms.values().stream().toList();
    }
}
