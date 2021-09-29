package tk.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (12:31 19.07.21)

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.Ticks;
import tk.booky.cloudutilities.utils.Constants;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static tk.booky.cloudutilities.utils.Constants.argument;
import static tk.booky.cloudutilities.utils.Constants.literal;

public class LoopCommand {

    public static BrigadierCommand create(Object plugin, ProxyServer server) {
        return new BrigadierCommand(literal("loop")
            .requires(source -> source.hasPermission("cu.command.loop"))
            .then(argument("times", LongArgumentType.longArg(1))
                .then(argument("interval", LongArgumentType.longArg(1))
                    .then(argument("command", StringArgumentType.greedyString())
                        .executes(context -> execute(plugin, server, context.getSource(), LongArgumentType.getLong(context, "times"), LongArgumentType.getLong(context, "interval"), StringArgumentType.getString(context, "command")))))));
    }

    private static int execute(Object plugin, ProxyServer server, CommandSource sender, long times, long interval, String command) {
        AtomicReference<ScheduledTask> task = new AtomicReference<>();
        AtomicInteger timesRan = new AtomicInteger(0);

        Runnable execute = sender instanceof Player ?
            () -> ((Player) sender).spoofChatInput(command) :
            () -> server.getCommandManager().executeImmediatelyAsync(sender, command);

        Runnable runnable = () -> {
            boolean cancelTask = timesRan.incrementAndGet() >= times;

            try {
                execute.run();
            } catch (IllegalStateException exception) {
                cancelTask = true;
            }

            if (cancelTask) {
                ScheduledTask current = task.get();
                if (current != null) {
                    current.cancel();
                }
            }
        };

        task.set(server.getScheduler().buildTask(plugin, runnable).repeat(Ticks.duration(interval)).schedule());
        sender.sendMessage(Constants.PREFIX.append(Component.text("The task has been scheduled!", NamedTextColor.GREEN)));

        return 1;
    }
}
