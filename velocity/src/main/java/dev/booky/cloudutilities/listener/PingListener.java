package dev.booky.cloudutilities.listener;
// Created by booky10 in CloudUtilities (14:03 08.05.22)

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.server.ServerPing;

public record PingListener(ProtocolVersion first, ProtocolVersion last) {

    private static final String SOFTWARE_NAME = System.getProperty("cloudutilities.software-name", "Velocity") + " ";

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        ProtocolVersion playerVer = event.getConnection().getProtocolVersion();

        int protocol = -1;
        if (playerVer.compareTo(this.first) >= 0 && playerVer.compareTo(this.last) <= 0) {
            protocol = playerVer.getProtocol(); // valid version
        }

        String versionName = SOFTWARE_NAME + this.getVersionRange();
        event.setPing(event.getPing().asBuilder()
                .version(new ServerPing.Version(protocol, versionName))
                .build());
    }

    private String getVersionRange() {
        StringBuilder version = new StringBuilder(this.first.getVersionIntroducedIn());
        if (this.first != this.last || this.first.getVersionsSupportedBy().size() > 1) {
            version.append('-').append(this.last.getMostRecentSupportedVersion());
        }
        return version.toString();
    }
}
