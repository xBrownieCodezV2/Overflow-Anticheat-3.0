package me.jumba.overflow.util.minecraft;

import io.netty.buffer.ByteBuf;

/**
 * Created on 07/04/2020 Package me.jumba.sparky.util.minecraft
 */
public class ByteUtils {
    public static byte[] readByteArray(ByteBuf byteBuf) {
        byte[] abyte = new byte[readVarIntFromBuffer(byteBuf)];
        readBytes(abyte, byteBuf);
        return abyte;
    }

    public static int readVarIntFromBuffer(ByteBuf byteBuf) {
        int i = 0;
        int j = 0;

        while (true) {
            byte b0 = readByte(byteBuf);
            i |= (b0 & 127) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    public static ByteBuf readBytes(byte[] p_readBytes_1_, ByteBuf byteBuf) {
        return byteBuf.readBytes(p_readBytes_1_);
    }


    public static byte readByte(ByteBuf byteBuf) {
        return byteBuf.readByte();
    }
}
