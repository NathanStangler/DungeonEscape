package net.dungeonescape;

import net.dungeonescape.common.DungeonItems;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.common.FakePlayer;
import net.dungeonescape.common.map.DungeonMapRenderer;
import net.dungeonescape.dungeon.RoomLocation;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.File;
import java.io.IOException;

public class DungeonGame {
    private final DungeonEscapeMain plugin;
    private final String id;
    private final DungeonPlayer[] players = new DungeonPlayer[2];

    public DungeonGame(DungeonEscapeMain plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    public void startGame(Player player1, Player player2) {
        try {
            FileUtils.copyDirectory(new File(plugin.getDataFolder() + File.separator + "world"), new File("dungeonescape/" + id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        World world = WorldCreator.name("dungeonescape/" + id).createWorld();
        world.setAutoSave(false);

        players[0] = new DungeonPlayer(player1, new Location(world, -100, 200, 0), new FakePlayer(plugin, player1, player2));
        players[1] = new DungeonPlayer(player2, new Location(world, 100, 200, 0), new FakePlayer(plugin, player2, player1));

        for (DungeonPlayer player : players) {
            player.getPlayer().teleport(player.getLocation().add(0.5, 0, 0.5));
            plugin.getRoomManager().getRooms().forEach(room -> player.addRoomLocation(new RoomLocation(room, player.getLocation().add(room.getShift()))));
        }

        for (DungeonPlayer player : players) {
            Player p = player.getPlayer();
            resetPlayer(p);
            p.setBedSpawnLocation(player.getLocation(), true);
            p.teleport(player.getLocation().add(0.5, 0, 0.5));
            giveItems(p);
            player.getFakePlayer().spawn();
            player.getRoomLocation(plugin.getRoomManager().getRoom("START")).setCompleted();
        }

        plugin.getBossBarManager().addGame(this);
    }

    public void giveItems(Player p) {
        PlayerInventory inv = p.getInventory();
        inv.setHelmet(DungeonItems.getEnchanted(Material.CHAINMAIL_HELMET, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        inv.setChestplate(DungeonItems.getEnchanted(Material.CHAINMAIL_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        inv.setLeggings(DungeonItems.getEnchanted(Material.CHAINMAIL_LEGGINGS, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        inv.setBoots(DungeonItems.getEnchanted(Material.CHAINMAIL_BOOTS, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        inv.setItem(0, DungeonItems.getEnchanted(Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 1));
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        MapView mapView = Bukkit.createMap(p.getWorld());
        for (MapRenderer mapRenderer : mapView.getRenderers()) {
            mapView.removeRenderer(mapRenderer);
        }
        mapView.addRenderer(new DungeonMapRenderer(plugin));
        mapMeta.setMapView(mapView);
        map.setItemMeta(mapMeta);
        inv.setItem(8, new ItemStack(map));
    }

    public void resetGame() {
        plugin.getBossBarManager().removeGame(this);
        for (DungeonPlayer player : players) {
            resetPlayer(player.getPlayer());
            player.getRoomLocations().forEach(roomLocation -> {
                if (roomLocation.isUnlocked()) {
                    roomLocation.getDungeonRoom().onCompletion(player, roomLocation);
                }
            });
            player.getFakePlayer().remove();
            player.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
        }

        players[0] = null;
        players[1] = null;

        Bukkit.unloadWorld("dungeonescape/" + id, false);

        try {
            FileUtils.deleteDirectory(new File("dungeonescape/" + id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void resetPlayer(Player p) {
        p.setHealth(20);
        p.setFoodLevel(40);
        p.setFireTicks(0);
        p.setExp(0);
        p.getInventory().clear();
        p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
        p.setGameMode(GameMode.SURVIVAL);
        p.setBedSpawnLocation(null);
        p.clearTitle();
    }

    public DungeonPlayer[] getPlayers() {
        return players;
    }

    public DungeonPlayer getEnemy(DungeonPlayer player) {
        if (player.equals(players[0])) {
            return players[1];
        } else {
            return players[0];
        }
    }

    public int getInverseOffset(DungeonPlayer player) {
        if (player.equals(players[0])) {
            return 200;
        } else {
            return -200;
        }
    }

    public DungeonPlayer getDungeonPlayer(Player p) {
        for (DungeonPlayer player : players) {
            if (player.getPlayer().equals(p)) return player;
        }
        return null;
    }

    public String getId() {
        return id;
    }
}
