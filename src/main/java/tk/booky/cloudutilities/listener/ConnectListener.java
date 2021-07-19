package tk.booky.cloudutilities.listener;
// Created by booky10 in CloudUtilities (14:43 18.07.21)

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;

import static tk.booky.cloudutilities.utils.Constants.BRAND_IDENTIFIER;
import static tk.booky.cloudutilities.utils.Constants.BRAND_MESSAGE;

public class ConnectListener {

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTarget() instanceof Player) {
            if (event.getIdentifier().getId().equals(BRAND_IDENTIFIER.getId())) {
                event.getTarget().sendPluginMessage(BRAND_IDENTIFIER, BRAND_MESSAGE);
                event.setResult(PluginMessageEvent.ForwardResult.handled());
            }
        }
    }
}
