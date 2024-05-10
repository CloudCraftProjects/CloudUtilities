package dev.booky.cloudutilities.config;
// Created by booky10 in CloudUtilities (00:09 11.05.2024.)

import com.velocitypowered.api.network.ProtocolVersion;
import dev.booky.cloudcore.config.ConfigurateLoader;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

@ConfigSerializable
public class CloudUtilsConfig {

    public static final ConfigurateLoader<?, ?> CONFIGURATE_LOADER = ConfigurateLoader.yamlLoader()
            .withAllDefaultSerializers()
            .withSerializers(builder -> builder
                    .register(ProtocolVersion.class, ProtocolVersionSerializer.INSTANCE))
            .build();

    private TablistConfig tablist = new TablistConfig();

    @ConfigSerializable
    public static class TablistConfig {

        private List<Component> headers = List.of();
        private List<Component> footers = List.of();
        @Comment("Interval between switching header/footer lines in ticks")
        private int updateInterval = 40;

        private TablistConfig() {
        }

        public boolean isEmpty() {
            return this.headers.isEmpty() && this.footers.isEmpty();
        }

        public List<Component> getHeaders() {
            return this.headers;
        }

        public List<Component> getFooters() {
            return this.footers;
        }

        public int getUpdateInterval() {
            return this.updateInterval;
        }
    }

    private PingConfig ping = new PingConfig();

    @ConfigSerializable
    public static class PingConfig {

        private ProtocolVersion firstSupported = ProtocolVersion.UNKNOWN;
        private ProtocolVersion lastSupported = ProtocolVersion.UNKNOWN;

        private PingConfig() {
        }

        public boolean isDisabled() {
            return !this.firstSupported.isSupported() && !this.lastSupported.isSupported();
        }

        public ProtocolVersion getFirstSupported() {
            return this.firstSupported;
        }

        public ProtocolVersion getLastSupported() {
            return this.lastSupported;
        }
    }

    private CloudUtilsConfig() {
    }

    public TablistConfig getTablist() {
        return this.tablist;
    }

    public PingConfig getPing() {
        return this.ping;
    }
}
