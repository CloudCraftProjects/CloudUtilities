package tk.booky.cloudutilities.arguments;
// Created by booky10 in CloudUtilities (14:24 18.07.21)

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.UuidUtils;

import java.util.Optional;
import java.util.UUID;

public class PlayerArgumentType implements ArgumentType<String> {

    private static final PlayerArgumentType PLAYER_ARGUMENT = new PlayerArgumentType();
    public static ProxyServer server;

    private PlayerArgumentType() {
    }

    public static PlayerArgumentType player() {
        return PLAYER_ARGUMENT;
    }

    public static Player getPlayer(CommandContext<?> context, String name) {
        return server.getPlayer(UUID.fromString(context.getArgument(name, String.class))).orElse(null);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String entered = reader.readUnquotedString();
        Optional<Player> target;

        try {
            if ((target = server.getPlayer(UUID.fromString(entered))).isEmpty()) {
                throw new IllegalArgumentException();
            } else {
                entered = target.get().getUniqueId().toString();
            }
        } catch (IllegalArgumentException exception1) {
            try {
                if ((target = server.getPlayer(UuidUtils.fromUndashed(entered))).isEmpty()) {
                    throw new IllegalArgumentException();
                } else {
                    entered = target.get().getUniqueId().toString();
                }
            } catch (IllegalArgumentException exception2) {
                if ((target = server.getPlayer(entered)).isEmpty()) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
                } else {
                    entered = target.get().getUniqueId().toString();
                }
            }
        }

        return entered;
    }

    @Override
    public String toString() {
        return "string()";
    }
}
