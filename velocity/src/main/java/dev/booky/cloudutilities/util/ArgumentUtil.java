package dev.booky.cloudutilities.util;
// Created by booky10 in CloudUtilities (14:24 18.07.21)

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.UuidUtils;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ArgumentUtil {

    private ArgumentUtil() {
    }

    public static SuggestionProvider<CommandSource> suggestPlayer(ProxyServer server, String argument) {
        return (ctx, builder) -> CompletableFuture.supplyAsync(() -> {
            String rawLcInput = builder.getRemainingLowerCase();
            int lastSpaceIdx = rawLcInput.lastIndexOf(CommandDispatcher.ARGUMENT_SEPARATOR_CHAR);
            String lcInput = lastSpaceIdx == -1 ? rawLcInput : rawLcInput.substring(lastSpaceIdx + 1);

            for (Player player : server.getAllPlayers()) {
                String lcUsername = player.getUsername().toLowerCase(Locale.ROOT);
                if (lcUsername.startsWith(lcInput)) {
                    builder.suggest(lcUsername);
                }
            }

            return builder.build();
        });
    }

    public static Player getPlayer(ProxyServer server, CommandContext<?> context, String name) throws CommandSyntaxException {
        return parse(server, StringArgumentType.getString(context, name));
    }

    public static Player parse(ProxyServer server, String input) throws CommandSyntaxException {
        Optional<Player> target = Optional.empty();

        try {
            target = server.getPlayer(UUID.fromString(input));
        } catch (IllegalArgumentException ignored) {
        }

        if (target.isEmpty()) {
            try {
                target = server.getPlayer(UuidUtils.fromUndashed(input));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (target.isEmpty()) {
            target = server.getPlayer(input);
        }

        return target.orElseThrow(() -> CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create());
    }
}
