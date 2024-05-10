package dev.booky.cloudutilities.listener;
// Created by booky10 in CloudUtilities (14:03 08.05.22)

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.server.ServerPing;
import dev.booky.cloudutilities.config.CloudUtilsConfig.PingConfig;

public class PingListener {

    private static final String SOFTWARE_NAME = System.getProperty(
            "cloudutilities.software-name", "Velocity") + " ";

    private final PingConfig pingConfig;

    public PingListener(PingConfig pingConfig) {
        this.pingConfig = pingConfig;
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        ProtocolVersion playerVer = event.getConnection().getProtocolVersion();
        ProtocolVersion first = this.pingConfig.getFirstSupported();
        ProtocolVersion last = this.pingConfig.getLastSupported();

        int protocol;
        if (playerVer.isSupported()
                && (!first.isSupported() || playerVer.noLessThan(first))
                && (!last.isSupported() || playerVer.noGreaterThan(last))) {
            protocol = playerVer.getProtocol(); // valid version
        } else {
            protocol = -1; // invalid version
        }

        String versionName = SOFTWARE_NAME + this.getVersionRange(first, last);
        event.setPing(event.getPing().asBuilder()
                .version(new ServerPing.Version(protocol, versionName))
                .build());
    }

    private String getVersionRange(ProtocolVersion first, ProtocolVersion last) {
        StringBuilder version = new StringBuilder(first.getVersionIntroducedIn());
        if (first != last || first.getVersionsSupportedBy().size() > 1) {
            version.append('-').append(last.getMostRecentSupportedVersion());
        }
        return version.toString();
    }
}
