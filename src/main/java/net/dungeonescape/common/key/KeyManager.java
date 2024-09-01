package net.dungeonescape.common.key;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.ReflectionHandler;
import net.dungeonescape.dungeon.DungeonRoom;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KeyManager {
    private static final List<Vector> VECTORS = drawCircle();
    private static final ItemStack SKULL = getSkull();
    private final DungeonEscapeMain plugin;
    private final Set<Key> keys = ConcurrentHashMap.newKeySet();
    private int index = 0;
    private int task = -1;

    public KeyManager(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    public void create(Location location, DungeonRoom room) {
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location.clone().subtract(0, 1, 0), EntityType.ARMOR_STAND);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setAI(false);
        stand.setInvulnerable(true);
        stand.getEquipment().setHelmet(SKULL);
        keys.add(new Key(stand, room));
    }

    public void remove(Key key) {
        key.getStand().remove();
        keys.remove(key);
    }

    public Key isKey(ArmorStand stand) {
        return keys.stream().filter(key -> key.getStand().equals(stand)).findAny().orElse(null);
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            keys.forEach(key -> {
                key.getStand().getWorld().spawnParticle(Particle.REDSTONE, key.getStand().getLocation().add(VECTORS.get(index)), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.GRAY, 3), true);
                Location loc = key.getStand().getLocation();
                key.getStand().setRotation(loc.getYaw() + 5, loc.getPitch());
            });
            if (++index >= 50) index = 0;
        }, 0L, 1L).getTaskId();
    }

    public void close() {
        Bukkit.getScheduler().cancelTask(task);
        task = -1;
        keys.forEach(this::remove);
    }

    public static List<Vector> drawCircle() {
        List<Vector> vectors = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            double angle = Math.PI * i / 25;
            vectors.add(new Vector(1.4 * Math.sin(angle), 1.5, 1.4 * Math.cos(angle)));
        }
        return vectors;
    }

    private static ItemStack getSkull() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2Q4NzZmODJkNTNjNmIxM2Q4OGViMjFiZTMyMTE5ZjFhYWI2MzFkZGY2ZjZhZTlkNmJjMzc0MmVkMWRlYSJ9fX0="));
        ReflectionHandler.setField(meta, "profile", profile);
        head.setItemMeta(meta);
        return head;
    }
}
