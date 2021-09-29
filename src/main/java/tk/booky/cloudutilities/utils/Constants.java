package tk.booky.cloudutilities.utils;
// Created by booky10 in CloudUtilities (14:37 18.07.21)

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Constants {

    public static final Component PREFIX = Component.text()
        .append(Component.text('[', NamedTextColor.GRAY))
        .append(Component.text('C', NamedTextColor.WHITE, TextDecoration.BOLD))
        .append(Component.text('U', NamedTextColor.AQUA, TextDecoration.BOLD))
        .append(Component.text(']', NamedTextColor.GRAY))
        .append(Component.space())
        .build();

    public static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
