package dev.booky.cloudutilities.bukkit;
// Created by booky10 in CloudUtilities (04:01 11.05.2024.)

import dev.booky.cloudcore.i18n.CloudTranslator;
import dev.booky.cloudutilities.bukkit.commands.AbstractCommand;
import dev.booky.cloudutilities.bukkit.commands.AllowPvPCommand;
import dev.booky.cloudutilities.bukkit.commands.FlyCommand;
import dev.booky.cloudutilities.bukkit.listener.PvPListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
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
    private @MonotonicNonNull List<AbstractCommand> commands;

    @Override
    public void onLoad() {
        this.i18n = new CloudTranslator(this.getClassLoader(),
                new NamespacedKey(this, "i18n"),
                SUPPORTED_LOCALES);
        this.i18n.load();

        this.manager = new CloudUtilsManager(this);
        Bukkit.getServicesManager().register(CloudUtilsManager.class,
                this.manager, this, ServicePriority.Normal);
    }

    @Override
    public void onEnable() {
        this.commands = List.of(
                new AllowPvPCommand(this.manager),
                new FlyCommand()
        );
        for (AbstractCommand command : this.commands) {
            command.register(this);
        }

        PluginManager plugins = Bukkit.getPluginManager();
        plugins.registerEvents(new PvPListener(this.manager), this);
    }

    @Override
    public void onDisable() {
        if (this.commands != null) {
            for (AbstractCommand command : this.commands) {
                command.unregister();
            }
            this.commands.clear();
        }
        if (this.i18n != null) {
            this.i18n.unload();
        }
    }

    public CloudUtilsManager getManager() {
        return this.manager;
    }
}
