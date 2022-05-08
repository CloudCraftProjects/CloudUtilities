package dev.booky.cloudutilities;
// Created by booky10 in CustomConnector (14:54 19.06.21)

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.booky.cloudutilities.commands.ConnectCommand;
import dev.booky.cloudutilities.commands.LobbyCommand;
import dev.booky.cloudutilities.commands.LoopCommand;
import dev.booky.cloudutilities.commands.PingCommand;
import org.slf4j.Logger;

@Plugin(
    id = "cloudutilities",
    name = "CloudUtilities",
    version = "${version}",
    authors = "booky10"
)
public class CloudUtilitiesMain {

    @Inject @SuppressWarnings("unused") private ProxyServer server;
    @Inject @SuppressWarnings("unused") private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getCommandManager().register(LoopCommand.create(this, server));
        server.getCommandManager().register(ConnectCommand.create(server));
        server.getCommandManager().register(PingCommand.create(server));

        server.getCommandManager().register("lobby", LobbyCommand.create(server),
            "hub", "l", "h", "leave", "quit", "exit");
    }
}
