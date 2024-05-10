package dev.booky.cloudutilities;
// Created by booky10 in CustomConnector (14:54 19.06.21)

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.booky.cloudutilities.commands.ConnectCommand;
import dev.booky.cloudutilities.commands.LobbyCommand;
import dev.booky.cloudutilities.commands.LoopCommand;
import dev.booky.cloudutilities.commands.PingCommand;
import dev.booky.cloudutilities.config.CloudUtilsConfig;
import dev.booky.cloudutilities.listener.PingListener;
import dev.booky.cloudutilities.listener.TablistListener;
import dev.booky.cloudutilities.util.BuildConstants;
import dev.booky.cloudutilities.util.TablistUpdater;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.nio.file.Path;

import static dev.booky.cloudutilities.config.CloudUtilsConfig.CONFIGURATE_LOADER;

@Plugin(
        id = "cloudutilities",
        name = "CloudUtilities",
        version = BuildConstants.PLUGIN_VERSION,
        authors = "booky10",
        dependencies = @Dependency(id = "cloudcore")
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

        this.server.getEventManager().register(this,
                ProxyReloadEvent.class, handler -> {
                    try {
                        this.reload();
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }
                });

        Path configPath = this.dataDirectory.resolve("config.yml");
        CloudUtilsConfig config = CONFIGURATE_LOADER.loadObject(configPath, CloudUtilsConfig.class);

        if (this.tablistTask != null) {
            this.tablistTask.cancel();
            this.tablistTask = null;
        }

        if (!config.getTablist().isEmpty()) {
            TablistUpdater updater = new TablistUpdater(this.server, config.getTablist());
            this.tablistTask = updater.start(this);

            TablistListener listener = new TablistListener(updater);
            this.server.getEventManager().register(this, listener);

            for (Player player : this.server.getAllPlayers()) {
                updater.updateTablist(player);
            }
        }

        if (!config.getPing().isDisabled()) {
            PingListener listener = new PingListener(config.getPing());
            this.server.getEventManager().register(this, listener);
        }
    }
}
