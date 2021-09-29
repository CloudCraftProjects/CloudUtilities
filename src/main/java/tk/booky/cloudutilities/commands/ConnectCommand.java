package tk.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (14:21 18.07.21)

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import tk.booky.cloudutilities.utils.Constants;
import tk.booky.cloudutilities.utils.PlayerArgumentParser;

import java.net.InetSocketAddress;

import static tk.booky.cloudutilities.utils.Constants.argument;
import static tk.booky.cloudutilities.utils.Constants.literal;

public class ConnectCommand {

    public static BrigadierCommand create(ProxyServer server) {
        return new BrigadierCommand(literal("connect")
            .requires(source -> source.hasPermission("cu.command.connect"))
            .then(argument("host", StringArgumentType.word())
                .then(argument("port", IntegerArgumentType.integer(1, 65535))
                    .requires(source -> source instanceof Player && source.hasPermission("cu.command.connect"))
                    .executes(context -> execute(server, context.getSource(), (Player) context.getSource(), StringArgumentType.getString(context, "host"), IntegerArgumentType.getInteger(context, "port")))
                    .then(argument("target", StringArgumentType.word())
                        .requires(source -> source.hasPermission("cu.command.connect"))
                        .executes(context -> execute(server, context.getSource(), PlayerArgumentParser.getPlayer(context, "target"), StringArgumentType.getString(context, "host"), IntegerArgumentType.getInteger(context, "port")))))));
    }

    private static int execute(ProxyServer server, CommandSource sender, Player target, String host, int port) throws CommandSyntaxException {
        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            RegisteredServer registered = server.createRawRegisteredServer(new ServerInfo(Integer.toString(address.hashCode()), address));

            sender.sendMessage(
                Constants.PREFIX
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
