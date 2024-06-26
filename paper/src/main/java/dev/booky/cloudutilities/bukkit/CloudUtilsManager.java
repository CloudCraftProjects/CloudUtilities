package dev.booky.cloudutilities.bukkit;
// Created by booky10 in CloudUtilities (04:08 11.05.2024.)

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.util.function.Consumer;

import static dev.booky.cloudutilities.bukkit.CloudUtilsConfig.CONFIGURATE_LOADER;

@Singleton
public class CloudUtilsManager {

    private final Plugin plugin;

    private final Path configPath;
    private CloudUtilsConfig config;

    @Inject
    public CloudUtilsManager(Plugin plugin) {
        this.plugin = plugin;

        this.configPath = plugin.getDataFolder().toPath().resolve("config.yml");
        this.config = this.loadConfig();
    }

    public void reloadConfig() {
        this.config = this.loadConfig();
    }

    public void updateConfig(Consumer<CloudUtilsConfig> updater) {
        updater.accept(this.config);
        this.saveConfig();
    }

    public void saveConfig() {
        CONFIGURATE_LOADER.saveObject(this.configPath, this.config);
    }

    private CloudUtilsConfig loadConfig() {
        return CONFIGURATE_LOADER.loadObject(this.configPath, CloudUtilsConfig.class);
    }

    public CloudUtilsConfig getConfig() {
        return this.config;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }
}
