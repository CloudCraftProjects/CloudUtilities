package tk.booky.cloudutilities.listener;
// Created by booky10 in CloudUtilities (14:43 18.07.21)

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import tk.booky.cloudutilities.utils.Constants;

public class ConnectListener {

    @Subscribe
    public void postConnect(ServerPostConnectEvent event) {
        event.getPlayer().sendPluginMessage(Constants.BRAND_IDENTIFIER, Constants.BRAND_MESSAGE);
    }
}
