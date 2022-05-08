package dev.booky.cloudutilities.util;
// Created by booky10 in CloudUtilities (14:24 18.07.21)

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.UuidUtils;

import java.util.Optional;
import java.util.UUID;

public class ArgumentUtil {

    public static SuggestionProvider<CommandSource> suggestPlayer(ProxyServer server, String argument) {
        return (context, builder) -> {
            ParsedArgument<?, ?> parsed = context.getArguments().get(argument);
            if (parsed == null) return builder.buildFuture();

            String target = (String) parsed.getResult();
            for (Player player : server.getAllPlayers()) {
                if (!target.isBlank() && !player.getUsername().toLowerCase().startsWith(target.toLowerCase())) continue;
                builder.suggest(player.getUsername());
            }

            return builder.buildFuture();
        };
    }

    public static Player getPlayer(ProxyServer server, CommandContext<?> context, String name) throws CommandSyntaxException {
        return parse(server, StringArgumentType.getString(context, name)).orElse(null);
    }

    public static Optional<Player> parse(ProxyServer server, String input) throws CommandSyntaxException {
        Optional<Player> target;

        try {
            if ((target = server.getPlayer(UUID.fromString(input))).isEmpty()) {
                throw new IllegalArgumentException();
            } else {
                input = target.get().getUniqueId().toString();
            }
        } catch (IllegalArgumentException exception1) {
            try {
                if ((target = server.getPlayer(UuidUtils.fromUndashed(input))).isEmpty()) {
                    throw new IllegalArgumentException();
                } else {
                    input = target.get().getUniqueId().toString();
                }
            } catch (IllegalArgumentException exception2) {
                if ((target = server.getPlayer(input)).isEmpty()) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
                }
            }
        }

        return target;
    }
}
