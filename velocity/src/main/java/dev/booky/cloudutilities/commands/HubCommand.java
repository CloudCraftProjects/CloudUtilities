package dev.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (02:21 08.05.22)

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.booky.cloudutilities.CloudUtilitiesMain;
import dev.booky.cloudutilities.util.Utilities;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static dev.booky.cloudutilities.util.PlayerArguments.getPlayer;
import static dev.booky.cloudutilities.util.PlayerArguments.playerSuggestions;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

@Singleton
public class HubCommand extends AbstractCommand {

    private final ProxyServer server;
    private final CloudUtilitiesMain plugin;

    @Inject
    public HubCommand(ProxyServer server, CloudUtilitiesMain plugin) {
        super("hub", "lobby", "l", "h", "leave", "quit", "exit");
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public LiteralCommandNode<CommandSource> buildNode() {
        return literal(this.getLabel())
                .requires(source -> source instanceof Player
                        && source.hasPermission(this.getPermission()))
                .executes(ctx -> this.sendToHub(ctx.getSource(), (Player) ctx.getSource()))
                .then(argument("target", word())
                        .suggests(playerSuggestions(this.server))
                        .requires(source -> source.hasPermission(this.getPermission("other")))
                        .executes(ctx -> this.sendToHub(ctx.getSource(),
                                getPlayer(this.server, ctx, "target"))))
                .build();
    }

    public int sendToHub(CommandSource source, Player target) {
        RegisteredServer lobby = this.server.getServer(this.plugin.getConfig().getLobbyServer())
                .orElseThrow(() -> new RuntimeException("Can't find lobby server specified in config"));

        RegisteredServer currentServer = target.getCurrentServer()
                .map(ServerConnection::getServer)
                .orElse(null);
        if (lobby.equals(currentServer)) {
            source.sendMessage(Utilities.PREFIX.append(text("You are already in the lobby", RED)));
            return 1;
        }

        target.createConnectionRequest(lobby).fireAndForget();
        source.sendMessage(Utilities.PREFIX.append(text("Sent " + target.getUsername() + " to lobby", GREEN)));
        return 1;
    }
}
