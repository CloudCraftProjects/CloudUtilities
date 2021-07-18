package tk.booky.cloudutilities;
// Created by booky10 in CustomConnector (14:54 19.06.21)

import com.google.inject.Inject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Plugin(id = "cloudutilities", name = "CloudUtilities", version = "@version@", authors = "booky10")
public class CloudUtilitiesMain {

    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.forDefaultNamespace("brand");
    public static final byte[] MESSAGE = StringSerializer.fromString("youtu.be/dQw4w9WgXcQ");

    @Inject @SuppressWarnings("unused") private ProxyServer server;
    @Inject @SuppressWarnings("unused") private Logger logger;

    private final Object sampleLock = "JUST_A_STRING";
    private ServerPing.SamplePlayer[] sampled;
    private long lastSample = 0;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getCommandManager().register(
            new BrigadierCommand(
                LiteralArgumentBuilder.
                    <CommandSource>literal("ping")
                    .requires(source -> source instanceof Player)
                    .executes(context -> {
                        context.getSource().sendMessage(Component.text("§7[§bPing§7]§a Du hast aktuell einen Ping von " + ((Player) context.getSource()).getPing() + "ms!"));
                        return 1;
                    })
            )
        );

        server.getCommandManager().register(
            new BrigadierCommand(
                LiteralArgumentBuilder.<CommandSource>
                    literal("connect")
                    .requires(source -> source instanceof Player && source.hasPermission("customconnect.use"))
                    .then(RequiredArgumentBuilder.<CommandSource, String>
                        argument("host", StringArgumentType.string())
                        .then(RequiredArgumentBuilder.<CommandSource, Integer>
                            argument("port", IntegerArgumentType.integer(1, 65535))
                            .executes(context -> {
                                try {
                                    Player player = (Player) context.getSource();

                                    String host = StringArgumentType.getString(context, "host");
                                    int port = IntegerArgumentType.getInteger(context, "port");

                                    InetSocketAddress address = new InetSocketAddress(host, port);
                                    RegisteredServer server = this.server.createRawRegisteredServer(new ServerInfo(Integer.toString(address.hashCode()), address));

                                    player.sendMessage(Component.text("You will be sent to " + address + "..."));
                                    player.createConnectionRequest(server).fireAndForget();

                                    return 1;
                                } catch (Throwable throwable) {
                                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
                                }
                            })))
            )
        );
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        if (System.currentTimeMillis() - lastSample > 10 * 1000) {
            synchronized (sampleLock) {
                List<Player> players = new ArrayList<>(server.getAllPlayers());
                ServerPing.SamplePlayer[] sampled = new ServerPing.SamplePlayer[players.size()];

                for (int i = 0; i < players.size(); i++) {
                    Player player = players.get(i);
                    sampled[i] = new ServerPing.SamplePlayer(player.getUsername(), player.getUniqueId());
                }

                lastSample = System.currentTimeMillis();
                this.sampled = sampled;
            }
        }

        event.setPing(event.getPing().asBuilder().samplePlayers(sampled).version(new ServerPing.Version(event.getPing().getVersion().getProtocol(), "CloudCraft 1.13.2 - 1.17.1")).build());
    }

    @Subscribe
    public void postLogin(ServerPostConnectEvent event) {
        event.getPlayer().sendPluginMessage(IDENTIFIER, MESSAGE);
    }
}
