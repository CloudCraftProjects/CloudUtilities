package dev.booky.cloudutilities.bukkit.arguments;
// Created by booky10 in CloudUtilities (18:28 15.05.2024.)

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static io.papermc.paper.command.brigadier.MessageComponentSerializer.message;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

// adopted from https://github.com/LuckPerms/LuckPerms/blob/5c1ea5633ed5986c02d3fa1ccf187638d143c091/common/src/main/java/me/lucko/luckperms/common/util/DurationParser.java (MIT License)
public class DurationArgumentType implements CustomArgumentType.Converted<Duration, String> {

    private static final Map<ChronoUnit, String> UNITS_PATTERNS = ImmutableMap.<ChronoUnit, String>builder()
            .put(ChronoUnit.YEARS, "y(?:ear)?s?")
            .put(ChronoUnit.MONTHS, "mo(?:nth)?s?")
            .put(ChronoUnit.WEEKS, "w(?:eek)?s?")
            .put(ChronoUnit.DAYS, "d(?:ay)?s?")
            .put(ChronoUnit.HOURS, "h(?:our|r)?s?")
            .put(ChronoUnit.MINUTES, "m(?:inute|in)?s?")
            .put(ChronoUnit.SECONDS, "s(?:econd|ec)?s?")
            .build();
    private static final ChronoUnit[] UNITS = UNITS_PATTERNS.keySet().toArray(new ChronoUnit[0]);

    private static final String PATTERN_STRING = UNITS_PATTERNS.values().stream()
            .map(pattern -> "(?:(\\d+)\\s*" + pattern + "[,\\s]*)?")
            .collect(Collectors.joining("", "^\\s*", "$"));
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING, Pattern.CASE_INSENSITIVE);

    private static final DurationArgumentType INSTANCE = new DurationArgumentType();

    private DurationArgumentType() {
    }

    public static DurationArgumentType duration() {
        return INSTANCE;
    }

    @Override
    public Duration convert(String nativeType) throws CommandSyntaxException {
        Matcher matcher = PATTERN.matcher(nativeType);
        if (!matcher.matches()) {
            Message message = message().serialize(translatable(
                    "cu.argument.duration.invalid-duration", text(nativeType)));
            throw new SimpleCommandExceptionType(message).create();
        }

        Duration duration = Duration.ZERO;
        for (int i = 0; i < UNITS.length; i++) {
            ChronoUnit unit = UNITS[i];
            int g = i + 1;

            if (matcher.group(g) != null && !matcher.group(g).isEmpty()) {
                int n = Integer.parseInt(matcher.group(g));
                if (n > 0) {
                    duration = duration.plus(unit.getDuration().multipliedBy(n));
                }
            }
        }
        return duration;
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return string();
    }
}
