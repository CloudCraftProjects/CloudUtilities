package dev.booky.cloudutilities.bukkit;
// Created by booky10 in CloudUtilities (04:01 11.05.2024.)

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class CloudUtilitiesMain extends JavaPlugin {

    private CloudUtilsManager manager;

    @Override
    public void onLoad() {
        this.manager = new CloudUtilsManager(this);
        Bukkit.getServicesManager().register(CloudUtilsManager.class,
                this.manager, this, ServicePriority.Normal);
    }

    public CloudUtilsManager getManager() {
        return this.manager;
    }
}
