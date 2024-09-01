package net.dungeonescape.common;

import java.util.HashMap;

public class HeightManager {
    private final HashMap<DungeonPlayer, Integer> height = new HashMap<>();

    public int getHeight(DungeonPlayer player) {
        return height.getOrDefault(player, 200);
    }

    public void setHeight(DungeonPlayer player, int max) {
        height.put(player, max);
    }

    public void remove(DungeonPlayer player) {
        height.remove(player);
    }
}
