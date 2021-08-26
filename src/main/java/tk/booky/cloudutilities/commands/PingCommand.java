package tk.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (14:21 18.07.21)

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import tk.booky.cloudutilities.utils.PlayerArgumentParser;
import tk.booky.cloudutilities.utils.Constants;

public class PingCommand {

    public static BrigadierCommand create() {
        return new BrigadierCommand(
            LiteralArgumentBuilder.<CommandSource>
                literal("ping")
                .then(
                    RequiredArgumentBuilder.<CommandSource, String>
                        argument("target", StringArgumentType.word())
                        .executes(context -> execute(context.getSource(), PlayerArgumentParser.getPlayer(context, "target")))
                )
                .requires(source -> source instanceof Player)
                .executes(context -> execute(context.getSource(), (Player) context.getSource()))
        );
    }

    private static int execute(CommandSource sender, Player target) throws CommandSyntaxException {
        if (target == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create();
        } else {
            sender.sendMessage(
                Constants.PREFIX
                    .append(Component.text("Player ", NamedTextColor.GREEN))
                    .append(Component.text(target.getUsername(), NamedTextColor.WHITE))
                    .append(Component.text(" has a ping of ", NamedTextColor.GREEN))
                    .append(Component.text(target.getPing(), getColorPing(target.getPing())))
                    .append(Component.text(" milliseconds.", NamedTextColor.GREEN))
            );
            return 1;
        }
    }

    private static NamedTextColor getColorPing(long ping) {
        return
            ping < 50 ?
                NamedTextColor.GREEN :
                ping < 100 ?
                    NamedTextColor.YELLOW :
                    ping < 150 ?
                        NamedTextColor.GOLD :
                        ping < 200 ?
                            NamedTextColor.RED :
                            NamedTextColor.DARK_RED;
    }
}
