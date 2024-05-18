package dev.booky.cloudutilities.bukkit.commands;
// Created by booky10 in CloudUtilities (04:13 11.05.2024.)

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public abstract class AbstractCommand {

    protected static final String COMMAND_PERMISSION_PREFIX = "cu.command.";

    private final String label;
    private final List<String> aliases;

    protected AbstractCommand(String label, String... aliases) {
        this.label = label;
        this.aliases = List.of(aliases);
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

    protected abstract LiteralCommandNode<CommandSourceStack> buildNode();

    public void register(Commands registrar, Plugin plugin) {
        registrar.register(plugin.getPluginMeta(), this.buildNode(), null, this.aliases);
    }

    public String getLabel() {
        return this.label;
    }

    public List<String> getAliases() {
        return this.aliases;
    }
}
