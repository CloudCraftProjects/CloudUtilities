package dev.booky.cloudutilities.bukkit.commands;
// Created by booky10 in CloudUtilities (19:14 13.05.2024.)

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.SignedMessageResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.players;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.signedMessage;

public final class VanillaMsgCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger("CloudUtilities");

    private static final Set<String> TELL_COMMANDS = Set.of("msg", "tell", "w");
    private static final Set<String> MINECRAFT_TELL_COMMANDS = TELL_COMMANDS.stream()
            .map(label -> NamespacedKey.MINECRAFT + ":" + label)
            .collect(Collectors.toUnmodifiableSet());

    private VanillaMsgCommand() {
    }

    public static <S> void inject(RootCommandNode<S> root, Commands registrar) {
        List<CommandNode<S>> namespacedNodes = MINECRAFT_TELL_COMMANDS.stream()
                .map(root::getChild)
                .filter(Objects::nonNull)
                .toList();
        List<CommandNode<S>> nodes = Stream.concat(namespacedNodes.stream(),
                        TELL_COMMANDS.stream()
                                .map(root::getChild)
                                .filter(Objects::nonNull)
                                // support plugins replacing vanilla commands and just cancel this logic
                                .filter(child -> namespacedNodes.stream()
                                        .anyMatch(node -> child.equals(node.getRedirect()))))
                .toList();

        List<String> labels = nodes.stream().map(CommandNode::getName).toList();
        if (labels.isEmpty()) {
            return;
        }
        String mainLabel = labels.getFirst();
        List<String> aliases = labels.subList(1, labels.size());

        LOGGER.info("Replacing {} vanilla message commands...", labels.size());
        root.getChildren().removeAll(nodes);
        registrar.register(literal(mainLabel)
                .then(argument("targets", players())
                        .then(argument("message", signedMessage())
                                .executes(VanillaMsgCommand::execute)))
                .build(), aliases);
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                .resolve(ctx.getSource());
        if (targets.isEmpty()) {
            return 0;
        }

        SignedMessageResolver messageResolver = ctx.getArgument("message", SignedMessageResolver.class);
        messageResolver.resolveSignedMessage("message", ctx)
                .thenAccept(message -> sendMessage(ctx.getSource(), targets, message))
                .exceptionally(error -> {
                    LOGGER.error("Error while resolving signed chat message {} from {}",
                            messageResolver, ctx.getSource(), error);
                    return null;
                });
        return targets.size();
    }

    private static void sendMessage(CommandSourceStack source, List<Player> targets, SignedMessage message) {
        Component sourceName = source.getExecutor() != null
                ? source.getExecutor().teamDisplayName()
                : source.getSender().name();
        ChatType.Bound incomingChat = ChatType.MSG_COMMAND_INCOMING.bind(sourceName);

        for (Player target : targets) {
            ChatType.Bound outgoingChat = ChatType.MSG_COMMAND_OUTGOING.bind(sourceName, target.teamDisplayName());
            source.getSender().sendMessage(message, outgoingChat);
            target.sendMessage(message, incomingChat);
        }
    }
}
