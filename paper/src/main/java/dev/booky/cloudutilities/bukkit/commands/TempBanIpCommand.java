package dev.booky.cloudutilities.bukkit.commands;
// Created by booky10 in CloudUtilities (17:29 18.05.2024.)

import com.google.common.net.InetAddresses;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.ban.BanListType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.ban.IpBanList;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent.Cause;
import org.bukkit.util.NumberConversions;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;

import static dev.booky.cloudcore.commands.AddressArgumentType.address;
import static dev.booky.cloudcore.commands.CommandUtil.buildExceptionType;
import static dev.booky.cloudcore.commands.ComponentMessageArgumentType.componentMessage;
import static dev.booky.cloudcore.commands.DurationArgumentType.duration;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.MessageComponentSerializer.message;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@Singleton
public class TempBanIpCommand extends AbstractCommand {

    private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED =
            buildExceptionType(translatable("commands.banip.failed"));

    @Inject
    public TempBanIpCommand() {
        super("tempban-ip", "tban-ip");
    }

    @Override
    protected LiteralCommandNode<CommandSourceStack> buildNode() {
        return literal(this.getLabel())
                .requires(source -> source.getSender().hasPermission(this.getPermission()))
                .then(argument("target", address())
                        .then(argument("duration", duration())
                                .executes(this::executeNoReason)
                                .then(argument("reason", componentMessage())
                                        .executes(this::execute))))
                .build();
    }

    private int executeNoReason(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        InetAddress address = ctx.getArgument("target", InetAddress.class);
        Duration duration = ctx.getArgument("duration", Duration.class);
        return this.execute(ctx.getSource(), address, duration, null);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        InetAddress address = ctx.getArgument("target", InetAddress.class);
        Duration duration = ctx.getArgument("duration", Duration.class);
        Component reason = ctx.getArgument("reason", Component.class);
        return this.execute(ctx.getSource(), address, duration, reason);
    }

    public int execute(
            CommandSourceStack source,
            InetAddress address,
            Duration duration,
            @Nullable Component reason
    ) throws CommandSyntaxException {
        IpBanList banList = Bukkit.getBanList(BanListType.IP);
        if (banList.isBanned(address)) {
            throw ERROR_ALREADY_BANNED.create();
        }

        String stringReason = reason == null ? null : message().serialize(reason).getString();
        String sourceName = source.getExecutor() == null ? source.getSender().getName() : source.getExecutor().getName();

        BanEntry<InetAddress> entry = banList.addBan(address, stringReason, duration, sourceName);
        if (entry == null) {
            return 0; // somehow failed to ban
        }

        ComponentBuilder<?, ?> playerNames = text();
        for (Player player : Bukkit.getOnlinePlayers()) {
            InetSocketAddress playerAddress = player.getAddress();
            if (playerAddress == null || !address.equals(playerAddress.getAddress())) {
                continue;
            }

            if (!playerNames.children().isEmpty()) {
                playerNames.append(text(", "));
            }
            playerNames.applicableApply(player.teamDisplayName());
            player.kick(translatable("multiplayer.disconnect.ip_banned"), Cause.IP_BANNED);
        }
        int affectedCount = NumberConversions.ceil(playerNames.children().size() / 2d);

        source.getSender().sendMessage(translatable("commands.banip.success",
                text(InetAddresses.toAddrString(address)),
                text(String.valueOf(entry.getReason()))));
        if (affectedCount > 0) {
            source.getSender().sendMessage(translatable("commands.banip.info",
                    text(affectedCount), playerNames));
        }

        return affectedCount;
    }
}
