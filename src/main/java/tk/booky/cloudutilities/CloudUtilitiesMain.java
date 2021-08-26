package tk.booky.cloudutilities;
// Created by booky10 in CustomConnector (14:54 19.06.21)

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import tk.booky.cloudutilities.commands.ConnectCommand;
import tk.booky.cloudutilities.commands.LoopCommand;
import tk.booky.cloudutilities.commands.PingCommand;
import tk.booky.cloudutilities.listener.PingListener;
import tk.booky.cloudutilities.utils.PlayerArgumentParser;

@Plugin(id = "cloudutilities", name = "CloudUtilities", version = "@version@", authors = "booky10")
public class CloudUtilitiesMain {

    @Inject @SuppressWarnings("unused") private ProxyServer server;
    @Inject @SuppressWarnings("unused") private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Setting proxy server on own argument
        PlayerArgumentParser.server = server;

        // Registering commands
        server.getCommandManager().register(LoopCommand.create(this, server));
        server.getCommandManager().register(ConnectCommand.create(server));
        server.getCommandManager().register(PingCommand.create());

        // Registering listeners
        server.getEventManager().register(this, new PingListener(server));
    }
}
