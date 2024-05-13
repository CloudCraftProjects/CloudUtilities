package dev.booky.cloudutilities.bukkit;
// Created by booky10 in CloudUtilities (04:01 11.05.2024.)

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.booky.cloudcore.i18n.CloudTranslator;
import dev.booky.cloudutilities.bukkit.commands.AbstractCommand;
import dev.booky.cloudutilities.bukkit.commands.AllowPvPCommand;
import dev.booky.cloudutilities.bukkit.commands.FlyCommand;
import dev.booky.cloudutilities.bukkit.commands.VanillaMsgCommand;
import dev.booky.cloudutilities.bukkit.injection.CloudUtilModule;
import dev.booky.cloudutilities.bukkit.listener.PvPListener;
import dev.booky.cloudutilities.bukkit.listener.SleepListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.List;
import java.util.Locale;

public class CloudUtilitiesMain extends JavaPlugin {

    private static final List<Locale> SUPPORTED_LOCALES = List.of(
            Locale.ENGLISH, Locale.GERMAN
    );

    private static final List<Class<? extends AbstractCommand>> COMMAND_CLASSES = List.of(
            AllowPvPCommand.class, FlyCommand.class, VanillaMsgCommand.class
    );
    private static final List<Class<? extends Listener>> LISTENER_CLASSES = List.of(
            PvPListener.class, SleepListener.class
    );

    private final Injector injector;

    private @MonotonicNonNull CloudTranslator i18n;
    private @MonotonicNonNull CloudUtilsManager manager;

    public CloudUtilitiesMain() {
        CloudUtilModule module = new CloudUtilModule(this);
        this.injector = Guice.createInjector(module);
    }

    @Override
    public void onLoad() {
        this.i18n = new CloudTranslator(this.getClassLoader(),
                new NamespacedKey(this, "i18n"),
                SUPPORTED_LOCALES);
        this.i18n.load();

        this.manager = this.injector.getInstance(CloudUtilsManager.class);
        Bukkit.getServicesManager().register(CloudUtilsManager.class,
                this.manager, this, ServicePriority.Normal);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            for (Class<? extends AbstractCommand> commandClass : COMMAND_CLASSES) {
                AbstractCommand command = this.injector.getInstance(commandClass);
                command.register(event.registrar(), this);
            }
        });
    }

    @Override
    public void onEnable() {
        for (Class<? extends Listener> listenerClass : LISTENER_CLASSES) {
            Listener listener = this.injector.getInstance(listenerClass);
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable() {
        if (this.i18n != null) {
            this.i18n.unload();
        }
    }

    public Injector getInjector() {
        return this.injector;
    }

    public CloudUtilsManager getManager() {
        return this.manager;
    }
}
