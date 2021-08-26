package tk.booky.cloudutilities.listener;
// Created by booky10 in CloudUtilities (14:43 18.07.21)

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing.SamplePlayer;

import java.util.ArrayList;
import java.util.List;

public class PingListener {

    private final ProxyServer server;
    private final Object lock = "";
    private SamplePlayer[] players;
    private long last;

    public PingListener(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - last > 10_000) {
            synchronized (lock) {
                List<Player> online = new ArrayList<>(server.getAllPlayers());
                SamplePlayer[] sampled = new SamplePlayer[online.size()];

                for (int i = 0; i < online.size(); i++) {
                    Player player = online.get(i);
                    sampled[i] = new SamplePlayer(player.getUsername(), player.getUniqueId());
                }

                last = System.currentTimeMillis();
                players = sampled;
            }
        }

        event.setPing(event.getPing().asBuilder().samplePlayers(players).build());
    }
}
