package dev.booky.cloudutilities.listener;
// Created by booky10 in CloudUtilities (04:47 08.05.22)

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import net.kyori.adventure.text.Component;

public record JoinListener(Component header, Component footer) {

    @Subscribe
    public void onJoin(PostLoginEvent event) {
        if (header.equals(Component.empty())) {
            event.getPlayer().sendPlayerListFooter(footer);
            return;
        }

        if (footer.equals(Component.empty())) {
            event.getPlayer().sendPlayerListHeader(header);
            return;
        }

        event.getPlayer().sendPlayerListHeaderAndFooter(header, footer);
    }
}
