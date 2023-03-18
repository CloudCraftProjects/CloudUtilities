package dev.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (14:21 18.07.21)

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.booky.cloudutilities.util.Utilities;

import java.net.InetSocketAddress;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static dev.booky.cloudutilities.util.ArgumentUtil.getPlayer;
import static dev.booky.cloudutilities.util.ArgumentUtil.suggestPlayer;
import static dev.booky.cloudutilities.util.Utilities.argument;
import static dev.booky.cloudutilities.util.Utilities.literal;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

public class ConnectCommand {

    public static BrigadierCommand create(ProxyServer server) {
        return new BrigadierCommand(literal("connect")
                .requires(source -> source.hasPermission("cu.command.connect"))
                .then(argument("host", string())
                        .then(argument("port", integer(1, 65535))
                                .then(argument("target", word())
                                        .suggests(suggestPlayer(server, "target"))
                                        .requires(source -> source.hasPermission("cu.command.connect.other"))
                                        .executes(context -> execute(server, context.getSource(),
                                                getPlayer(server, context, "target"),
                                                getString(context, "host"),
                                                getInteger(context, "port"))))
                                .requires(source -> source instanceof Player && source.hasPermission("cu.command.connect"))
                                .executes(context -> execute(server, context.getSource(), (Player) context.getSource(),
                                        getString(context, "host"),
                                        getInteger(context, "port"))))));
    }

    private static int execute(ProxyServer server, CommandSource sender,
                               Player target, String host, int port) throws CommandSyntaxException {
        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            RegisteredServer registered = null;

            for (RegisteredServer serverInfo : server.getAllServers()) {
                if (serverInfo.getServerInfo().getAddress().equals(address)) {
                    registered = serverInfo;
                    break;
                }
            }

            if (registered == null) {
                registered = server.createRawRegisteredServer(
                        new ServerInfo(Integer.toString(address.hashCode()), address));
            }

            sender.sendMessage(Utilities.PREFIX
                    .append(text(target.getUsername(), WHITE))
                    .append(text(" will be sent to ", GREEN))
                    .append(text(address.toString(), WHITE)));

            target.createConnectionRequest(registered).fireAndForget();
            return 1;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }
    }
}
