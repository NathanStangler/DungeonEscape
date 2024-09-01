package net.dungeonescape.common.blinkingblock;

import org.bukkit.Location;
import org.bukkit.Material;

public class BlinkingBlock {
    private final Location location;
    private final Material material;

    public BlinkingBlock(Location location, Material material) {
        this.location = location;
        this.material = material;
    }

    public Location getLocation() {
        return location;
    }

    public Material getMaterial() {
        return material;
    }
}
