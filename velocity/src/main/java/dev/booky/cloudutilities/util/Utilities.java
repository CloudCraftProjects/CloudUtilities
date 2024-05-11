package dev.booky.cloudutilities.util;
// Created by booky10 in CloudUtilities (14:37 18.07.21)

import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public final class Utilities {

    public static final Component PREFIX = text()
            .append(text('[', GRAY))
            .append(text('C', WHITE, BOLD))
            .append(text('U', AQUA, BOLD))
            .append(text(']', GRAY))
            .append(space())
            .build();

    private Utilities() {
    }
}
