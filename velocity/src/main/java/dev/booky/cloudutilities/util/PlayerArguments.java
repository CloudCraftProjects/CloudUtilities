package dev.booky.cloudutilities.util;
// Created by booky10 in CloudUtilities (14:24 18.07.21)

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.UuidUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.CommandDispatcher.ARGUMENT_SEPARATOR_CHAR;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.velocitypowered.api.command.VelocityBrigadierMessage.tooltip;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public final class PlayerArguments {

    private PlayerArguments() {
    }

    public static SuggestionProvider<CommandSource> playerSuggestions(ProxyServer server) {
        return (ctx, builder) -> CompletableFuture.supplyAsync(() -> {
            String rawLcInput = builder.getRemainingLowerCase();
            int lastSpaceIdx = rawLcInput.lastIndexOf(ARGUMENT_SEPARATOR_CHAR);
            String lcInput = lastSpaceIdx == -1 ? rawLcInput : rawLcInput.substring(lastSpaceIdx + 1);

            for (Player player : server.getAllPlayers()) {
                String username = player.getUsername();
                if (username.regionMatches(true,
                        0, lcInput, 0, lcInput.length())) {
                    builder.suggest(username);
                }
            }

            return builder.build();
        });
    }

    public static Player getPlayer(ProxyServer server, CommandContext<?> ctx, String name) throws CommandSyntaxException {
        return parse(server, getString(ctx, name));
    }

    public static Player parse(ProxyServer server, String input) throws CommandSyntaxException {
        return Optional.<Player>empty()
                .or(() -> {
                    try {
                        return server.getPlayer(UUID.fromString(input));
                    } catch (IllegalArgumentException ignored) {
                        return Optional.empty();
                    }
                })
                .or(() -> {
                    try {
                        return server.getPlayer(UuidUtils.fromUndashed(input));
                    } catch (IllegalArgumentException ignored) {
                        return Optional.empty();
                    }
                })
                .or(() -> server.getPlayer(input))
                .orElseThrow(() -> new SimpleCommandExceptionType(
                        tooltip(translatable("cu.error.unknown-player", text(input))))
                        .create());
    }
}
