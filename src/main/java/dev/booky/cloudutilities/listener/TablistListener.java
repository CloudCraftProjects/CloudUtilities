package dev.booky.cloudutilities.listener;
// Created by booky10 in CloudUtilities (04:47 08.05.22)

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import dev.booky.cloudutilities.util.TablistUpdater;

public final class TablistListener {

    private final TablistUpdater updater;

    public TablistListener(TablistUpdater updater) {
        this.updater = updater;
    }

    @Subscribe
    public void onJoin(PostLoginEvent event) {
        this.updater.updateTablist(event.getPlayer());
    }

    @Subscribe
    public void onServerSwitch(ServerPostConnectEvent event) {
        this.updater.updateTablist(event.getPlayer());
    }
}
