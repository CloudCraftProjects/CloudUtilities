package dev.booky.cloudutilities.commands;
// Created by booky10 in CloudUtilities (12:31 19.07.21)

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.booky.cloudutilities.CloudUtilitiesMain;
import dev.booky.cloudutilities.util.Utilities;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.util.Ticks.duration;

@Singleton
public class LoopCommand extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger("CloudUtilities");

    private final ProxyServer server;
    private final Object plugin;

    @Inject
    public LoopCommand(ProxyServer server, CloudUtilitiesMain plugin) {
        super("loop");
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public LiteralCommandNode<CommandSource> buildNode() {
        return literal(this.getLabel())
                .requires(source -> source.hasPermission(this.getPermission()))
                .then(argument("times", longArg(1))
                        .then(argument("intervalTicks", longArg(1))
                                .then(argument("input", greedyString())
                                        .executes(ctx -> this.startLooping(ctx.getSource(),
                                                getLong(ctx, "times"),
                                                getLong(ctx, "intervalTicks"),
                                                getString(ctx, "input"))))))
                .build();
    }

    public int startLooping(
            CommandSource sender,
            long times, long interval, String input
    ) {
        AtomicInteger timesRan = new AtomicInteger(0);
        Consumer<ScheduledTask> runnable = task -> {
            if (sender instanceof Player && !((Player) sender).isActive()) {
                // cancel task instantly when player goes offline
                task.cancel();
                return;
            }

            this.server.getCommandManager()
                    .executeAsync(sender, input)
                    .thenAccept(success -> {
                        if (!success && sender instanceof Player) {
                            ((Player) sender).spoofChatInput("/" + input);
                        }
                    })
                    .exceptionally(error -> {
                        LOGGER.error("Error while executing loop for {} with input '{}'",
                                sender, input, error);
                        return null;
                    });

            if (timesRan.incrementAndGet() >= times) {
                task.cancel();
            }
        };

        this.server.getScheduler()
                .buildTask(this.plugin, runnable)
                .repeat(duration(interval))
                .schedule();
        sender.sendMessage(Utilities.PREFIX.append(text("The task has been scheduled", GREEN)));

        return 1;
    }
}
