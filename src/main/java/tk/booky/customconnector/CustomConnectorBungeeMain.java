package tk.booky.customconnector;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class CustomConnectorBungeeMain extends Plugin {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerCommand(this, new CustomConnectorCommand());
        getProxy().getPluginManager().registerListener(this, new CustomBrandListener());
    }

    public class CustomConnectorCommand extends Command {

        public CustomConnectorCommand() {
            super("connect", "customconnect.use");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (sender instanceof ProxiedPlayer) {
                if (args.length == 1) {
                    ProxiedPlayer player = (ProxiedPlayer) sender;

                    String[] split = args[0].split(":");
                    if (split.length == 1) split = new String[]{split[0], "25565"};

                    InetSocketAddress address = InetSocketAddress.createUnresolved(split[0], Integer.parseInt(split[1]));
                    ServerInfo serverInfo = getProxy().constructServerInfo(UUID.randomUUID().toString().substring(0, 6), address, "null", false);

                    player.connect(serverInfo, ServerConnectEvent.Reason.COMMAND);
                    player.sendMessage(new TextComponent("Sending you to " + address + "..."));
                } else {
                    sender.sendMessage(new TextComponent("Please specify the address to connect to!"));
                }
            } else {
                sender.sendMessage(new TextComponent("Please use this command as a player!"));
            }
        }
    }

    public class CustomBrandListener implements Listener {

        public byte[] brand = new PacketSerializer("youtu.be/dQw4w9WgXcQ").toArray();
        private final Map<UUID, Integer> tasks = new HashMap<>();

        @EventHandler
        public void postLogin(PostLoginEvent event) {
            tasks.put(event.getPlayer().getUniqueId(), ProxyServer.getInstance().getScheduler().schedule(CustomConnectorBungeeMain.this, () -> {
                if (event.getPlayer().getPendingConnection().getVersion() >= ProtocolConstants.MINECRAFT_1_13) {
                    event.getPlayer().sendData("minecraft:brand", brand);
                } else {
                    event.getPlayer().sendData("MC|Brand", brand);
                }
            }, 1, 1, TimeUnit.SECONDS).getId());
        }

        @EventHandler
        public void onDisconnect(PlayerDisconnectEvent event) {
            if (!tasks.containsKey(event.getPlayer().getUniqueId())) return;
            ProxyServer.getInstance().getScheduler().cancel(tasks.get(event.getPlayer().getUniqueId()));
        }
    }
}
