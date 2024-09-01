package net.dungeonescape.command.commands;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.Message;
import net.dungeonescape.command.DungeonEscapeSubcommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class StartCommand implements DungeonEscapeSubcommand {
    @Override
    public boolean execute(DungeonEscapeMain plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 3) {
            Message.HELP_START.send(sender);
            return false;
        }

        Player player1 = Bukkit.getPlayer(args[1]);
        if (player1 == null) {
            Message.START_INVALID_PLAYER.send(sender, args[1]);
            return false;
        }
        if (plugin.getGameManager().getGame(player1) != null) {
            Message.START_ALREADY_IN_GAME.send(sender, args[1]);
            return false;
        }

        Player player2 = Bukkit.getPlayer(args[2]);
        if (player2 == null) {
            Message.START_INVALID_PLAYER.send(sender, args[2]);
            return false;
        }
        if (plugin.getGameManager().getGame(player2) != null) {
            Message.START_ALREADY_IN_GAME.send(sender, args[2]);
            return false;
        }

        if (player1.equals(player2)) {
            Message.START_SAME_PLAYER.send(sender);
            return false;
        }

        plugin.getGameManager().create(player1, player2);
        Message.START_SUCCESS.send(sender);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(DungeonEscapeMain plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        }
        if (args.length == 3) {
            return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase())).toList();
        }
        return Collections.emptyList();
    }
}
