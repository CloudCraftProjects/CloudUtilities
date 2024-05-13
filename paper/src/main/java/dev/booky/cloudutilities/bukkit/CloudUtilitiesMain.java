package dev.booky.cloudutilities.bukkit;
// Created by booky10 in CloudUtilities (04:01 11.05.2024.)

import dev.booky.cloudcore.i18n.CloudTranslator;
import dev.booky.cloudutilities.bukkit.commands.AbstractCommand;
import dev.booky.cloudutilities.bukkit.commands.AllowPvPCommand;
import dev.booky.cloudutilities.bukkit.commands.FlyCommand;
import dev.booky.cloudutilities.bukkit.commands.VanillaMsgCommand;
import dev.booky.cloudutilities.bukkit.listener.PvPListener;
import dev.booky.cloudutilities.bukkit.listener.SleepListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.List;
import java.util.Locale;

public class CloudUtilitiesMain extends JavaPlugin {

    private static final List<Locale> SUPPORTED_LOCALES = List.of(
            Locale.ENGLISH, Locale.GERMAN
    );

    private @MonotonicNonNull CloudTranslator i18n;
    private @MonotonicNonNull CloudUtilsManager manager;

    @Override
    public void onLoad() {
        this.i18n = new CloudTranslator(this.getClassLoader(),
                new NamespacedKey(this, "i18n"),
                SUPPORTED_LOCALES);
        this.i18n.load();

        this.manager = new CloudUtilsManager(this);
        Bukkit.getServicesManager().register(CloudUtilsManager.class,
                this.manager, this, ServicePriority.Normal);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            List<AbstractCommand> commands = List.of(
                    new AllowPvPCommand(this.manager),
                    new FlyCommand(),
                    new VanillaMsgCommand()
            );
            for (AbstractCommand command : commands) {
                command.register(event.registrar(), this);
            }
        });
    }

    @Override
    public void onEnable() {
        List<Listener> listeners = List.of(
                new PvPListener(this.manager),
                new SleepListener(this.manager)
        );
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable() {
        if (this.i18n != null) {
            this.i18n.unload();
        }
    }

    public CloudUtilsManager getManager() {
        return this.manager;
    }
}
