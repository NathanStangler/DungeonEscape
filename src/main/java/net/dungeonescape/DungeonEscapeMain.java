package net.dungeonescape;

import net.dungeonescape.command.DungeonEscapeCommand;
import net.dungeonescape.common.BossBarManager;
import net.dungeonescape.common.HeightManager;
import net.dungeonescape.common.blinkingblock.BlinkingBlockManager;
import net.dungeonescape.common.flame.FlameManager;
import net.dungeonescape.common.groundshake.GroundShakeManager;
import net.dungeonescape.common.key.KeyManager;
import net.dungeonescape.common.laser.LaserManager;
import net.dungeonescape.common.movingblock.MovingBlockManager;
import net.dungeonescape.common.tornado.TornadoManager;
import net.dungeonescape.customMobs.CustomMobManager;
import net.dungeonescape.dungeon.RoomManager;
import net.dungeonescape.listener.DungeonListener;
import net.dungeonescape.listener.RoomListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class DungeonEscapeMain extends JavaPlugin {
    private KeyManager keyManager;
    private GroundShakeManager groundShakeManager;
    private TornadoManager tornadoManager;
    private LaserManager laserManager;
    private HeightManager heightManager;
    private BlinkingBlockManager blinkingBlockManager;
    private MovingBlockManager movingBlockManager;
    private FlameManager flameManager;
    private BossBarManager bossBarManager;
    private CustomMobManager customMobManager;
    private RoomManager roomManager;
    private DungeonGameManager gameManager;

    @Override
    public void onEnable() {
        loadWorld();
        loadImages();

        this.keyManager = new KeyManager(this);
        keyManager.start();
        this.groundShakeManager = new GroundShakeManager(this);
        groundShakeManager.start();
        this.tornadoManager = new TornadoManager(this);
        tornadoManager.start();
        this.laserManager = new LaserManager(this);
        laserManager.start();
        this.heightManager = new HeightManager();
        this.blinkingBlockManager = new BlinkingBlockManager(this);
        blinkingBlockManager.start();
        this.movingBlockManager = new MovingBlockManager(this);
        movingBlockManager.start();
        this.flameManager = new FlameManager(this);
        flameManager.start();
        this.bossBarManager = new BossBarManager(this);
        bossBarManager.start();
        this.customMobManager = new CustomMobManager(this);
        this.roomManager = new RoomManager(this);
        this.gameManager = new DungeonGameManager(this);

        getServer().getCommandMap().register("dungeonescape", new DungeonEscapeCommand(this));
        getServer().getPluginManager().registerEvents(new DungeonListener(this), this);
        getServer().getPluginManager().registerEvents(new RoomListener(this), this);
    }

    @Override
    public void onDisable() {
        keyManager.close();
        groundShakeManager.close();
        tornadoManager.close();
        laserManager.close();
        blinkingBlockManager.close();
        movingBlockManager.close();
        flameManager.close();
        bossBarManager.close();
        gameManager.close();
    }

    private void loadWorld() {
        List<String> files = List.of("level.dat", "region/r.0.0.mca", "region/r.0.1.mca", "region/r.0.-1.mca", "region/r.-1.0.mca", "region/r.-1.1.mca", "region/r.-1.-1.mca", "region/r.-2.0.mca", "region/r.-2.-1.mca");
        File worldFolder = new File(getDataFolder(), "world");
        if (!worldFolder.exists()) worldFolder.mkdir();

        for (String file : files) {
            if (!(new File(worldFolder, file)).exists() && getResource("world/" + file) != null) {
                saveResource("world/" + file, true);
            }
        }
    }

    private void loadImages() {
        List<String> files = List.of("checkmark.png", "background.png", "block.png");
        File imageFolder = new File(getDataFolder(), "images");
        if (!imageFolder.exists()) imageFolder.mkdir();

        for (String file : files) {
            if (!(new File(imageFolder, file)).exists() && getResource("images/" + file) != null) {
                saveResource("images/" + file, true);
            }
        }
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public GroundShakeManager getGroundShakeManager() {
        return groundShakeManager;
    }

    public TornadoManager getTornadoManager() {
        return tornadoManager;
    }

    public LaserManager getLaserManager() {
        return laserManager;
    }

    public HeightManager getHeightManager() {
        return heightManager;
    }

    public BlinkingBlockManager getBlinkingBlockManager() {
        return blinkingBlockManager;
    }

    public MovingBlockManager getMovingBlockManager() {
        return movingBlockManager;
    }

    public FlameManager getFlameManager() {
        return flameManager;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public CustomMobManager getCustomMobManager() {
        return customMobManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public DungeonGameManager getGameManager() {
        return gameManager;
    }
}
