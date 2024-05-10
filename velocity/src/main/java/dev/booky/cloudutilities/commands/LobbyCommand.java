package dev.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (02:21 08.05.22)

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.booky.cloudutilities.util.Utilities;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static dev.booky.cloudutilities.util.ArgumentUtil.getPlayer;
import static dev.booky.cloudutilities.util.ArgumentUtil.suggestPlayer;
import static dev.booky.cloudutilities.util.Utilities.argument;
import static dev.booky.cloudutilities.util.Utilities.literal;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class LobbyCommand {

    public static BrigadierCommand create(ProxyServer server) {
        return new BrigadierCommand(literal("lobby")
                .then(argument("target", word())
                        .suggests(suggestPlayer(server, "target"))
                        .requires(source -> source.hasPermission("cu.command.lobby.other"))
                        .executes(context -> execute(server, context.getSource(), getPlayer(server, context, "target"))))
                .requires(source -> source instanceof Player && source.hasPermission("cu.command.lobby"))
                .executes(context -> execute(server, context.getSource(), (Player) context.getSource())));
    }

    private static int execute(ProxyServer server, CommandSource source, Player target) {
        RegisteredServer lobby = server.getServer("lobby")
                .orElseGet(() -> server.getServer("hub").orElse(null));
        if (lobby == null) {
            source.sendMessage(Utilities.PREFIX.append(text("No lobby server found", RED)));
            return 1;
        }

        if (lobby.getServerInfo().equals(target.getCurrentServer().map(ServerConnection::getServerInfo).orElse(null))) {
            source.sendMessage(Utilities.PREFIX.append(text("You are already in the lobby", RED)));
            return 1;
        }

        target.createConnectionRequest(lobby).fireAndForget();
        source.sendMessage(Utilities.PREFIX.append(text("Sent " + target.getUsername() + " to lobby", GREEN)));
        return 1;
    }
}
