package dev.booky.cloudutilities;
// Created by booky10 in CustomConnector (14:54 19.06.21)

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.booky.cloudutilities.commands.ConnectCommand;
import dev.booky.cloudutilities.commands.LobbyCommand;
import dev.booky.cloudutilities.commands.LoopCommand;
import dev.booky.cloudutilities.commands.PingCommand;
import dev.booky.cloudutilities.listener.PingListener;
import dev.booky.cloudutilities.listener.TablistListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "cloudutilities",
        name = "CloudUtilities",
        version = "${version}",
        authors = "booky10"
)
public class CloudUtilitiesMain {

    @Inject @SuppressWarnings("unused") private ProxyServer server;
    @Inject @SuppressWarnings("unused") private Logger logger;
    @Inject @DataDirectory private Path dataDirectory;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        this.server.getCommandManager().register(LoopCommand.create(this, this.server));
        this.server.getCommandManager().register(ConnectCommand.create(this.server));
        this.server.getCommandManager().register(PingCommand.create(this.server));

        this.server.getCommandManager().register("lobby", LobbyCommand.create(this.server),
                "hub", "l", "h", "leave", "quit", "exit");

        this.reload();
    }

    private void reload() throws IOException {
        this.server.getEventManager().unregisterListeners(this);

        this.server.getEventManager().register(this, ProxyReloadEvent.class, handler -> {
            try {
                this.reload();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });

        Path configPath = this.dataDirectory.resolve("config.yml");
        Files.createDirectories(configPath.getParent());
        if (!Files.exists(configPath)) {
            Files.createFile(configPath);
        }

        ConfigurationNode config = YAMLConfigurationLoader.builder()
                .setPath(configPath).build().load();

        Component header = MiniMessage.miniMessage().deserializeOr(
                config.getNode("tablist", "header").getString(), Component.empty());
        Component footer = MiniMessage.miniMessage().deserializeOr(
                config.getNode("tablist", "footer").getString(), Component.empty());

        TablistListener listener = new TablistListener(header, footer);
        this.server.getEventManager().register(this, listener);

        for (Player player : this.server.getAllPlayers()) {
            listener.onUpdate(player);
        }

        ProtocolVersion first = ProtocolVersion.getProtocolVersion(config
                .getNode("ping", "first-supported").getInt(-1));
        ProtocolVersion last = ProtocolVersion.getProtocolVersion(config
                .getNode("ping", "last-supported").getInt(-1));
        this.server.getEventManager().register(this, new PingListener(first, last));
    }
}
