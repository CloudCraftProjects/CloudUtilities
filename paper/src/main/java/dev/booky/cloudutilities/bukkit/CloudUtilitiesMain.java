package dev.booky.cloudutilities.bukkit;
// Created by booky10 in CloudUtilities (04:01 11.05.2024.)

import dev.booky.cloudutilities.bukkit.commands.AbstractCommand;
import dev.booky.cloudutilities.bukkit.commands.AllowPvPCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.List;

public class CloudUtilitiesMain extends JavaPlugin {

    private @MonotonicNonNull CloudUtilsManager manager;
    private @MonotonicNonNull List<AbstractCommand> commands;

    @Override
    public void onLoad() {
        this.manager = new CloudUtilsManager(this);
        Bukkit.getServicesManager().register(CloudUtilsManager.class,
                this.manager, this, ServicePriority.Normal);
    }

    @Override
    public void onEnable() {
        this.commands = List.of(
                new AllowPvPCommand(this.manager)
        );
        for (AbstractCommand command : this.commands) {
            command.register(this);
        }
    }

    @Override
    public void onDisable() {
        for (AbstractCommand command : this.commands) {
            command.unregister();
        }
        this.commands.clear();
    }

    public CloudUtilsManager getManager() {
        return this.manager;
    }
}
