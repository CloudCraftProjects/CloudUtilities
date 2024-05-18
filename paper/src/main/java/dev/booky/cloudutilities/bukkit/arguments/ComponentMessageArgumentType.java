package dev.booky.cloudutilities.bukkit.arguments;
// Created by booky10 in CloudUtilities (18:05 18.05.2024.)

import com.mojang.brigadier.arguments.ArgumentType;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.command.brigadier.argument.SignedMessageResolver;
import net.kyori.adventure.text.Component;

import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.signedMessage;
import static net.kyori.adventure.text.Component.text;

public class ComponentMessageArgumentType implements CustomArgumentType.Converted<Component, SignedMessageResolver> {

    public static final ComponentMessageArgumentType INSTANCE = new ComponentMessageArgumentType();

    private ComponentMessageArgumentType() {
    }

    public static ComponentMessageArgumentType componentMessage() {
        return INSTANCE;
    }

    @Override
    public Component convert(SignedMessageResolver nativeType) {
        return text(nativeType.content());
    }

    @Override
    public ArgumentType<SignedMessageResolver> getNativeType() {
        return signedMessage();
    }
}
