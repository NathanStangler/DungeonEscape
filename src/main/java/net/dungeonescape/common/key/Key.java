package net.dungeonescape.common.key;

import net.dungeonescape.dungeon.DungeonRoom;
import org.bukkit.entity.ArmorStand;

public class Key {
    private final ArmorStand stand;
    private final DungeonRoom room;

    public Key(ArmorStand stand, DungeonRoom room) {
        this.stand = stand;
        this.room = room;
    }

    public ArmorStand getStand() {
        return stand;
    }

    public DungeonRoom getRoom() {
        return room;
    }
}
