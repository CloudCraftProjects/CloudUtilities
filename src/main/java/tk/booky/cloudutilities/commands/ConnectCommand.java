package tk.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (14:21 18.07.21)

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import tk.booky.cloudutilities.arguments.PlayerArgumentType;
import tk.booky.cloudutilities.utils.Constants;

import java.net.InetSocketAddress;

public class ConnectCommand {

    public static BrigadierCommand create(ProxyServer server) {
        return new BrigadierCommand(
            LiteralArgumentBuilder.<CommandSource>
                literal("connect")
                .then(RequiredArgumentBuilder.<CommandSource, String>
                    argument("host", StringArgumentType.word())
                    .then(RequiredArgumentBuilder.<CommandSource, Integer>
                        argument("port", IntegerArgumentType.integer(1, 65535))
                        .requires(source -> source instanceof Player && source.hasPermission("cu.command.connect"))
                        .executes(context -> execute(server, context.getSource(), (Player) context.getSource(), StringArgumentType.getString(context, "host"), IntegerArgumentType.getInteger(context, "port")))
                        .then(RequiredArgumentBuilder.<CommandSource, String>
                            argument("target", PlayerArgumentType.player())
                            .requires(source -> source.hasPermission("cu.command.connect"))
                            .executes(context -> execute(server, context.getSource(), PlayerArgumentType.getPlayer(context, "target"), StringArgumentType.getString(context, "host"), IntegerArgumentType.getInteger(context, "port")))
                        )
                    )
                )
        );
    }

    private static int execute(ProxyServer server, CommandSource sender, Player target, String host, int port) throws CommandSyntaxException {
        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            RegisteredServer registered = server.createRawRegisteredServer(new ServerInfo(Integer.toString(address.hashCode()), address));

            sender.sendMessage(
                Constants.PREFIX
                    .append(Component.space())
                    .append(Component.text(target.getUsername(), NamedTextColor.WHITE))
                    .append(Component.text(" will be sent to ", NamedTextColor.GREEN))
                    .append(Component.text(address.toString(), NamedTextColor.WHITE))
                    .append(Component.text('.', NamedTextColor.GREEN))
            );

            target.createConnectionRequest(registered).fireAndForget();
            return 1;
        } catch (Throwable throwable) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        }
    }
}
