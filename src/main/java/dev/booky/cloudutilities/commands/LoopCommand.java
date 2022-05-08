package dev.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (12:31 19.07.21)

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.booky.cloudutilities.util.Utilities;
import net.kyori.adventure.util.Ticks;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static dev.booky.cloudutilities.util.Utilities.argument;
import static dev.booky.cloudutilities.util.Utilities.literal;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public class LoopCommand {

    public static BrigadierCommand create(Object plugin, ProxyServer server) {
        return new BrigadierCommand(literal("loop")
            .requires(source -> source.hasPermission("cu.command.loop"))
            .then(argument("times", longArg(1))
                .then(argument("interval", longArg(1))
                    .then(argument("command", greedyString())
                        .executes(context -> execute(plugin, server, context.getSource(), getLong(context, "times"), getLong(context, "interval"), getString(context, "command")))))));
    }

    private static int execute(Object plugin, ProxyServer server, CommandSource sender, long times, long interval, String command) {
        AtomicReference<ScheduledTask> task = new AtomicReference<>();
        AtomicInteger timesRan = new AtomicInteger(0);

        Runnable fallbackExecute = sender instanceof Player ?
            () -> ((Player) sender).spoofChatInput(command) : null;

        Runnable runnable = () -> {
            boolean cancelTask = timesRan.incrementAndGet() >= times;

            try {
                boolean success = server.getCommandManager().executeImmediatelyAsync(sender, command).join();
                if (!success && fallbackExecute != null) fallbackExecute.run();
            } catch (Throwable exception) {
                if (!cancelTask) cancelTask = true;
            }

            if (!cancelTask) return;
            ScheduledTask current = task.get();
            if (current != null) {
                current.cancel();
            }
        };

        task.set(server.getScheduler().buildTask(plugin, runnable).repeat(Ticks.duration(interval)).schedule());
        sender.sendMessage(Utilities.PREFIX.append(text("The task has been scheduled", GREEN)));

        return 1;
    }
}
