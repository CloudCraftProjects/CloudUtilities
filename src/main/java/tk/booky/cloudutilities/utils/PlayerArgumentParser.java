package tk.booky.cloudutilities.utils;
// Created by booky10 in CloudUtilities (14:24 18.07.21)

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.UuidUtils;

import java.util.Optional;
import java.util.UUID;

public class PlayerArgumentParser {

    public static ProxyServer server;

    public static Player getPlayer(CommandContext<?> context, String name) throws CommandSyntaxException {
        return parse(StringArgumentType.getString(context, name)).orElse(null);
    }

    public static Optional<Player> parse(String input) throws CommandSyntaxException {
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
