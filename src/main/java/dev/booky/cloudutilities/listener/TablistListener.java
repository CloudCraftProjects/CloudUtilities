package dev.booky.cloudutilities.listener;
// Created by booky10 in CloudUtilities (04:47 08.05.22)

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

public record TablistListener(Component header, Component footer) {

    @Subscribe
    public void onJoin(PostLoginEvent event) {
        this.onUpdate(event.getPlayer());
    }

    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        this.onUpdate(event.getPlayer());
    }

    public void onUpdate(Player player) {
        player.sendPlayerListHeaderAndFooter(this.header, this.footer);
    }
}
