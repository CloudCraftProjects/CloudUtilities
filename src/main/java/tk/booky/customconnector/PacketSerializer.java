package tk.booky.customconnector;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class PacketSerializer {

    private final byte[] result;

    public PacketSerializer(String string) {
        ByteBuf buffer = Unpooled.buffer();
        writeString(string, buffer);

        result = buffer.array();
        buffer.release();
    }

    private void writeString(String string, ByteBuf buffer) {
        if (string.length() > Short.MAX_VALUE) throw new IllegalArgumentException("String too long!");
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

        writeVarInt(bytes.length, buffer);
        buffer.writeBytes(bytes);
    }

    private void writeVarInt(int value, ByteBuf output) {
        int part;

        do {
            part = value & 0x7F;
            value >>>= 7;
            if (value != 0) part |= 0x80;
            output.writeByte(part);
        } while (value != 0);
    }

    public byte[] toArray() {
        return result;
    }
}

