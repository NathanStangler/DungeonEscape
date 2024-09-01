package net.dungeonescape.common.movingblock;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Shulker;
import org.bukkit.util.Vector;

public class MovingBlock {
    private final Location location;
    private final FallingBlock block;
    private final Shulker shulker;
    private final ArmorStand stand;
    private final Vector increment;
    private boolean direction;
    private int ticks;

    public MovingBlock(Location location, FallingBlock block, Shulker shulker, ArmorStand stand, Vector increment) {
        this.location = location;
        this.block = block;
        this.shulker = shulker;
        this.stand = stand;
        this.increment = increment;
    }

    public Location getLocation() {
        return location;
    }

    public FallingBlock getBlock() {
        return block;
    }

    public ArmorStand getStand() {
        return stand;
    }

    public Shulker getShulker() {
        return shulker;
    }

    public Vector getIncrement() {
        return increment;
    }

    public boolean getDirection() {
        return direction;
    }

    public void incrementTicks() {
        if (++ticks == 60) {
            ticks = 0;
            direction = !direction;
        }
    }
}
