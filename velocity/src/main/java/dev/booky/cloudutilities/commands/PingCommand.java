package dev.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (14:21 18.07.21)

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.booky.cloudutilities.util.ArgumentUtil;
import dev.booky.cloudutilities.util.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static dev.booky.cloudutilities.util.Utilities.argument;
import static dev.booky.cloudutilities.util.Utilities.literal;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_RED;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

public class PingCommand {

    public static BrigadierCommand create(ProxyServer server) {
        return new BrigadierCommand(literal("ping")
                .then(argument("target", word())
                        .requires(source -> source.hasPermission("cu.command.ping.other"))
                        .suggests(ArgumentUtil.suggestPlayer(server, "target"))
                        .executes(context -> execute(context.getSource(), ArgumentUtil.getPlayer(server, context, "target"))))
                .requires(source -> source instanceof Player && source.hasPermission("cu.command.ping"))
                .executes(context -> execute(context.getSource(), (Player) context.getSource())));
    }

    private static int execute(CommandSource sender, Player target) throws CommandSyntaxException {
        if (target == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }

        sender.sendMessage(Component.text()
                .color(GREEN).append(Utilities.PREFIX)
                .append(Component.text("Player "))
                .append(Component.text(target.getUsername(), WHITE))
                .append(Component.text(" has a ping of "))
                .append(Component.text(target.getPing(), getPingColor(target.getPing())))
                .append(Component.text("ms")));
        return 1;
    }

    private static TextColor getPingColor(long ping) {
        if (ping < 50) return GREEN;
        if (ping < 100) return YELLOW;
        if (ping < 150) return GOLD;
        if (ping < 200) return RED;
        return DARK_RED;
    }
}
