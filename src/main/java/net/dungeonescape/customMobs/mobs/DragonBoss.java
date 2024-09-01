package net.dungeonescape.customMobs.mobs;

import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.common.ReflectionHandler;
import net.dungeonescape.dungeon.DungeonRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.Node;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

import java.util.Map;

public class DragonBoss extends EnderDragon {
    private final DungeonRoom room;
    private final DungeonPlayer player;

    public DragonBoss(Location location, DungeonRoom room, DungeonPlayer player) {
        super(EntityType.ENDER_DRAGON, ((CraftWorld) location.getWorld()).getHandle());
        this.room = room;
        this.player = player;
        setPodium(new BlockPos(location.getX(), location.getY(), location.getZ()));

        Map<Heightmap.Types, Heightmap> maps = level.getChunk(SectionPos.blockToSectionCoord(location.getX()), SectionPos.blockToSectionCoord(location.getZ())).heightmaps;
        int chunkX = location.getBlockX() & 15;
        int chunkZ = location.getBlockZ() & 15;

        ReflectionHandler.invoke(Heightmap.class, maps.get(Heightmap.Types.MOTION_BLOCKING), "a", new Class[]{int.class, int.class, int.class}, chunkX, chunkZ, 200);
        ReflectionHandler.invoke(Heightmap.class, maps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES), "a", new Class[]{int.class, int.class, int.class}, chunkX, chunkZ, 200);

        Node[] nodes = ReflectionHandler.getField(this.getClass().getSuperclass(), this, "cv");
        int[] nodeAdjacency = ReflectionHandler.getField(this.getClass().getSuperclass(), this, "cw");

        for (int i = 0; i < 24; ++i) {
            int j = 2;
            int k;
            int l;

            if (i < 12) {
                float value = 2.0F * (-3.1415927F + 0.2617994F * (float) i);
                k = Mth.floor(2.0F * Mth.cos(value));
                l = Mth.floor(2.0F * Mth.sin(value));
            } else if (i < 20) {
                float value = 2.0F * (-3.1415927F + 0.3926991F * (float) (i - 12));
                k = Mth.floor(Mth.cos(value));
                l = Mth.floor(Mth.sin(value));
                j += 3;
            } else {
                float value = 2.0F * (-3.1415927F + 0.7853982F * (float) (i - 20));
                k = Mth.floor(Mth.cos(value));
                l = Mth.floor(Mth.sin(value));
            }

            nodes[i] = new Node(k + getPodium().getX(), j + 200, l + getPodium().getZ());
        }

        int[] values = new int[]{
                6146, 8197, 8202, 16404, 32808, 32848, 65696, 131392, 131712, 263424, 526848, 525313, 1581057, 3166214,
                2138120, 6373424, 4358208, 12910976, 9044480, 9706496, 15216640, 13688832, 11763712, 8257536
        };

        for (int i = 0; i < 24; i++) {
            nodeAdjacency[i] = values[i];
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return player.getDungeonRoom() == room && super.canAttack(target);
    }
}
