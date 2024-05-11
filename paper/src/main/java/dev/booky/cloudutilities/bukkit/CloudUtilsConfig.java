package dev.booky.cloudutilities.bukkit;
// Created by booky10 in CloudUtilities (04:08 11.05.2024.)

import dev.booky.cloudcore.config.ConfigurateLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class CloudUtilsConfig {

    public static final ConfigurateLoader<?, ?> CONFIGURATE_LOADER = ConfigurateLoader.yamlLoader()
            .withAllDefaultSerializers()
            .build();

    private CloudUtilsConfig() {
    }
}
