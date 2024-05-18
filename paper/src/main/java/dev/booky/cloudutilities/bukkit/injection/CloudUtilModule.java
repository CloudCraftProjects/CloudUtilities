package dev.booky.cloudutilities.bukkit.injection;
// Created by booky10 in CloudUtilities (22:54 13.05.2024.)

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import dev.booky.cloudutilities.bukkit.CloudUtilitiesMain;
import dev.booky.cloudutilities.bukkit.CloudUtilsConfig;
import dev.booky.cloudutilities.bukkit.CloudUtilsManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CloudUtilModule extends AbstractModule {

    private final CloudUtilitiesMain plugin;

    public CloudUtilModule(CloudUtilitiesMain plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        // plugin instances
        this.bind(CloudUtilitiesMain.class).toInstance(this.plugin);
        this.bind(JavaPlugin.class).toInstance(this.plugin);
        this.bind(Plugin.class).toInstance(this.plugin);
    }

    @Provides
    public CloudUtilsConfig getConfig(CloudUtilsManager manager) {
        return manager.getConfig();
    }
}
