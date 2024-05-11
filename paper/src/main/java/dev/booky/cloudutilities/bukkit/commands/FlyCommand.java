package dev.booky.cloudutilities.bukkit.commands;
// Created by booky10 in CloudUtilities (17:15 11.05.2024.)

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import net.kyori.adventure.util.TriState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class FlyCommand extends AbstractCommand {

    public FlyCommand() {
        super("fly");
    }

    @Override
    protected CommandTree buildTree() {
        return new CommandTree("fly")
                .withPermission(this.getPermission())
                .executesNative(this::execute)
                .then(new BooleanArgument("state")
                        .executesNative(this::execute)
                        .then(new EntitySelectorArgument.ManyPlayers("targets")
                                .withPermission(this.getPermission("other"))
                                .executesNative(this::execute)))
                .then(new EntitySelectorArgument.ManyPlayers("targets")
                        .withPermission(this.getPermission("other"))
                        .executesNative(this::execute));
    }

    private int execute(NativeProxyCommandSender sender, CommandArguments args) {
        Collection<Player> targets = args.<Collection<Player>>getOptionalUnchecked("targets")
                .orElseGet(() -> List.of((Player) sender.getCallee()));
        TriState state = args.<Boolean>getOptionalUnchecked("state")
                .map(TriState::byBoolean).orElse(TriState.NOT_SET);
        return this.set(sender, targets, state);
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
