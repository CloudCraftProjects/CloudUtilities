package dev.booky.cloudutilities.util;
// Created by booky10 in CloudUtilities (04:25 04.11.23)

import com.google.common.base.Suppliers;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.booky.cloudutilities.config.CloudUtilsConfig.TablistConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.util.Ticks;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class TablistUpdater {

    private final ProxyServer proxy;
    private final TablistConfig tablistConfig;

    private final TablistPart header = new TablistPart();
    private final TablistPart footer = new TablistPart();

    private int updateTick = 0;
    private boolean dirty = true;

    public TablistUpdater(ProxyServer proxy, TablistConfig tablistConfig) {
        this.proxy = proxy;
        this.tablistConfig = tablistConfig;
    }

    public ScheduledTask start(Object plugin) {
        return this.proxy.getScheduler().buildTask(plugin, this::executeUpdate)
                .repeat(Ticks.SINGLE_TICK_DURATION_MS, TimeUnit.MILLISECONDS)
                .schedule();
    }

    public void executeUpdate() {
        if (this.updateTick++ >= this.tablistConfig.getUpdateInterval()) {
            this.updateTick = 0;
            this.header.processIntervalTick(this.tablistConfig.getHeaders());
            this.footer.processIntervalTick(this.tablistConfig.getFooters());
        }

        if (this.dirty) {
            this.dirty = false;
            this.updateTablists();
        }
    }

    public void updateTablists() {
        for (RegisteredServer server : this.proxy.getAllServers()) {
            Collection<Player> players = server.getPlayersConnected();
            if (players.isEmpty()) {
                continue;
            }

            Supplier<Map.Entry<Component, Component>> packetSupplier =
                    Suppliers.memoize(() -> this.buildTablist(server.getServerInfo()));
            for (Player player : players) {
                this.updateTablist0(player, packetSupplier);
            }
        }
    }

    public boolean updateTablist(Player player) {
        return this.updateTablist0(player, () -> {
            ServerInfo info = player.getCurrentServer()
                    .map(ServerConnection::getServerInfo)
                    .orElse(null);
            return this.buildTablist(info);
        });
    }

    private Map.Entry<Component, Component> buildTablist(ServerInfo server) {
        Component header = this.header.getDynamicComponent(server);
        Component footer = this.footer.getDynamicComponent(server);
        return Map.entry(header, footer);
    }

    private boolean updateTablist0(Player player, Supplier<Map.Entry<Component, Component>> tablistSupplier) {
        if (!player.isActive()) {
            return false;
        }
        Map.Entry<Component, Component> tablist = tablistSupplier.get();
        player.sendPlayerListHeaderAndFooter(tablist.getKey(), tablist.getValue());
        return true;
    }

    private final class TablistPart {

        private Component component = Component.empty();
        private int index = 0;

        public void processIntervalTick(List<Component> components) {
            if (components.isEmpty()) {
                this.updateComponent(Component.empty());
                this.index = 0;
                return;
            }

            int newIndex = ++this.index;
            if (newIndex >= components.size()) {
                newIndex = 0;
            }

            this.updateComponent(components.get(newIndex));
            this.index = newIndex;
        }

        private String buildTimeStr() {
            Calendar calendar = Calendar.getInstance();
            String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
            if (hour.length() == 1) {
                hour = "0" + hour;
            }

            String minute = Integer.toString(calendar.get(Calendar.MINUTE));
            if (minute.length() == 1) {
                minute = "0" + minute;
            }

            return hour + ":" + minute;
        }

        private Component replaceStatic(Component component) {
            if (Component.empty().equals(component)) {
                // no need for replacing stuff in an empty component
                return Component.empty();
            }

            String playerCountStr = Integer.toString(TablistUpdater.this.proxy.getPlayerCount());
            return component
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%TIME%")
                            .replacement(this.buildTimeStr())
                            .build())
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%PLAYERCOUNT%")
                            .replacement(playerCountStr)
                            .build());
        }

        public void updateComponent(Component component) {
            Component replacedComponent = this.replaceStatic(component);
            if (!this.component.equals(replacedComponent)) {
                this.component = replacedComponent;
                TablistUpdater.this.dirty = true;
            }
        }

        public Component getDynamicComponent(ServerInfo server) {
            Component component = this.component;
            if (component == Component.empty()) {
                return component;
            }

            return component.replaceText(
                    TextReplacementConfig.builder()
                            .matchLiteral("%SERVER%")
                            .replacement(server.getName())
                            .build());
        }
    }
}
