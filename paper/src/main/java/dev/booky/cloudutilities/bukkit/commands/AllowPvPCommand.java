package dev.booky.cloudutilities.bukkit.commands;
// Created by booky10 in CloudUtilities (04:17 11.05.2024.)

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.booky.cloudutilities.bukkit.CloudUtilsManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.command.CommandSender;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@Singleton
public class AllowPvPCommand extends AbstractCommand {

    private final CloudUtilsManager manager;

    @Inject
    public AllowPvPCommand(CloudUtilsManager manager) {
        super("allowpvp");
        this.manager = manager;
    }

    @Override
    protected LiteralCommandNode<CommandSourceStack> buildNode() {
        return literal(this.getLabel())
                .requires(source -> source.getSender().hasPermission(this.getPermission()))
                .executes(this::executeToggle)
                .then(argument("active", bool())
                        .executes(this::executeSet))
                .build();
    }

    private int executeToggle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        this.toggle(ctx.getSource().getSender());
        return Command.SINGLE_SUCCESS;
    }

    private int executeSet(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        boolean state = getBool(ctx, "active");
        this.set(ctx.getSource().getSender(), state);
        return Command.SINGLE_SUCCESS;
    }

    public void toggle(CommandSender sender) throws CommandSyntaxException {
        boolean newState = !this.manager.getConfig().isAllowPvP();
        this.set(sender, newState);
    }

    public void set(CommandSender sender, boolean state) throws CommandSyntaxException {
        if (this.manager.getConfig().isAllowPvP() == state) {
            throw buildException(translatable("cu.command.allowpvp.already-set", text(state)));
        }
        this.manager.updateConfig(config -> config.setAllowPvP(state));
        sender.sendMessage(translatable("cu.command.allowpvp.success", text(state)));
    }
}
