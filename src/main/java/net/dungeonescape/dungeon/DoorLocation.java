package net.dungeonescape.dungeon;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class DoorLocation {
    private final Vector vector;
    private final double rotation;
    private final Vector min;
    private final Vector max;

    public DoorLocation(Vector vector, double rotation) {
        this.vector = vector;
        this.rotation = rotation;
        this.min = getLowerVector(rotation);
        this.max = getHigherVector(rotation);
    }

    public Vector getVector() {
        return vector;
    }

    public double getRotation() {
        return rotation;
    }

    public boolean contains(Location location) {
        return location.getX() >= min.getX() && location.getY() >= min.getY() && location.getZ() >= min.getZ() && location.getX() <= max.getX() && location.getY() <= max.getY() && location.getZ() <= max.getZ();
    }
    public Vector getMin() {
        return min.clone();
    }

    public Vector getMax() {
        return max.clone();
    }

    private Vector getLowerVector(double rotation) {
        if (rotation == 0 || rotation == 180) {
            return new Vector(-1, 0, 0);
        } else {
            return new Vector(0, 0, -1);
        }
    }

    private Vector getHigherVector(double rotation) {
        if (rotation == 0 || rotation == 180) {
            return new Vector(1, 4, 0);
        } else {
            return new Vector(0, 4, 1);
        }
    }
}
