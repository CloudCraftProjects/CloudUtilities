package tk.booky.cloudutilities.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class StringSerializer {

    private StringSerializer() {
    }

    public static byte[] fromString(String string) {
        ByteBuf buffer = Unpooled.buffer();

        try {
            writeString(buffer, string);
            return buffer.array();
        } finally {
            buffer.release();
        }
    }

    private static void writeString(ByteBuf buffer, CharSequence string) {
        int size = ByteBufUtil.utf8Bytes(string);
        writeVarInt(buffer, size);
        buffer.writeCharSequence(string, StandardCharsets.UTF_8);
    }

    private static void writeVarInt(ByteBuf buffer, int value) {
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buffer.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            buffer.writeShort((value & 0x7F | 0x80) << 8 | (value >>> 7));
        } else {
            writeVarIntFull(buffer, value);
        }
    }

    private static void writeVarIntFull(ByteBuf buffer, int value) {
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buffer.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            buffer.writeShort((value & 0x7F | 0x80) << 8 | (value >>> 7));
        } else if ((value & (0xFFFFFFFF << 21)) == 0) {
            buffer.writeMedium((value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14));
        } else if ((value & (0xFFFFFFFF << 28)) == 0) {
            buffer.writeInt((value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16) | ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21));
        } else {
            buffer.writeInt((value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16 | ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80));
            buffer.writeByte(value >>> 28);
        }
    }
}

