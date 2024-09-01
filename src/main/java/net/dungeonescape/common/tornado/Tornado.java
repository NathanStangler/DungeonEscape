package net.dungeonescape.common.tornado;

import org.bukkit.Location;

public class Tornado {
    private final Location location;
    private int ticks;

    public Tornado(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public int getTicks() {
        return ticks;
    }

    public int incrementTicks() {
        return ++ticks;
    }
}
