package net.dungeonescape;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DungeonGameManager {
    private final DungeonEscapeMain plugin;
    private final Map<String, DungeonGame> games = new HashMap<>();

    public DungeonGameManager(DungeonEscapeMain plugin) {
        this.plugin = plugin;
    }

    public DungeonGame create(Player player1, Player player2) {
        String id = Long.toHexString(System.nanoTime());
        DungeonGame game = new DungeonGame(plugin, id);
        games.put(id, game);
        game.startGame(player1, player2);
        return game;
    }

    public boolean end(String id) {
        DungeonGame game = games.remove(id);
        if (game == null) return false;
        game.resetGame();
        return true;
    }

    public DungeonGame getGame(Player player) {
        String world = player.getWorld().getName();
        if (!world.startsWith("dungeonescape")) return null;
        return games.get(world.substring(14));
    }

    public Set<String> getGames() {
        return games.keySet();
    }

    public void close() {
        new HashSet<>(games.keySet()).forEach(this::end);
    }
}
