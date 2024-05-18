package dev.booky.cloudutilities.config;
// Created by booky10 in CloudUtilities (00:13 11.05.2024.)

import com.velocitypowered.api.network.ProtocolVersion;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Predicate;

public class ProtocolVersionSerializer extends ScalarSerializer<ProtocolVersion> {

    public static final TypeSerializer<ProtocolVersion> INSTANCE = new ProtocolVersionSerializer();

    private ProtocolVersionSerializer() {
        super(ProtocolVersion.class);
    }

    @Override
    public ProtocolVersion deserialize(Type type, Object obj) {
        if (obj instanceof Number num) {
            return ProtocolVersion.getProtocolVersion(num.intValue());
        }
        String objStr = String.valueOf(obj);
        return Arrays.stream(ProtocolVersion.values())
                .filter(version -> version.name().equals(objStr)
                        || version.getVersionsSupportedBy().contains(objStr))
                .findAny().orElse(ProtocolVersion.UNKNOWN);
    }

    @Override
    protected Object serialize(ProtocolVersion item, Predicate<Class<?>> typeSupported) {
        return item.name();
    }
}
