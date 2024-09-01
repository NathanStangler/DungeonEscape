package net.dungeonescape.customMobs;

import io.papermc.paper.adventure.PaperAdventure;
import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.customMobs.goals.*;
import net.dungeonescape.customMobs.mobs.DragonBoss;
import net.dungeonescape.customMobs.mobs.WardenBoss;
import net.dungeonescape.dungeon.DungeonRoom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.stream.Collectors;

public class CustomMobManager {
    private final DungeonEscapeMain plugin;
    private final Map<Entity, UUID> entities = new HashMap<>();

    public CustomMobManager(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    public Zombie zombie(DungeonPlayer player, Location location) {
        Zombie zombie = init(player, EntityType.ZOMBIE, location);
        Color color = Color.fromRGB(197, 209, 217);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(color);
        boots.setItemMeta(bootsMeta);
        ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta legsMeta = (LeatherArmorMeta) boots.getItemMeta();
        legsMeta.setColor(color);
        legs.setItemMeta(legsMeta);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) boots.getItemMeta();
        chestMeta.setColor(color);
        chest.setItemMeta(chestMeta);
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) boots.getItemMeta();
        helmetMeta.setColor(color);
        helmet.setItemMeta(helmetMeta);
        zombie.getBukkitLivingEntity().getEquipment().setBoots(boots);
        zombie.getBukkitLivingEntity().getEquipment().setLeggings(legs);
        zombie.getBukkitLivingEntity().getEquipment().setChestplate(chest);
        zombie.getBukkitLivingEntity().getEquipment().setHelmet(helmet);
        zombie.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(zombie, Player.class, true));
        zombie.goalSelector.addGoal(0, new TornadoPullGoal(plugin, zombie));
        zombie.goalSelector.addGoal(1, new ZombieAttackGoal(zombie, 1.0D, false));
        zombie.goalSelector.addGoal(2, new CustomStrollToSpawn(zombie, location));
        return zombie;
    }

    public Stray stray(DungeonPlayer player, Location location) {
        Stray stray = init(player, EntityType.STRAY, location);
        stray.getBukkitLivingEntity().getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
        stray.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(stray, Player.class, true));
        stray.goalSelector.addGoal(0, new CustomStrollToSpawn(stray, location));
        return stray;
    }

    public Vindicator vindicator(DungeonPlayer player, Location location) {
        Vindicator vindicator = init(player, EntityType.VINDICATOR, location);
        vindicator.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(vindicator, Player.class, true));
        vindicator.goalSelector.addGoal(0, new MeleeAttackGoal(vindicator, 1.0D, true));
        vindicator.goalSelector.addGoal(1, new SummonGoal(plugin, vindicator, player));
        vindicator.goalSelector.addGoal(2, new CustomStrollToSpawn(vindicator, location));
        return vindicator;
    }

    public CaveSpider caveSpider(DungeonPlayer player, Location location) {
        CaveSpider caveSpider = init(player, EntityType.CAVE_SPIDER, location);
        caveSpider.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(caveSpider, Player.class, true));
        caveSpider.goalSelector.addGoal(0, new LeapAtTargetGoal(caveSpider, 0.4F));
        caveSpider.goalSelector.addGoal(1, new MeleeAttackGoal(caveSpider, 1.0D, true));
        caveSpider.goalSelector.addGoal(2, new CustomStrollToSpawn(caveSpider, location));
        return caveSpider;
    }

    public Strider strider(DungeonPlayer player, Location location) {
        Strider strider = init(player, EntityType.STRIDER, location);
        strider.getAttributes().registerAttribute(Attributes.ATTACK_DAMAGE);
        strider.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        strider.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(strider, Player.class, true));
        strider.goalSelector.addGoal(0, new ShootFireGoal(plugin, strider));
        strider.goalSelector.addGoal(1, new MeleeAttackGoal(strider, 1.0D, false));
        strider.goalSelector.addGoal(2, new CustomStrollToSpawn(strider, location));
        return strider;
    }

    public Evoker evoker(DungeonPlayer player, Location location) {
        Evoker evoker = init(player, EntityType.EVOKER, location);
        evoker.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(evoker, Player.class, true));
        evoker.goalSelector.addGoal(0, new EarthquakeGoal(plugin, evoker, player));
        evoker.goalSelector.addGoal(1, new CustomStrollToSpawn(evoker, location));
        return evoker;
    }

    public Silverfish silverfish(DungeonPlayer player, Location location) {
        Silverfish silverfish = init(player, EntityType.SILVERFISH, location);
        silverfish.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(silverfish, Player.class, true));
        silverfish.goalSelector.addGoal(0, new MeleeAttackGoal(silverfish, 1.0D, false));
        silverfish.goalSelector.addGoal(1, new CustomStrollToSpawn(silverfish, location));
        return silverfish;
    }

    public EnderMan enderman(DungeonPlayer player, Location location) {
        EnderMan enderman = init(player, EntityType.ENDERMAN, location);
        enderman.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(enderman, Player.class, true));
        enderman.goalSelector.addGoal(0, new MeleeAttackGoal(enderman, 1.0D, false));
        enderman.goalSelector.addGoal(1, new CustomStrollToSpawn(enderman, location));
        return enderman;
    }

    public EndCrystal crystal(DungeonPlayer player, Location location) {
        EndCrystal crystal = spawn(player, EntityType.ENDER_CRYSTAL, location);
        crystal.setShowBottom(false);
        crystal.setBeamTarget(null);
        return crystal;
    }

    public WardenBoss warden(DungeonPlayer player, Location location, DungeonRoom room) {
        WardenBoss warden = new WardenBoss(location, room, player);
        ((CraftWorld) location.getWorld()).getHandle().addFreshEntity(warden);
        entities.put(warden.getBukkitEntity(), player.getPlayer().getUniqueId());
        warden.getBukkitEntity().setMetadata("spawnRoom", new FixedMetadataValue(plugin, player.getDungeonRoom(location)));
        warden.setPos(location.getX(), location.getY(), location.getZ());
        warden.setPersistenceRequired(true);
        warden.setCustomNameVisible(true);
        warden.setCustomName(PaperAdventure.asVanilla(getHealthBar(warden.getHealth(), warden.getMaxHealth())));

        warden.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0D);
        warden.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        return warden;
    }

    public DragonBoss dragon(DungeonPlayer player, Location location, DungeonRoom room) {
        DragonBoss dragon = new DragonBoss(location, room, player);
        ((CraftWorld) location.getWorld()).getHandle().addFreshEntity(dragon);
        entities.put(dragon.getBukkitEntity(), player.getPlayer().getUniqueId());
        dragon.getBukkitEntity().setMetadata("spawnRoom", new FixedMetadataValue(plugin, player.getDungeonRoom(location)));
        dragon.setPos(location.getX(), location.getY(), location.getZ());
        dragon.setPersistenceRequired(true);
        dragon.setCustomNameVisible(true);
        dragon.setCustomName(PaperAdventure.asVanilla(getHealthBar(dragon.getHealth(), dragon.getMaxHealth())));

        dragon.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0D);
        return dragon;
    }

    private <T> T spawn(DungeonPlayer player, EntityType type, Location location) {
        Entity e = location.getWorld().spawnEntity(location, type);
        e.setMetadata("spawnRoom", new FixedMetadataValue(plugin, player.getDungeonRoom(location)));
        net.minecraft.world.entity.Entity entity = ((CraftEntity) e).getHandle();
        entity.setPos(location.getX(), location.getY(), location.getZ());
        entities.put(e, player.getPlayer().getUniqueId());
        return (T) entity;
    }

    private <T> T init(DungeonPlayer player, EntityType type, Location location) {
        Mob mob = spawn(player, type, location);
        AttributeInstance attribute = mob.getAttribute(Attributes.FOLLOW_RANGE);
        if (attribute != null) {
            attribute.setBaseValue(10.0D);
        }
        mob.targetSelector.removeAllGoals();
        mob.goalSelector.removeAllGoals();
        mob.setAggressive(false);
        mob.setPersistenceRequired(true);
        mob.setCustomNameVisible(true);
        mob.setCustomName(PaperAdventure.asVanilla(getHealthBar(mob.getHealth(), mob.getMaxHealth())));
        return (T) mob;
    }

    public UUID removeEntity(Entity entity) {
        return entities.remove(entity);
    }

    public Set<Entity> getEntities(UUID player) {
        return entities.entrySet().stream().filter(entry -> entry.getValue().equals(player)).map(Map.Entry::getKey).collect(Collectors.toUnmodifiableSet());
    }

    public Set<Entity> getEntities() {
        return Collections.unmodifiableSet(entities.keySet());
    }

    public Component getHealthBar(float health, float maxHealth) {
        int dashes = 15;
        int hasDashes = Math.round((health / maxHealth) * dashes);
        int lostDashes = dashes - hasDashes;

        Component hasFormatted = Component.text(" ".repeat(Math.max(0, hasDashes))).color(NamedTextColor.RED);
        Component lostFormatted = Component.text(" ".repeat(Math.max(0, lostDashes))).color(NamedTextColor.GRAY);
        return hasFormatted.append(lostFormatted).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE).decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.TRUE);
    }
}
