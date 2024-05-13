package dev.booky.cloudutilities.bukkit.commands;
// Created by booky10 in CloudUtilities (17:15 11.05.2024.)

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.kyori.adventure.util.TriState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.players;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@Singleton
public class FlyCommand extends AbstractCommand {

    @Inject
    public FlyCommand() {
        super("fly");
    }

    @Override
    protected LiteralCommandNode<CommandSourceStack> buildNode() {
        return literal(this.getLabel())
                .requires(source -> source.getSender().hasPermission(this.getPermission()))
                .executes(ctx -> this.execute(ctx, false, false))
                .then(argument("state", bool())
                        .executes(ctx -> this.execute(ctx, false, true))
                        .then(argument("targets", players())
                                .requires(source -> source.getSender().hasPermission(this.getPermission("other")))
                                .executes(ctx -> this.execute(ctx, true, true))))
                .then(argument("targets", players())
                        .requires(source -> source.getSender().hasPermission(this.getPermission("other")))
                        .executes(ctx -> this.execute(ctx, true, false)))
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx, boolean withTargets, boolean withState) throws CommandSyntaxException {
        List<Player> targets = withTargets
                ? ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource())
                : ctx.getSource().getExecutor() == null ? List.of() : List.of((Player) ctx.getSource().getExecutor());
        TriState state = !withState ? TriState.NOT_SET : TriState.byBoolean(getBool(ctx, "state"));
        return this.set(ctx.getSource().getSender(), targets, state);
    }

    public int set(CommandSender sender, Collection<Player> targets, TriState state) {
        for (Player target : targets) {
            boolean newState = state.toBooleanOrElseGet(() -> !target.getAllowFlight());
            if (target.getAllowFlight() == newState) {
                sender.sendMessage(translatable("cu.command.fly.already-set",
                        target.teamDisplayName(), text(newState)));
                continue;
            }

            target.setAllowFlight(newState);
            sender.sendMessage(translatable("cu.command.fly.success",
                    target.teamDisplayName(), text(newState)));

            if (newState && !target.isOnGround()) {
                target.setFlying(true);
            }
        }
        return targets.size();
    }
}
