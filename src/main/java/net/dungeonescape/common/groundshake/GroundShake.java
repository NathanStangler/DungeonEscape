package net.dungeonescape.common.groundshake;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GroundShake {
    private final Player player;
    private final Location location;
    private final int stand;
    private final int block;
    private boolean direction;
    private int ticks;

    public GroundShake(Player player, Location location, int stand, int block) {
        this.player = player;
        this.location = location;
        this.stand = stand;
        this.block = block;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public int getStand() {
        return stand;
    }

    public int getBlock() {
        return block;
    }

    public boolean getDirection() {
        return direction;
    }

    public void switchDirection() {
        direction = !direction;
    }

    public int getTicks() {
        return ticks;
    }

    public void incrementTicks() {
        ticks++;
    }
}
