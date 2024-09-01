package net.dungeonescape.common.flame;

import org.bukkit.Location;

public class Flame {
    private final Location location;
    private int ticks;

    public Flame(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public int getTicks() {
        return ticks;
    }

    public void incrementTicks() {
        ticks++;
    }
}
