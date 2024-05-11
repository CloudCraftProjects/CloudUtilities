package dev.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (14:21 18.07.21)

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.format.TextColor;

import java.util.function.LongPredicate;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static dev.booky.cloudutilities.util.PlayerArguments.getPlayer;
import static dev.booky.cloudutilities.util.PlayerArguments.playerSuggestions;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_RED;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

@Singleton
public class PingCommand extends AbstractCommand {

    private final ProxyServer server;

    @Inject
    public PingCommand(ProxyServer server) {
        super("ping", "latency");
        this.server = server;
    }

    @Override
    public LiteralCommandNode<CommandSource> buildNode() {
        return literal(this.getLabel())
                .requires(source -> source.hasPermission(this.getPermission()))
                .executes(ctx -> this.sendPing(ctx.getSource(), (Player) ctx.getSource()))
                .then(argument("target", word())
                        .requires(source -> source.hasPermission(this.getPermission("other")))
                        .suggests(playerSuggestions(this.server))
                        .executes(ctx -> this.sendPing(ctx.getSource(),
                                getPlayer(this.server, ctx, "target"))))
                .build();
    }

    public int sendPing(CommandSource sender, Player target) {
        long ping = target.getPing();
        PingLevel pingLevel = PingLevel.determine(ping);

        sender.sendMessage(translatable("cu.command.ping",
                text(target.getUsername()),
                text(ping, pingLevel.getColor())));
        return 1;
    }

    private enum PingLevel implements LongPredicate {

        GOOD(50, GREEN),
        MEDIUM(100, YELLOW),
        BAD(150, GOLD),
        VERY_BAD(200, RED),
        WTF(Integer.MAX_VALUE, DARK_RED);

        private final int maxPing;
        private final TextColor color;

        PingLevel(int maxPing, TextColor color) {
            this.maxPing = maxPing;
            this.color = color;
        }

        public static PingLevel determine(long ping) {
            for (PingLevel level : values()) {
                if (level.test(ping)) {
                    return level;
                }
            }
            return WTF;
        }

        @Override
        public boolean test(long value) {
            return value <= this.maxPing;
        }

        public TextColor getColor() {
            return this.color;
        }
    }
}
