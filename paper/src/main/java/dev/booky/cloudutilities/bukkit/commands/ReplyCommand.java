package dev.booky.cloudutilities.bukkit.commands;
// Created by booky10 in CloudUtilities (23:02 13.05.2024.)

import com.google.common.collect.MapMaker;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.SignedMessageResolver;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static dev.booky.cloudcore.commands.CommandUtil.buildException;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.signedMessage;
import static net.kyori.adventure.text.Component.translatable;

@Singleton
public class ReplyCommand extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger("CloudUtilities");

    private final Provider<VanillaMsgCommand> msgCommand;

    private final Map<Player, Player> replyTargets = new MapMaker().weakKeys().weakValues().makeMap();

    @Inject
    public ReplyCommand(Provider<VanillaMsgCommand> msgCommand) {
        super("reply", "r");
        this.msgCommand = msgCommand;
    }

    @Override
    protected LiteralCommandNode<CommandSourceStack> buildNode() {
        return literal(this.getLabel())
                .requires(source -> source.getExecutor() instanceof Player
                        && source.getSender().hasPermission(this.getPermission()))
                .then(argument("message", signedMessage())
                        .executes(this::execute))
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!(ctx.getSource().getExecutor() instanceof Player player)) {
            return 0;
        }
        Player target = this.getReplyTarget(player);
        if (target == null) {
            throw buildException(translatable("cu.command.reply.no-target"));
        }

        SignedMessageResolver messageResolver = ctx.getArgument("message", SignedMessageResolver.class);
        messageResolver.resolveSignedMessage("message", ctx)
                .thenAccept(message -> this.msgCommand.get()
                        .sendMessage(ctx.getSource(), List.of(target), message))
                .exceptionally(error -> {
                    LOGGER.error("Error while resolving signed chat message {} from {}",
                            messageResolver, ctx.getSource(), error);
                    return null;
                });
        return Command.SINGLE_SUCCESS;
    }

    public void setReplyTarget(Player player, Player target) {
        this.replyTargets.put(player, target);
    }

    public @Nullable Player getReplyTarget(Player player) {
        return this.replyTargets.get(player);
    }
}
