package net.dungeonescape.common;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.dungeonescape.DungeonEscapeMain;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakePlayer extends ServerPlayer {
    private final DungeonEscapeMain plugin;
    private final Player fromPlayer;
    private final Player toPlayer;

    public FakePlayer(DungeonEscapeMain plugin, Player fromPlayer, Player toPlayer) {
        super(((CraftWorld) fromPlayer.getWorld()).getHandle().getServer(), ((CraftWorld) fromPlayer.getWorld()).getHandle(), getProfile(fromPlayer), ((CraftPlayer) fromPlayer).getHandle().getProfilePublicKey());
        this.plugin = plugin;
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
    }

    public void spawn() {
        setPos(toPlayer.getLocation().getBlockX(), toPlayer.getLocation().getBlockY(), toPlayer.getLocation().getBlockZ());
        ServerGamePacketListenerImpl connection = ((CraftPlayer) toPlayer).getHandle().connection;
        connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, this));
        connection.send(new ClientboundAddPlayerPacket(this));
        Bukkit.getScheduler().runTaskLater(plugin, () -> connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, this)), 1L);

        PlayerTeam team = new PlayerTeam(new Scoreboard(), "");
        team.setCollisionRule(Team.CollisionRule.PUSH_OTHER_TEAMS);
        connection.send(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));
        connection.send(ClientboundSetPlayerTeamPacket.createMultiplePlayerPacket(team, List.of(toPlayer.getName(), String.valueOf(this.getId())), ClientboundSetPlayerTeamPacket.Action.ADD));

        PlayerInventory inv = fromPlayer.getInventory();
        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();
        list.add(Pair.of(EquipmentSlot.MAINHAND, net.minecraft.world.item.ItemStack.fromBukkitCopy(inv.getItemInMainHand())));
        list.add(Pair.of(EquipmentSlot.OFFHAND, net.minecraft.world.item.ItemStack.fromBukkitCopy(inv.getItemInOffHand())));
        list.add(Pair.of(EquipmentSlot.FEET, net.minecraft.world.item.ItemStack.fromBukkitCopy(inv.getBoots())));
        list.add(Pair.of(EquipmentSlot.LEGS, net.minecraft.world.item.ItemStack.fromBukkitCopy(inv.getLeggings())));
        list.add(Pair.of(EquipmentSlot.CHEST, net.minecraft.world.item.ItemStack.fromBukkitCopy(inv.getChestplate())));
        list.add(Pair.of(EquipmentSlot.HEAD, net.minecraft.world.item.ItemStack.fromBukkitCopy(inv.getHelmet())));
        connection.send(new ClientboundSetEquipmentPacket(this.getId(), list));
    }

    public void move(Location location) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) toPlayer).getHandle().connection;
        connection.send(new ClientboundMoveEntityPacket.PosRot(this.getId(), getDelta(location.getX(), this.getX()), getDelta(location.getY(), this.getY()), getDelta(location.getZ(), this.getZ()), getAngle(location.getYaw()), getAngle(location.getPitch()), true));
        connection.send(new ClientboundRotateHeadPacket(this, getAngle(location.getYaw())));
        setPos(location.getX(), location.getY(), location.getZ());
    }

    public void glide(boolean gliding) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) toPlayer).getHandle().connection;
        if (gliding) setPose(Pose.FALL_FLYING);
        else {
            if (fromPlayer.isSneaking()) setPose(Pose.CROUCHING);
            else setPose(Pose.STANDING);
        }
        connection.send(new ClientboundSetEntityDataPacket(this.getId(), this.entityData, true));
    }

    public void teleport(Location location) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) toPlayer).getHandle().connection;
        setPos(location.getX(), location.getY(), location.getZ());
        connection.send(new ClientboundTeleportEntityPacket(this));
        connection.send(new ClientboundRotateHeadPacket(this, getAngle(location.getYaw())));
    }

    public void swing() {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) toPlayer).getHandle().connection;
        connection.send(new ClientboundAnimatePacket(this, 0));
    }

    public void sneak() {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) toPlayer).getHandle().connection;
        if (getPose().equals(Pose.CROUCHING)) setPose(Pose.STANDING);
        else setPose(Pose.CROUCHING);
        connection.send(new ClientboundSetEntityDataPacket(this.getId(), this.entityData, true));
    }

    public void hold(org.bukkit.inventory.ItemStack itemStack, boolean offHand) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) toPlayer).getHandle().connection;
        connection.send(new ClientboundSetEquipmentPacket(this.getId(), List.of(Pair.of(offHand ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND, net.minecraft.world.item.ItemStack.fromBukkitCopy(itemStack)))));
    }

    public void armor(org.bukkit.inventory.ItemStack itemStack, PlayerArmorChangeEvent.SlotType slotType) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) toPlayer).getHandle().connection;
        EquipmentSlot slot = switch (slotType) {
            case FEET -> EquipmentSlot.FEET;
            case LEGS -> EquipmentSlot.LEGS;
            case CHEST -> EquipmentSlot.CHEST;
            case HEAD -> EquipmentSlot.HEAD;
        };
        connection.send(new ClientboundSetEquipmentPacket(this.getId(), List.of(Pair.of(slot, net.minecraft.world.item.ItemStack.fromBukkitCopy(itemStack)))));
    }

    public void remove() {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) toPlayer).getHandle().connection;
        connection.send(new ClientboundRemoveEntitiesPacket(this.getId()));
    }

    private static GameProfile getProfile(Player fromPlayer) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), fromPlayer.getName());
        GameProfile player = ((CraftPlayer) fromPlayer).getHandle().getGameProfile();
        Property property = player.getProperties().get("textures").iterator().next();
        profile.getProperties().put("textures", new Property("textures", property.getValue(), property.getSignature()));
        return profile;
    }

    private short getDelta(double newPos, double oldPos) {
        return (short) ((newPos*32 - oldPos*32)*128);
    }

    private byte getAngle(float angle) {
        return (byte) (angle*256/360);
    }
}
