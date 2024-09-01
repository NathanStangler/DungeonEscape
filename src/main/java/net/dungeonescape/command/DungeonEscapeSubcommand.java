package net.dungeonescape.command;

import net.dungeonescape.DungeonEscapeMain;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DungeonEscapeSubcommand {
    boolean execute(DungeonEscapeMain plugin, @NotNull CommandSender sender, @NotNull String[] args);

    @NotNull List<String> tabComplete(DungeonEscapeMain plugin, @NotNull CommandSender sender, @NotNull String[] args);
}
