package dev.booky.cloudutilities;
// Created by booky10 in CustomConnector (14:54 19.06.21)

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.booky.cloudutilities.commands.ConnectCommand;
import dev.booky.cloudutilities.commands.LobbyCommand;
import dev.booky.cloudutilities.commands.LoopCommand;
import dev.booky.cloudutilities.commands.PingCommand;
import dev.booky.cloudutilities.listener.JoinListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
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
        server.getCommandManager().register(LoopCommand.create(this, server));
        server.getCommandManager().register(ConnectCommand.create(server));
        server.getCommandManager().register(PingCommand.create(server));

        server.getCommandManager().register("lobby", LobbyCommand.create(server),
            "hub", "l", "h", "leave", "quit", "exit");

        onProxyReload(null);
    }

    @Subscribe
    public void onProxyReload(ProxyReloadEvent event) throws IOException {
        server.getEventManager().unregisterListeners(this);

        dataDirectory.toFile().mkdirs();
        File configFile = new File(dataDirectory.toFile(), "config.yml");
        if (!configFile.exists()) configFile.createNewFile();

        ConfigurationNode config = YAMLConfigurationLoader.builder()
            .setPath(dataDirectory.resolve("config.yml"))
            .build().load();

        GsonComponentSerializer gson = GsonComponentSerializer.gson();
        Component header = gson.deserialize(config.getNode("tablist", "header").getString("{\"text\":\"\"}"));
        Component footer = gson.deserialize(config.getNode("tablist", "footer").getString("{\"text\":\"\"}"));

        if (!header.equals(Component.empty()) || !footer.equals(Component.empty())) {
            JoinListener listener = new JoinListener(header, footer);
            server.getEventManager().register(this, listener);

            for (Player player : server.getAllPlayers()) {
                listener.onJoin(new PostLoginEvent(player));
            }
        }
    }
}
