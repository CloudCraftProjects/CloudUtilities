package dev.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (02:30 11.05.2024.)

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;

import java.util.List;

public abstract class AbstractCommand {

    protected static final String COMMAND_PERMISSION_PREFIX = "cu.command.";

    private final String label;
    private final List<String> aliases;

    protected AbstractCommand(String label, String... aliases) {
        this.label = label;
        this.aliases = List.of(aliases);
    }

    protected static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public String getPermission() {
        return COMMAND_PERMISSION_PREFIX + this.label;
    }

    public String getPermission(String extra) {
        return COMMAND_PERMISSION_PREFIX + this.label + '.' + extra;
    }

    public String getPermission(String... extras) {
        StringBuilder permBuilder = new StringBuilder(COMMAND_PERMISSION_PREFIX).append(this.label);
        for (String extra : extras) {
            permBuilder.append('.').append(extra);
        }
        return permBuilder.toString();
    }

    public abstract LiteralCommandNode<CommandSource> buildNode();

    public void register(CommandManager commands, Object plugin) {
        LiteralCommandNode<CommandSource> node = this.buildNode();
        CommandMeta commandMeta = commands
                .metaBuilder(this.label)
                .aliases(this.aliases.toArray(new String[0]))
                .plugin(plugin)
                .build();

        BrigadierCommand command = new BrigadierCommand(node);
        commands.register(commandMeta, command);
    }

    public String getLabel() {
        return this.label;
    }

    public List<String> getAliases() {
        return this.aliases;
    }
}
