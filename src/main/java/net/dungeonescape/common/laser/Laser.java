package net.dungeonescape.common.laser;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Laser {
    private final Location start;
    private final Location end;
    private final int ticks;
    private final Vector startIncrement;
    private final Vector endIncrement;
    private boolean direction;
    private int tick;

    public Laser(Location start, Location end, int ticks, Vector startIncrement, Vector endIncrement) {
        this.start = start;
        this.end = end;
        this.ticks = ticks;
        this.startIncrement = startIncrement;
        this.endIncrement = endIncrement;
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }

    public int getTicks() {
        return ticks;
    }

    public Vector getStartIncrement() {
        return startIncrement;
    }

    public Vector getEndIncrement() {
        return endIncrement;
    }

    public boolean getDirection() {
        return direction;
    }

    public void incrementTicks() {
        if (++tick == ticks) {
            tick = 0;
            direction = !direction;
        }
    }
}
