package net.dungeonescape.command;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.Message;
import net.dungeonescape.command.commands.StartCommand;
import net.dungeonescape.command.commands.StopCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DungeonEscapeCommand extends Command {
    private final DungeonEscapeMain plugin;
    private final Map<String, DungeonEscapeSubcommand> commands = new HashMap<>();

    public DungeonEscapeCommand(DungeonEscapeMain plugin) {
        super("dungeonescape", "Main command for DungeonEscape", "/dungeonescape help", List.of("dungeon"));
        setPermission("dungeonescape.admin");
        this.plugin = plugin;
        register();
    }

    private void register() {
        commands.put("start", new StartCommand());
        commands.put("stop", new StopCommand());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Message.HELP.send(sender);
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "start" -> commands.get("start").execute(plugin, sender, args);
            case "stop" -> commands.get("stop").execute(plugin, sender, args);
            default -> {
                Message.HELP.send(sender);
                yield false;
            }
        };
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return Stream.of("help", "start", "stop").filter(arg -> arg.startsWith(args[0].toLowerCase())).toList();
        }

        String commandName = args[0].toLowerCase();
        if (commandName.equals("help")) {
            return Collections.emptyList();
        }

        DungeonEscapeSubcommand command = commands.get(commandName);
        if (command == null) {
            return Collections.emptyList();
        }
        return command.tabComplete(plugin, sender, args);
    }
}
