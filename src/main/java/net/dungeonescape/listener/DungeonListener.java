package net.dungeonescape.listener;

import com.destroystokyo.paper.event.entity.EndermanEscapeEvent;
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.DungeonGame;
import net.dungeonescape.Message;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.common.FakePlayer;
import net.dungeonescape.common.key.Key;
import net.dungeonescape.dungeon.DoorLocation;
import net.dungeonescape.dungeon.DungeonRoom;
import net.dungeonescape.dungeon.RoomLocation;
import net.dungeonescape.dungeon.rooms.EndRoom;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.MetadataValue;

import java.util.Optional;
import java.util.UUID;

public class DungeonListener implements Listener {
    private final DungeonEscapeMain plugin;

    public DungeonListener(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        if (!e.getEntity().getWorld().getName().startsWith("dungeonescape")) return;
        e.setFoodLevel(20);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!e.getEntity().getWorld().getName().startsWith("dungeonescape")) return;
        e.setDroppedExp(0);
        e.getDrops().clear();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        DungeonPlayer dungeonPlayer = game.getDungeonPlayer(p);
        dungeonPlayer.getFakePlayer().move(p.getLocation().clone().add(game.getInverseOffset(dungeonPlayer), 0, 0));
        dungeonPlayer.getFakePlayer().glide(p.isGliding());

        p.getWorld().getNearbyEntitiesByType(ArmorStand.class, p.getLocation(), 1, 2, 1).stream().filter(stand -> stand.getBoundingBox().overlaps(p.getBoundingBox())).forEach(stand -> {
            Key key = plugin.getKeyManager().isKey(stand);
            if (key != null) {
                p.getWorld().playSound(key.getStand(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                dungeonPlayer.setHasKey(true);
                dungeonPlayer.setRoomCompleted(key.getRoom());
                plugin.getKeyManager().remove(key);
            }
        });

        DungeonRoom oldRoom = dungeonPlayer.getDungeonRoom(e.getFrom());
        DungeonRoom newRoom = dungeonPlayer.getDungeonRoom(e.getTo());
        if (oldRoom == null || oldRoom.equals(newRoom)) return;
        if (newRoom == null || newRoom.getName().equals("START")) return;
        oldRoom.onLeave(dungeonPlayer, dungeonPlayer.getRoomLocation(oldRoom));
        newRoom.onEnter(dungeonPlayer, dungeonPlayer.getRoomLocation(newRoom));
    }

    @EventHandler
    public void onAttackEntity(EntityDamageByEntityEvent e) {
        if (!e.getEntity().getWorld().getName().startsWith("dungeonescape")) return;
        if (!(e.getDamager() instanceof Player)) return;
        if (e.getEntityType() != EntityType.SHULKER || e.getEntityType() != EntityType.ARMOR_STAND) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteractEntity(PlayerArmorStandManipulateEvent e) {
        if (!e.getPlayer().getWorld().getName().startsWith("dungeonescape")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onOpen(PlayerInteractEvent e) {
        if (!e.getPlayer().getWorld().getName().startsWith("dungeonescape")) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null || !e.getClickedBlock().getType().isInteractable()) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onDeathLeave(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        e.deathMessage(Component.empty());
        DungeonPlayer dungeonPlayer = game.getDungeonPlayer(p);
        DungeonRoom oldRoom = dungeonPlayer.getDungeonRoom(p.getLocation());
        oldRoom.onLeave(dungeonPlayer, dungeonPlayer.getRoomLocation(oldRoom));
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if (!e.getLocation().getWorld().getName().startsWith("dungeonescape")) return;
        e.blockList().removeAll(e.blockList());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (!e.getEntity().getWorld().getName().startsWith("dungeonescape")) return;
        e.getDrops().clear();
    }

    @EventHandler
    public void onSwing(PlayerAnimationEvent e) {
        if (e.isCancelled()) return;
        DungeonGame game = plugin.getGameManager().getGame(e.getPlayer());
        if (game == null) return;
        game.getDungeonPlayer(e.getPlayer()).getFakePlayer().swing();
    }

    @EventHandler
    public void onCrouch(PlayerToggleSneakEvent e) {
        if (e.isCancelled()) return;
        DungeonGame game = plugin.getGameManager().getGame(e.getPlayer());
        if (game == null) return;
        game.getDungeonPlayer(e.getPlayer()).getFakePlayer().sneak();
    }

    @EventHandler
    public void onHold(PlayerItemHeldEvent e) {
        if (e.isCancelled()) return;
        DungeonGame game = plugin.getGameManager().getGame(e.getPlayer());
        if (game == null) return;
        game.getDungeonPlayer(e.getPlayer()).getFakePlayer().hold(e.getPlayer().getInventory().getItem(e.getNewSlot()), false);
    }

    @EventHandler
    public void onArmor(PlayerArmorChangeEvent e) {
        DungeonGame game = plugin.getGameManager().getGame(e.getPlayer());
        if (game == null) return;
        game.getDungeonPlayer(e.getPlayer()).getFakePlayer().armor(e.getNewItem(), e.getSlotType());
    }

    @EventHandler
    public void onSwitch(PlayerSwapHandItemsEvent e) {
        if (e.isCancelled()) return;
        DungeonGame game = plugin.getGameManager().getGame(e.getPlayer());
        if (game == null) return;
        FakePlayer fakePlayer = game.getDungeonPlayer(e.getPlayer()).getFakePlayer();
        fakePlayer.hold(e.getMainHandItem(), false);
        fakePlayer.hold(e.getOffHandItem(), true);
    }

    @EventHandler
    public void onDeathTeleport(PlayerDeathEvent e) {
        if (e.isCancelled()) return;
        DungeonGame game = plugin.getGameManager().getGame(e.getPlayer());
        if (game == null) return;
        DungeonPlayer player = game.getDungeonPlayer(e.getPlayer());
        player.getFakePlayer().teleport(player.getLocation().add(game.getInverseOffset(player) + 0.5, 0, 0.5));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.isCancelled()) return;
        DungeonGame game = plugin.getGameManager().getGame(e.getPlayer());
        if (game == null) return;
        DungeonPlayer player = game.getDungeonPlayer(e.getPlayer());
        player.getFakePlayer().teleport(e.getTo().clone().add(game.getInverseOffset(player), 0, 0));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!e.getPlayer().getWorld().getName().startsWith("dungeonescape")) return;
        e.setShouldDropExperience(false);
        e.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        DungeonPlayer player = game.getDungeonPlayer(e.getPlayer());
        p.teleport(player.getLocation().add(0.5, 0, 0.5));
        game.giveItems(p);
        player.getFakePlayer().hold(e.getPlayer().getInventory().getItemInMainHand(), false);
        player.getFakePlayer().hold(e.getPlayer().getInventory().getItemInOffHand(), true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!e.getPlayer().getWorld().getName().startsWith("dungeonescape")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.isCancelled() || !e.getWhoClicked().getWorld().getName().startsWith("dungeonescape")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onPop(ProjectileHitEvent e) {
        if (e.isCancelled() || e.getHitBlock() == null || !e.getHitBlock().getWorld().getName().startsWith("dungeonescape")) return;
        if (e.getHitBlock().getType().equals(Material.CHORUS_FLOWER)) e.setCancelled(true);
        if (e.getEntity() instanceof Arrow) e.getEntity().remove();
    }

    @EventHandler
    public void onDamageEnderman(EntityDamageByEntityEvent e) {
        if (!e.getEntity().getWorld().getName().startsWith("dungeonescape")) return;
        if (!e.getEntityType().equals(EntityType.ENDERMAN)) return;
        if (!e.getDamager().getType().equals(EntityType.ENDER_DRAGON) && !e.getDamager().getType().equals(EntityType.DRAGON_FIREBALL)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onKnockbackEnderman(EntityKnockbackByEntityEvent e) {
        if (!e.getEntity().getWorld().getName().startsWith("dungeonescape")) return;
        if (!e.getEntityType().equals(EntityType.ENDERMAN)) return;
        if (!e.getHitBy().getType().equals(EntityType.ENDER_DRAGON)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onEndermanTeleport(EndermanEscapeEvent e) {
        if (!e.getEntity().getWorld().getName().startsWith("dungeonescape")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (!(e.getTarget() instanceof Player player)) return;
        DungeonGame game = plugin.getGameManager().getGame(player);
        if (game == null) return;
        Optional<MetadataValue> metadata = e.getEntity().getMetadata("spawnRoom").stream().findFirst();
        if (metadata.isEmpty()) return;
        if (game.getDungeonPlayer(player).getDungeonRoom() == metadata.get().value()) return;
        e.setTarget(null);
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveFromWorldEvent e) {
        if (!e.getEntity().getWorld().getName().startsWith("dungeonescape")) return;
        if (!(e.getEntity() instanceof LivingEntity entity)) return;
        UUID uuid = plugin.getCustomMobManager().removeEntity(entity);
        if (uuid == null) return;
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        DungeonPlayer player = game.getDungeonPlayer(p);
        if (player == null) return;
        if (e.getEntityType().equals(EntityType.ENDER_DRAGON)) {
            ((EndRoom) player.getRoomLocation(plugin.getRoomManager().getRoom("END")).getDungeonRoom()).onKill(player);
            return;
        }
        Optional<MetadataValue> metadata = entity.getMetadata("spawnRoom").stream().findFirst();
        if (metadata.isEmpty()) return;
        if (plugin.getCustomMobManager().getEntities(player.getPlayer().getUniqueId()).size() == 0) {
            plugin.getKeyManager().create(entity.getLocation(), (DungeonRoom) metadata.get().value());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!e.getEntity().getWorld().getName().startsWith("dungeonescape")) return;
        if (!(e.getEntity() instanceof LivingEntity entity)) return;
        if (!plugin.getCustomMobManager().getEntities().contains(entity)) return;
        AttributeInstance attribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> entity.customName(plugin.getCustomMobManager().getHealthBar((float) entity.getHealth(), (float) attribute.getBaseValue())));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        DungeonGame game = plugin.getGameManager().getGame(e.getPlayer());
        if (game == null) return;
        plugin.getGameManager().end(game.getId());
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        if (!e.getWorld().getName().startsWith("dungeonescape")) return;
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onThunder(ThunderChangeEvent e) {
        if (!e.getWorld().getName().startsWith("dungeonescape")) return;
        if (e.toThunderState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        if (b == null) return;
        DungeonGame game = plugin.getGameManager().getGame(p);
        if (game == null) return;
        if (e.getHand() == null || !e.getHand().equals(EquipmentSlot.HAND)) return;
        DungeonPlayer dungeonPlayer = game.getDungeonPlayer(p);
        DungeonRoom dungeonRoom = dungeonPlayer.getDungeonRoom();
        if (dungeonRoom == null) return;

        for (DoorLocation doorLocation : dungeonRoom.getDoors()) {
            Location roomLocation = dungeonPlayer.getRoomLocation(dungeonRoom).getLocation();
            Location location = roomLocation.clone().add(doorLocation.getVector());

            if (!doorLocation.contains(b.getLocation().clone().subtract(location))) continue;

            for (RoomLocation room : dungeonPlayer.getRoomLocations()) {
                if (room.getDungeonRoom().getEntrance() == null) continue;
                if (!room.getLocation().add(room.getDungeonRoom().getEntrance().getVector()).equals(location)) continue;
                if (room.isUnlocked()) return;
                if (!dungeonPlayer.hasKey()) {
                    Message.DOOR_NO_KEY.send(p);
                    e.getClickedBlock().getLocation().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
                    return;
                }
                if (room.getDungeonRoom().getName().equals("END") && dungeonPlayer.getRoomsCompleted() < 11) {
                    Message.FINAL_DOOR_NOT_LAST.send(p);
                    e.getClickedBlock().getLocation().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
                    return;
                }
                e.getClickedBlock().getLocation().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                room.setUnlocked();
                room.getDungeonRoom().onUnlock(dungeonPlayer, room);
                dungeonPlayer.setHasKey(false);
                break;
            }

            Location location1 = location.clone().add(doorLocation.getMin());
            Location location2 = location.clone().add(doorLocation.getMax());
            int lowX = Math.min(location1.getBlockX(), location2.getBlockX());
            int lowY = Math.min(location1.getBlockY(), location2.getBlockY());
            int lowZ = Math.min(location1.getBlockZ(), location2.getBlockZ());

            for (int x = 0; x <= Math.abs(location1.getBlockX() - location2.getBlockX()); x++) {
                for (int y = 0; y <= Math.abs(location1.getBlockY() - location2.getBlockY()); y++) {
                    for (int z = 0; z <= Math.abs(location1.getBlockZ() - location2.getBlockZ()); z++) {
                        Block block = new Location(p.getWorld(), lowX + x, lowY + y, lowZ + z).getBlock();
                        sendPacket(block.getLocation(), block.getY() - 200, p);
                    }
                }
            }
            break;
        }
    }

    public void sendPacket(Location loc, long y, Player player) {
        ServerLevel world = ((CraftWorld) loc.getWorld()).getHandle();
        FallingBlockEntity fallingBlock = new FallingBlockEntity(world, loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5, Blocks.COAL_BLOCK.defaultBlockState());
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        player.sendBlockChange(loc, Bukkit.createBlockData(Material.AIR));
        connection.send(fallingBlock.getAddEntityPacket());
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            connection.send(new ClientboundRemoveEntitiesPacket(fallingBlock.getId()));
            loc.getBlock().setType(Material.AIR);
        }, y*3+4);
    }
}
