package dev.booky.cloudutilities.bukkit;
// Created by booky10 in CloudUtilities (04:08 11.05.2024.)

import dev.booky.cloudcore.config.ConfigurateLoader;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class CloudUtilsConfig {

    public static final ConfigurateLoader<?, ?> CONFIGURATE_LOADER = ConfigurateLoader.yamlLoader()
            .withAllDefaultSerializers()
            .build();

    @Setting("allow-pvp")
    private boolean allowPvP = true;

    private @Nullable Component nightSkipMessage = Component.translatable("cu.message.night-skip");

    private CloudUtilsConfig() {
    }

    public boolean isAllowPvP() {
        return this.allowPvP;
    }

    public void setAllowPvP(boolean allowPvP) {
        this.allowPvP = allowPvP;
    }

    public @Nullable Component getNightSkipMessage() {
        return this.nightSkipMessage;
    }

    public void setNightSkipMessage(@Nullable Component nightSkipMessage) {
        this.nightSkipMessage = nightSkipMessage;
    }
}
