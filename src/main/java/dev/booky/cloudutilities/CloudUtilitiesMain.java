package dev.booky.cloudutilities;
// Created by booky10 in CustomConnector (14:54 19.06.21)

import com.google.common.reflect.TypeToken;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.booky.cloudutilities.commands.ConnectCommand;
import dev.booky.cloudutilities.commands.LobbyCommand;
import dev.booky.cloudutilities.commands.LoopCommand;
import dev.booky.cloudutilities.commands.PingCommand;
import dev.booky.cloudutilities.listener.PingListener;
import dev.booky.cloudutilities.listener.TablistListener;
import dev.booky.cloudutilities.util.TablistUpdater;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Plugin(
        id = "cloudutilities",
        name = "CloudUtilities",
        version = "${version}",
        authors = "booky10"
)
@Singleton
public class CloudUtilitiesMain {

    private final ProxyServer server;
    private final Path dataDirectory;

    private @Nullable ScheduledTask tablistTask;

    @Inject
    public CloudUtilitiesMain(ProxyServer server, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.dataDirectory = dataDirectory;
    }

    private static List<Component> getComponents(ConfigurationNode node) {
        try {
            return node.getList(new TypeToken<String>() {})
                    .stream()
                    .map(MiniMessage.miniMessage()::deserialize)
                    .toList();
        } catch (ObjectMappingException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        this.server.getCommandManager().register(LoopCommand.create(this, this.server));
        this.server.getCommandManager().register(ConnectCommand.create(this.server));
        this.server.getCommandManager().register(PingCommand.create(this.server));

        this.server.getCommandManager().register("lobby", LobbyCommand.create(this.server),
                "hub", "l", "h", "leave", "quit", "exit");

        this.reload();
    }

    private synchronized void reload() throws IOException {
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

        {
            if (this.tablistTask != null) {
                this.tablistTask.cancel();
                this.tablistTask = null;
            }

            List<Component> headers = getComponents(config.getNode("tablist", "headers"));
            List<Component> footers = getComponents(config.getNode("tablist", "footers"));
            int updateInterval = config.getNode("tablist", "update-interval").getInt(40);

            if (!headers.isEmpty() || !footers.isEmpty()) {
                TablistUpdater updater = new TablistUpdater(this.server, updateInterval, headers, footers);
                this.tablistTask = updater.start(this);

                TablistListener listener = new TablistListener(updater);
                this.server.getEventManager().register(this, listener);

                for (Player player : this.server.getAllPlayers()) {
                    updater.updateTablist(player);
                }
            }
        }

        {
            ProtocolVersion first = ProtocolVersion.getProtocolVersion(config
                    .getNode("ping", "first-supported").getInt(-1));
            ProtocolVersion last = ProtocolVersion.getProtocolVersion(config
                    .getNode("ping", "last-supported").getInt(-1));
            this.server.getEventManager().register(this, new PingListener(first, last));
        }
    }
}
