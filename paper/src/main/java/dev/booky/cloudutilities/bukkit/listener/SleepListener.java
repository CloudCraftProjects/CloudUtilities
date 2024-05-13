package dev.booky.cloudutilities.bukkit.listener;
// Created by booky10 in CloudUtilities (10:59 13.05.2024.)

import dev.booky.cloudutilities.bukkit.CloudUtilsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;

public class SleepListener implements Listener {

    private final CloudUtilsManager manager;

    public SleepListener(CloudUtilsManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNightSkip(TimeSkipEvent event) {
        if (event.getSkipReason() != TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            return;
        }
        Component message = this.manager.getConfig().getNightSkipMessage();
        if (message != null) {
            Bukkit.broadcast(message);
        }
    }
}
