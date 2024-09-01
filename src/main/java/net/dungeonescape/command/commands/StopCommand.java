package net.dungeonescape.command.commands;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.Message;
import net.dungeonescape.command.DungeonEscapeSubcommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class StopCommand implements DungeonEscapeSubcommand {
    @Override
    public boolean execute(DungeonEscapeMain plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            Message.HELP_STOP.send(sender);
            return false;
        }

        if (!plugin.getGameManager().end(args[1])) {
            Message.STOP_DOES_NOT_EXIST.send(sender);
            return false;
        }

        Message.STOP_SUCCESS.send(sender);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(DungeonEscapeMain plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 2) {
            return plugin.getGameManager().getGames().stream().toList();
        }
        return Collections.emptyList();
    }
}