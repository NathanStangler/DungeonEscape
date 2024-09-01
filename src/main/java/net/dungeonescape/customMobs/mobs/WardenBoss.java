package net.dungeonescape.customMobs.mobs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.common.ReflectionHandler;
import net.dungeonescape.customMobs.goals.WardenBossRandomStroll;
import net.dungeonescape.dungeon.DungeonRoom;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.warden.SetRoarTarget;
import net.minecraft.world.entity.ai.behavior.warden.TryToSniff;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.schedule.Activity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

import javax.annotation.Nullable;
import java.util.List;

public class WardenBoss extends Warden {
    private static final List<SensorType<? extends Sensor<? super Warden>>> SENSOR_TYPES = List.of(SensorType.NEAREST_PLAYERS, SensorType.WARDEN_ENTITY_SENSOR);
    private static final List<MemoryModuleType<?>> MEMORY_TYPES = List.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.ROAR_TARGET, MemoryModuleType.DISTURBANCE_LOCATION, MemoryModuleType.RECENT_PROJECTILE, MemoryModuleType.IS_SNIFFING, MemoryModuleType.IS_EMERGING, MemoryModuleType.ROAR_SOUND_DELAY, MemoryModuleType.DIG_COOLDOWN, MemoryModuleType.ROAR_SOUND_COOLDOWN, MemoryModuleType.SNIFF_COOLDOWN, MemoryModuleType.TOUCH_COOLDOWN, MemoryModuleType.VIBRATION_COOLDOWN, MemoryModuleType.SONIC_BOOM_COOLDOWN, MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, MemoryModuleType.SONIC_BOOM_SOUND_DELAY);
    private final DungeonRoom room;
    private final DungeonPlayer player;

    public WardenBoss(Location location, DungeonRoom room, DungeonPlayer player) {
        super(EntityType.WARDEN, ((CraftWorld) location.getWorld()).getHandle());
        this.room = room;
        this.player = player;

        NbtOps dynamicopsnbt = NbtOps.INSTANCE;
        Brain.Provider<Warden> provider = Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
        Brain<Warden> brain = provider.makeBrain(new Dynamic<>(dynamicopsnbt, dynamicopsnbt.createMap(ImmutableMap.of(dynamicopsnbt.createString("memories"), dynamicopsnbt.emptyMap()))));
        ReflectionHandler.invoke(WardenAi.class, "a", brain);
        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(new SetRoarTarget<>(Warden::getEntityAngryAt), new TryToSniff(), (Behavior<? super Warden>) new RunOne(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(new WardenBossRandomStroll(0.5F, location), 2), Pair.of(new DoNothing(30, 60), 1)))));
        ReflectionHandler.invoke(WardenAi.class, "g", brain);
        ReflectionHandler.invoke(WardenAi.class, "a", new Class<?>[]{Warden.class, Brain.class}, this, brain);
        ReflectionHandler.invoke(WardenAi.class, "e", brain);
        ReflectionHandler.invoke(WardenAi.class, "f", brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        this.brain = brain;
    }

    @Override
    public boolean canTargetEntity(@Nullable Entity entity) {
        return player.getDungeonRoom() == room && super.canTargetEntity(entity);
    }
}
