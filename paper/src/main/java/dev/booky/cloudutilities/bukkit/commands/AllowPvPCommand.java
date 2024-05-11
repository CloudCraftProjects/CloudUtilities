package dev.booky.cloudutilities.bukkit.commands;
// Created by booky10 in CloudUtilities (04:17 11.05.2024.)

import dev.booky.cloudutilities.bukkit.CloudUtilsManager;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import dev.jorel.commandapi.wrappers.NativeProxyCommandSender;
import org.bukkit.command.CommandSender;

import static dev.jorel.commandapi.CommandAPIBukkit.failWithAdventureComponent;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public class AllowPvPCommand extends AbstractCommand {

    private final CloudUtilsManager manager;

    public AllowPvPCommand(CloudUtilsManager manager) {
        super("allowpvp");
        this.manager = manager;
    }

    @Override
    protected CommandTree buildTree() {
        return new CommandTree(this.getLabel())
                .withPermission(this.getPermission())
                .executesNative(this::executeToggle)
                .then(new BooleanArgument("active")
                        .executesNative(this::executeSet));
    }

    private void executeToggle(NativeProxyCommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        this.toggle(sender);
    }

    private void executeSet(NativeProxyCommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException {
        boolean state = args.getOptionalByClass("active",
                boolean.class).orElseThrow();
        this.set(sender, state);
    }

    public void toggle(CommandSender sender) throws WrapperCommandSyntaxException {
        boolean newState = !this.manager.getConfig().isAllowPvP();
        this.set(sender, newState);
    }

    public void set(CommandSender sender, boolean state) throws WrapperCommandSyntaxException {
        if (this.manager.getConfig().isAllowPvP() == state) {
            throw failWithAdventureComponent(translatable(
                    "cu.command.allowpvp.already-set", text(state)));
        }
        this.manager.updateConfig(config -> config.setAllowPvP(state));
        sender.sendMessage(translatable("cu.command.allowpvp.success", text(state)));
    }
}
