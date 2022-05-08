package dev.booky.cloudutilities.listener;
// Created by booky10 in CloudUtilities (14:03 08.05.22)

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.server.ServerPing;

public record PingListener(ProtocolVersion first, ProtocolVersion last) {

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        String versionRange = "Velocity ";
        if (first != last || first.getVersionsSupportedBy().size() > 0) {
            versionRange += first.getVersionIntroducedIn() + " - " + last.getMostRecentSupportedVersion();
        } else {
            versionRange += first.getVersionIntroducedIn();
        }

        int protocol;
        if (first.compareTo(event.getConnection().getProtocolVersion()) > 0) { // Too old
            protocol = -1;
        } else if (last.compareTo(event.getConnection().getProtocolVersion()) < 0) { // Too new
            protocol = -1;
        } else {
            protocol = event.getConnection().getProtocolVersion().getProtocol();
        }

        ServerPing.Builder ping = event.getPing().asBuilder();
        ping.version(new ServerPing.Version(protocol, versionRange));
        event.setPing(ping.build());
    }
}
