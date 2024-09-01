package net.dungeonescape.common.groundshake;

import net.dungeonescape.DungeonEscapeMain;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GroundShakeManager {
    private static final short UP = 219;
    private static final short DOWN = -328;
    private final DungeonEscapeMain plugin;
    private final Set<GroundShake> grounds = ConcurrentHashMap.newKeySet();
    private int task = -1;

    public GroundShakeManager(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    public void run(Location spawnLocation, Player player) {
        BlockState blockState = spawnLocation.getBlock().getState();
        ServerLevel world = ((CraftWorld) spawnLocation.getWorld()).getHandle();
        FallingBlockEntity fallingBlock = new FallingBlockEntity(world, spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), ((CraftBlockState) blockState).getHandle());
        fallingBlock.fallDistance = -1000000;
        fallingBlock.autoExpire = false;
        fallingBlock.setInvulnerable(true);
        fallingBlock.setNoGravity(true);
        ArmorStand armorStand = new ArmorStand(world, spawnLocation.getX(), spawnLocation.getY()-1, spawnLocation.getZ());
        armorStand.setNoGravity(true);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        fallingBlock.startRiding(armorStand);
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        connection.send(fallingBlock.getAddEntityPacket());
        connection.send(armorStand.getAddEntityPacket());
        connection.send(new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData(), true));
        connection.send(new ClientboundSetPassengersPacket(armorStand));

        grounds.add(new GroundShake(player, spawnLocation, armorStand.getId(), fallingBlock.getId()));
    }

    public void remove(GroundShake ground) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) ground.getPlayer()).getHandle().connection;
        connection.send(new ClientboundRemoveEntitiesPacket(ground.getStand(), ground.getBlock()));
        grounds.remove(ground);
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> grounds.forEach(ground -> {
            ServerGamePacketListenerImpl connection = ((CraftPlayer) ground.getPlayer()).getHandle().connection;
            connection.send(new ClientboundMoveEntityPacket.Pos(ground.getStand(), (short) 0, ground.getDirection() ? DOWN : UP, (short) 0, true));

            ground.incrementTicks();
            if (ground.getTicks() == 15) {
                ground.switchDirection();
                ground.getLocation().getWorld().playSound(ground.getLocation(), ground.getLocation().getBlock().getBlockData().getSoundGroup().getBreakSound(), 1, 1);
                ground.getLocation().getWorld().getNearbyLivingEntities(ground.getLocation(), 1.5).stream().filter(livingEntity -> livingEntity.isValid() && livingEntity.getType().equals(EntityType.PLAYER)).forEach(livingEntity -> livingEntity.damage(6));
            }

            if (ground.getTicks() == 25) {
                remove(ground);
            }
        }), 0L, 1L).getTaskId();
    }

    public void close() {
        Bukkit.getScheduler().cancelTask(task);
        task = -1;
        grounds.forEach(this::remove);
    }
}
