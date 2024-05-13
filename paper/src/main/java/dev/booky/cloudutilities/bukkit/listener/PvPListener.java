package dev.booky.cloudutilities.bukkit.listener;
// Created by booky10 in CloudUtilities (04:29 11.05.2024.)

import dev.booky.cloudutilities.bukkit.CloudUtilsManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

@Singleton
public class PvPListener implements Listener {

    private final CloudUtilsManager manager;

    @Inject
    public PvPListener(CloudUtilsManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getDamageSource().getCausingEntity() instanceof Player
                && event.getEntity() instanceof Player
                && !this.manager.getConfig().isAllowPvP()) {
            event.setCancelled(true);
        }
    }
}
