package dev.booky.cloudutilities.bukkit.commands;
// Created by booky10 in CloudUtilities (18:26 15.05.2024.)

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.ban.BanListType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent.Cause;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.util.Collection;

import static dev.booky.cloudcore.commands.CommandUtil.buildExceptionType;
import static dev.booky.cloudcore.commands.ComponentMessageArgumentType.componentMessage;
import static dev.booky.cloudcore.commands.DurationArgumentType.duration;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.MessageComponentSerializer.message;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.playerProfiles;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@Singleton
public class TempBanCommand extends AbstractCommand {

    private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED =
            buildExceptionType(translatable("commands.ban.failed"));

    @Inject
    public TempBanCommand() {
        super("tempban", "tban");
    }

    @Override
    protected LiteralCommandNode<CommandSourceStack> buildNode() {
        return literal(this.getLabel())
                .requires(source -> source.getSender().hasPermission(this.getPermission()))
                .then(argument("targets", playerProfiles())
                        .then(argument("duration", duration())
                                .executes(this::executeNoReason)
                                .then(argument("reason", componentMessage())
                                        .executes(this::execute))))
                .build();
    }

    private int executeNoReason(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<PlayerProfile> targets = ctx.getArgument("targets", PlayerProfileListResolver.class)
                .resolve(ctx.getSource());
        Duration duration = ctx.getArgument("duration", Duration.class);
        return this.execute(ctx.getSource(), targets, duration, null);
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<PlayerProfile> targets = ctx.getArgument("targets", PlayerProfileListResolver.class)
                .resolve(ctx.getSource());
        Duration duration = ctx.getArgument("duration", Duration.class);
        Component reason = ctx.getArgument("reason", Component.class);
        return this.execute(ctx.getSource(), targets, duration, reason);
    }

    public int execute(
            CommandSourceStack source,
            Collection<PlayerProfile> targets,
            Duration duration,
            @Nullable Component reason
    ) throws CommandSyntaxException {
        String stringReason = reason == null ? null : message().serialize(reason).getString();
        String sourceName = source.getExecutor() == null ? source.getSender().getName() : source.getExecutor().getName();

        ProfileBanList banlist = Bukkit.getBanList(BanListType.PROFILE);
        int successCount = 0;
        for (PlayerProfile target : targets) {
            if (banlist.isBanned(target)) {
                continue; // skip
            }
            BanEntry<PlayerProfile> entry = banlist.addBan(target, stringReason, duration, sourceName);
            if (entry == null) {
                continue; // somehow failed to ban the target
            }
            successCount++;

            source.getSender().sendMessage(
                    translatable("commands.ban.success",
                            text(String.valueOf(target.getName())),
                            text(String.valueOf(entry.getReason())))
            );

            if (target.getId() != null) {
                Player targetPlayer = Bukkit.getPlayer(target.getId());
                if (targetPlayer != null) {
                    targetPlayer.kick(translatable("multiplayer.disconnect.banned"), Cause.BANNED);
                }
            }
        }

        if (successCount != 0) {
            return successCount;
        }
        throw ERROR_ALREADY_BANNED.create();
    }
}
