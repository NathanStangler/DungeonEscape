package net.dungeonescape;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public interface Message {
    Component PREFIX = text().append(text("DungeonEscape: ").color(AQUA).decorate(TextDecoration.BOLD)).build();

    Message2<String, String> HELP_BUILDER = (usage, info) -> text()
            .append(text("/dungeonescape ").append(text(usage)))
            .append(text(" - ").color(DARK_GRAY))
            .append(text(info).color(GRAY))
            .clickEvent(ClickEvent.suggestCommand("/dungeonescape " + usage))
            .hoverEvent(HoverEvent.showText(text(info).color(GRAY)))
            .build();

    Message0 HELP_HELP = () -> HELP_BUILDER.build("help", "Lists all commands");
    Message0 HELP_START = () -> HELP_BUILDER.build("start <player1> <player2>", "Starts the dungeon escape event");
    Message0 HELP_STOP = () -> HELP_BUILDER.build("stop <game>", "Stops the dungeon escape event");

    Message0 HELP = () -> text().append(text("DungeonEscape Help:").color(BLUE)).append(Component.newline())
            .append(HELP_HELP.build()).append(Component.newline())
            .append(HELP_START.build()).append(Component.newline())
            .append(HELP_STOP.build()).build();

    Message1<String> START_INVALID_PLAYER = name -> text("Player ").append(text(name).color(AQUA)).append(text(" was not found")).color(RED);
    Message1<String> START_ALREADY_IN_GAME = name -> text("Player ").append(text(name).color(AQUA)).append(text(" is already in a game")).color(RED);
    Message0 START_SAME_PLAYER = () -> text("Unable to start game with the same player").color(RED);
    Message0 START_SUCCESS = () -> text("Started the dungeon escape event").color(RED);
    Message0 STOP_DOES_NOT_EXIST = () -> text("Game does not exist").color(RED);
    Message0 STOP_SUCCESS = () -> text("Stopped the dungeon escape event").color(RED);

    Message0 DOOR_NO_KEY = () -> text("This door requires a key.").color(RED);
    Message0 FINAL_DOOR_NOT_LAST = () -> text("This door cannot be accessed until all other rooms are unlocked.").color(RED);
    Message1<String> DUNGEON_COMPLETED = name -> text(name).append(text(" has completed the dungeon!")).color(BLUE);
    Message0 NOT_ON_GROUND_LEAP = () -> text("You must be on the ground to leap.").color(RED);

    interface Message0 extends Message {
        Component build();
        default void send(Audience audience) {
            audience.sendMessage(PREFIX.append(build()));
        }
    }

    interface Message1<a> extends Message {
        Component build(a a);
        default void send(Audience audience, a a) {
            audience.sendMessage(PREFIX.append(build(a)));
        }
    }

    interface Message2<a, b> extends Message {
        Component build(a a, b b);
        default void send(Audience audience, a a, b b) {
            audience.sendMessage(PREFIX.append(build(a, b)));
        }
    }
}
