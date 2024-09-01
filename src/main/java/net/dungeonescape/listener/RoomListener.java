package net.dungeonescape.listener;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.DungeonGame;
import net.dungeonescape.Message;
import net.dungeonescape.common.DungeonItems;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.dungeon.DungeonRoom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class RoomListener implements Listener {
    private final DungeonEscapeMain plugin;

    public RoomListener(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent e) {
        Player p = e.getPlayer();
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        e.setCancelled(true);
        for (DungeonPlayer player : game.getPlayers()) {
            player.getPlayer().sendMessage(Message.DUNGEON_COMPLETED.build(p.getName()));
        }
        plugin.getGameManager().end(game.getId());
    }

    @EventHandler
    public void onFall(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        DungeonRoom room = game.getDungeonPlayer(p).getDungeonRoom();
        if (room == null) return;
        if (!room.getName().equals("SIXTH") && !room.getName().equals("TENTH")) return;
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onParkour(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        DungeonPlayer player = game.getDungeonPlayer(p);
        DungeonRoom room = player.getDungeonRoom();
        if (room == null) return;
        if (!room.getName().equals("SIXTH") && !room.getName().equals("TENTH")) return;
        if (player.getRoomLocation(room).isCompleted()) return;
        int height = e.getTo().getBlockY();
        if (e.getFrom().getBlockY() >= height) return;
        if (height > plugin.getHeightManager().getHeight(player)) {
            plugin.getHeightManager().setHeight(player, height);
        }
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        DungeonPlayer dungeonPlayer = game.getDungeonPlayer(p);
        DungeonRoom room = dungeonPlayer.getDungeonRoom();
        if (room == null) return;
        if (!room.getName().equals("SIXTH") && !room.getName().equals("TENTH")) return;
        ItemStack item = e.getItem();
        if (item == null || !item.equals(DungeonItems.LEAPER)) return;
        if (p.hasCooldown(item.getType())) return;
        Block b = p.getLocation().subtract(0, 1, 0).getBlock();
        if (!b.getType().equals(Material.WHITE_STAINED_GLASS) && !b.getType().equals(Material.WHITE_CONCRETE) && !b.getType().equals(Material.LIGHT_GRAY_STAINED_GLASS) && !b.getType().equals(Material.GRAY_CONCRETE)) {
            Message.NOT_ON_GROUND_LEAP.send(p);
            return;
        }
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 0.4F);
        p.setVelocity(new Vector(0, Math.sqrt(0.16 * Math.max(plugin.getHeightManager().getHeight(dungeonPlayer) - 200, 1)), 0));
        p.setCooldown(item.getType(), 200);
    }

    @EventHandler
    public void onElytra(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        DungeonRoom room = game.getDungeonPlayer(p).getDungeonRoom();
        if (room == null) return;
        if (!room.getName().equals("FOURTH")) return;

        int radius = 2;
        Location loc = p.getLocation();
        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                for (int z = -radius; z < radius; z++) {
                    Block block = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                    if (block.getType() == Material.RED_CONCRETE && p.getBoundingBox().expand(0.2).overlaps(block.getBoundingBox())) {
                        p.damage(p.getHealth()*2);
                    }
                }
            }
        }
        if (!p.isGliding() && !p.isOnGround() && !p.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.COAL_BLOCK)) {
            p.setGliding(true);
        }
        if (!p.isGliding()) return;
        p.setVelocity(p.getLocation().getDirection().multiply(0.02).normalize());
    }

    @EventHandler
    public void onElytraDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        DungeonRoom room = game.getDungeonPlayer(p).getDungeonRoom();
        if (room == null) return;
        if (!room.getName().equals("FOURTH")) return;
        if (!p.isGliding()) return;
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onElytraGlide(EntityToggleGlideEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        DungeonRoom room = game.getDungeonPlayer(p).getDungeonRoom();
        if (room == null) return;
        if (!room.getName().equals("FOURTH")) return;
        if (!p.isGliding()) return;
        if (p.isOnGround() || p.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.COAL_BLOCK)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPressurePlate(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        DungeonRoom room = game.getDungeonPlayer(p).getDungeonRoom();
        if (room == null) return;
        if (!room.getName().equals("SEVENTH")) return;
        if (e.getAction() != Action.PHYSICAL || e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.STONE_PRESSURE_PLATE) return;
        int radius = 7;
        Location loc = p.getLocation();
        World world = loc.getWorld();
        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                for (int z = -radius; z < radius; z++) {
                    Block block = world.getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                    if (block.getType() == Material.DISPENSER) {
                        Directional directional = (Directional) block.getBlockData();
                        BlockFace face = directional.getFacing();
                        world.spawnArrow(block.getRelative(face).getLocation().add(0.5, 0.5, 0.5), face.getDirection(), 3, 0).setDamage(2);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBreakTuff(BlockBreakEvent e) {
        Player p = e.getPlayer();
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        DungeonRoom room = game.getDungeonPlayer(p).getDungeonRoom();
        if (room == null) return;
        if (!room.getName().equals("FIFTH")) {
            e.setCancelled(true);
            return;
        }
        if (!e.getBlock().getType().equals(Material.DEEPSLATE) && !e.getBlock().getType().equals(Material.TUFF)) {
            e.setCancelled(true);
            return;
        }
        e.setDropItems(false);
        if (!e.getBlock().getType().equals(Material.TUFF)) return;
        plugin.getKeyManager().create(e.getBlock().getLocation().add(0.5, 0, 0.5), room);
    }
}
