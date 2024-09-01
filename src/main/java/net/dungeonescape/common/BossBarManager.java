package net.dungeonescape.common;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.DungeonGame;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManager {
    private final DungeonEscapeMain plugin;
    private final Set<DungeonGame> games = ConcurrentHashMap.newKeySet();
    private int task;

    public BossBarManager(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    public void addGame(DungeonGame game) {
        games.add(game);
        BossBar bar1 = game.getPlayers()[0].getBar();
        BossBar bar2 = game.getPlayers()[1].getBar();

        for (DungeonPlayer player : game.getPlayers()) {
            player.getPlayer().showBossBar(bar1);
            player.getPlayer().showBossBar(bar2);
        }
    }

    public void removeGame(DungeonGame game) {
        if (!games.remove(game)) return;
        BossBar bar1 = game.getPlayers()[0].getBar();
        BossBar bar2 = game.getPlayers()[1].getBar();

        for (DungeonPlayer player : game.getPlayers()) {
            player.getPlayer().hideBossBar(bar1);
            player.getPlayer().hideBossBar(bar2);
        }
    }

    public void start() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> games.forEach(game -> {
            DungeonPlayer player1 = game.getPlayers()[0];
            DungeonPlayer player2 = game.getPlayers()[1];
            player1.getBar().progress(Math.min(Math.max((player1.getRoomsCompleted() - 1) / 11F, 0), 1));
            player2.getBar().progress(Math.min(Math.max((player2.getRoomsCompleted() - 1) / 11F, 0), 1));
        }), 20L, 20L);
    }

    public void close() {
        Bukkit.getScheduler().cancelTask(task);
        games.forEach(this::removeGame);
    }
}
