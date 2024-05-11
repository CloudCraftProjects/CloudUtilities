package dev.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (14:21 18.07.21)

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import dev.booky.cloudutilities.util.Utilities;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static dev.booky.cloudutilities.util.PlayerArguments.getPlayer;
import static dev.booky.cloudutilities.util.PlayerArguments.playerSuggestions;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;

@Singleton
public class ConnectCommand extends AbstractCommand {

    private static final int DEFAULT_PORT = 25565;

    private final ProxyServer server;

    @Inject
    public ConnectCommand(ProxyServer server) {
        super("connect");
        this.server = server;
    }

    @Override
    public LiteralCommandNode<CommandSource> buildNode() {
        return literal(this.getLabel())
                .requires(source -> source.hasPermission(this.getPermission()))
                .then(argument("host", string())
                        .executes(ctx -> this.connectTo(ctx.getSource(), (Player) ctx.getSource(),
                                getString(ctx, "host"), DEFAULT_PORT))
                        .then(argument("port", integer(0, 65535))
                                .executes(ctx -> this.connectTo(ctx.getSource(), (Player) ctx.getSource(),
                                        getString(ctx, "host"),
                                        getInteger(ctx, "port")))
                                .then(argument("target", word())
                                        .suggests(playerSuggestions(this.server))
                                        .requires(source -> source.hasPermission(this.getPermission("other")))
                                        .executes(ctx -> this.connectTo(ctx.getSource(),
                                                getPlayer(this.server, ctx, "target"),
                                                getString(ctx, "host"),
                                                getInteger(ctx, "port"))))))
                .build();
    }

    public int connectTo(CommandSource source, Player target, String host, int port) {
        InetSocketAddress address = new InetSocketAddress(host, port);
        return this.connectTo(source, target, address);
    }

    public int connectTo(CommandSource source, Player target, InetSocketAddress address) {
        RegisteredServer targetServer = this.server.getAllServers().stream()
                .filter(registeredServer -> registeredServer.getServerInfo().getAddress().equals(address))
                .findAny()
                .orElseGet(() -> this.server.createRawRegisteredServer(
                        new ServerInfo(Integer.toHexString(address.hashCode()), address)
                ));
        return this.connectTo(source, target, targetServer);
    }

    public int connectTo(CommandSource source, Player target, RegisteredServer server) {
        SocketAddress targetAddress = server.getServerInfo().getAddress();
        source.sendMessage(Utilities.PREFIX
                .append(text(target.getUsername(), WHITE))
                .append(text(" will be sent to ", GREEN))
                .append(text(targetAddress.toString(), WHITE)));
        target.createConnectionRequest(server).fireAndForget();
        return 1;
    }
}
