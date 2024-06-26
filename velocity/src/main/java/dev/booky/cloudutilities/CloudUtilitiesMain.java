package dev.booky.cloudutilities;
// Created by booky10 in CustomConnector (14:54 19.06.21)

import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyReloadEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.booky.cloudcore.i18n.CloudTranslator;
import dev.booky.cloudutilities.commands.AbstractCommand;
import dev.booky.cloudutilities.commands.ConnectCommand;
import dev.booky.cloudutilities.commands.HubCommand;
import dev.booky.cloudutilities.commands.LoopCommand;
import dev.booky.cloudutilities.commands.PingCommand;
import dev.booky.cloudutilities.config.CloudUtilsConfig;
import dev.booky.cloudutilities.listener.PingListener;
import dev.booky.cloudutilities.listener.TablistListener;
import dev.booky.cloudutilities.util.BuildConstants;
import dev.booky.cloudutilities.util.TablistUpdater;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.key.Key;
import org.bstats.velocity.Metrics;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static dev.booky.cloudutilities.config.CloudUtilsConfig.CONFIGURATE_LOADER;
import static net.kyori.adventure.key.Key.key;

@Plugin(
        id = "cloudutilities",
        name = "CloudUtilities",
        version = BuildConstants.PLUGIN_VERSION,
        authors = "booky10",
        dependencies = @Dependency(id = "cloudcore")
)
@Singleton
public class CloudUtilitiesMain {

    private static final List<Class<? extends AbstractCommand>> COMMAND_CLASSES = List.of(
            ConnectCommand.class, HubCommand.class, LoopCommand.class, PingCommand.class
    );
    private static final List<Locale> SUPPORTED_LOCALES = List.of(
            Locale.ENGLISH, Locale.GERMAN
    );

    private final Injector injector;
    private final ProxyServer server;
    private final Metrics.Factory metrics;

    private final Path configPath;
    private CloudUtilsConfig config;

    private final List<Object> registeredListeners = new ArrayList<>();
    private @Nullable ScheduledTask tablistTask;
    private @Nullable CloudTranslator translator;

    @Inject
    public CloudUtilitiesMain(
            Injector injector,
            ProxyServer server,
            @DataDirectory Path dataDirectory,
            Metrics.Factory metrics
    ) {
        this.injector = injector;
        this.server = server;
        this.metrics = metrics;

        this.configPath = dataDirectory.resolve("config.yml");
        this.config = this.loadConfig();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.metrics.make(this, 21858);

        for (Class<? extends AbstractCommand> commandClass : COMMAND_CLASSES) {
            AbstractCommand command = this.injector.getInstance(commandClass);
            command.register(this.server.getCommandManager(), this);
        }

        this.reload();
    }

    @Subscribe
    public void onProxyReload(ProxyReloadEvent event) {
        this.reload();
    }

    private void unload() {
        // unregister all listeners
        for (Object listener : this.registeredListeners) {
            this.server.getEventManager().unregisterListener(this, listener);
        }

        // cancel tablist updating task
        if (this.tablistTask != null) {
            this.tablistTask.cancel();
            this.tablistTask = null;
        }

        // unload translator from registry
        if (this.translator != null) {
            this.translator.unload();
            this.translator = null;
        }
    }

    private synchronized void reload() {
        this.unload(); // unload before reload

        // reload configuration
        this.reloadConfig();

        // load and register translations
        Key translatorName = key("cloudutilities", "i18n");
        this.translator = new CloudTranslator(this.getClass().getClassLoader(),
                translatorName, SUPPORTED_LOCALES);
        this.translator.load();

        // setup tablist, if not empty
        if (!this.config.getTablist().isEmpty()) {
            TablistUpdater updater = new TablistUpdater(this.server, this.config.getTablist());
            this.tablistTask = updater.start(this);

            TablistListener listener = new TablistListener(updater);
            this.server.getEventManager().register(this, listener);
            this.registeredListeners.add(listener);

            for (Player player : this.server.getAllPlayers()) {
                updater.updateTablist(player);
            }
        }

        // setup ping, if not useless
        if (!this.config.getPing().isDisabled()) {
            PingListener listener = new PingListener(this.config.getPing());
            this.server.getEventManager().register(this, listener);
            this.registeredListeners.add(listener);
        }
    }

    public void reloadConfig() {
        this.config = this.loadConfig();
    }

    private CloudUtilsConfig loadConfig() {
        return CONFIGURATE_LOADER.loadObject(this.configPath, CloudUtilsConfig.class);
    }

    public CloudUtilsConfig getConfig() {
        return this.config;
    }
}
