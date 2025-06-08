package com.github.ProkofievAndrii;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Main {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        CreateProduct message = new CreateProduct("test", 100.0);
        byte[] out = encode(message);
        System.out.println(bytesToHex(out));
        CreateProduct decodedOut = decode(out);
        System.out.println(decodedOut);
    }

    @SneakyThrows
    public static byte[] encode(CreateProduct message) {
        byte[] messageBytes = objectMapper.writeValueAsBytes(message);
        int messageSize = messageBytes.length + 4 + 4;
        int headerSize = 1 + 1 + 8 + 4 + 2;
        int size = headerSize + messageSize + 2;
        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte) 0x13)
                .put((byte) 1)
                .putLong(1)
                .putInt(messageSize);

        short headerCrc = Crc16.calculateCRC(buffer.array(), 0, buffer.position());
        buffer.putShort(headerCrc)
                .putInt(3)
                .putInt(4)
                .put(messageBytes);

        short bodyCrc = Crc16.calculateCRC(buffer.array(), headerSize, messageSize);
        buffer.putShort(bodyCrc);

        return buffer.array();
    }

    @SneakyThrows
    public static CreateProduct decode(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte magicB = buffer.get();
        if (magicB != 0x13)
            throw new IllegalArgumentException();
        byte bSrc = buffer.get();
        long bPktId = buffer.getLong();
        int wLen = buffer.getInt();
        short wCrc16 = buffer.getShort();
        short expectedCrc = Crc16.calculateCRC(buffer.array(), 0, 14);
        if (wCrc16 != expectedCrc)
            throw new IllegalArgumentException();
        int cType = buffer.getInt();
        int bUserID = buffer.getInt();
        int messageSize = wLen - 8;
        byte[] messageBytes = new byte[messageSize];
        buffer.get(messageBytes, 0, messageSize);
        short w2Crc16 = buffer.getShort(bytes.length - 2);
        short expectedCrc2 = Crc16.calculateCRC(buffer.array(), 16, wLen);
        if (w2Crc16 != expectedCrc2)
            throw new IllegalArgumentException();
        return objectMapper.readValue(messageBytes, CreateProduct.class);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}