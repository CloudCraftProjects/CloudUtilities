package dev.booky.cloudutilities.bukkit.commands;
// Created by booky10 in CloudUtilities (04:13 11.05.2024.)

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractCommand {

    protected static final String COMMAND_PERMISSION_PREFIX = "cu.command.";

    private final String label;
    private final List<String> aliases;
    private final List<String> allAliases;

    protected AbstractCommand(String label, String... aliases) {
        this.label = label;
        this.aliases = List.of(aliases);
        this.allAliases = Stream.concat(Stream.of(label),
                this.aliases.stream()).toList();
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

    protected abstract CommandTree buildTree();

    public void unregister() {
        for (String alias : this.allAliases) {
            CommandAPI.unregister(alias, true);
        }
    }

    public void register(JavaPlugin plugin) {
        this.unregister(); // just to be safe
        this.buildTree()
                .withAliases(this.aliases.toArray(new String[0]))
                .register(plugin);
    }

    public String getLabel() {
        return this.label;
    }

    public List<String> getAliases() {
        return this.aliases;
    }
}
