package tk.booky.cloudutilities.utils;
// Created by booky10 in CloudUtilities (14:37 18.07.21)

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Constants {

    public static final MinecraftChannelIdentifier BRAND_IDENTIFIER = MinecraftChannelIdentifier.forDefaultNamespace("brand");
    public static final byte[] BRAND_MESSAGE = StringSerializer.fromString("CloudCraft");

    public static String VERSION_FROM = ProtocolVersion.MINECRAFT_1_13_2.getVersionIntroducedIn();
    public static String VERSION_TO = ProtocolVersion.MAXIMUM_VERSION.getMostRecentSupportedVersion();
    public static String VERSION_RANGE = VERSION_FROM + " - " + VERSION_TO;
    public static String VERSION = "CloudCraft " + VERSION_RANGE;

    public static final Component PREFIX = Component.text()
        .append(Component.text('[', NamedTextColor.GRAY))
        .append(Component.text('C', NamedTextColor.WHITE, TextDecoration.BOLD))
        .append(Component.text('U', NamedTextColor.AQUA, TextDecoration.BOLD))
        .append(Component.text(']', NamedTextColor.GRAY))
        .build();
}
