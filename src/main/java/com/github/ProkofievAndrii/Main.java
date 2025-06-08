package com.github.ProkofievAndrii;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws Exception {
        CreateProduct product = new CreateProduct("Test", 100.0, true);
        demonstrate(product);
        CreateProduct product2 = new CreateProduct("Lorem Ipsum", 29.02, false);
        demonstrate(product2);
    }

    private static void demonstrate(CreateProduct product) {
        byte[] encoded = encode(product);
        System.out.println(bytesToHex(encoded));
        String decoded = decode(encoded);
        System.out.println(decoded);
    }

    public static byte[] encode(CreateProduct product) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(product);
            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);

            ByteBuffer plainMessage = ByteBuffer.allocate(8 + jsonBytes.length).order(ByteOrder.BIG_ENDIAN);
            plainMessage.putInt(0x00000001);
            plainMessage.putInt(0x00000042);
            plainMessage.put(jsonBytes);

            byte[] encryptedMessage = CypherUtils.encrypt(plainMessage.array());

            int messageSize = encryptedMessage.length;
            int headerSize = 1 + 1 + 8 + 4 + 2 + 2;
            int totalSize = headerSize + messageSize + 2;

            ByteBuffer buffer = ByteBuffer.allocate(totalSize).order(ByteOrder.BIG_ENDIAN);
            buffer.put((byte) 0x13);
            buffer.put((byte) 0x01);
            buffer.putLong(1);
            buffer.putInt(messageSize);

            short crcHeader = Crc16.calculateCRC(buffer.array(), 0, 14);
            buffer.putShort(crcHeader);
            buffer.put(encryptedMessage);

            short crcMessage = Crc16.calculateCRC(encryptedMessage, 0, encryptedMessage.length);
            buffer.putShort(crcMessage);

            return buffer.array();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(byte[] messageBytes) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(messageBytes).order(ByteOrder.BIG_ENDIAN);

            buffer.position(2);
            long pktId = buffer.getLong();
            int messageLength = buffer.getInt();
            buffer.position(buffer.position() + 2);

            if (messageBytes.length < buffer.position() + messageLength + 2) {
                throw new IllegalArgumentException("Packet too short");
            }

            byte[] encryptedMessage = new byte[messageLength];
            buffer.get(encryptedMessage);
            short receivedCrc = buffer.getShort();
            short calculatedCrc = Crc16.calculateCRC(encryptedMessage, 0, encryptedMessage.length);
            if (receivedCrc != calculatedCrc) {
                throw new IllegalArgumentException("Invalid message CRC16");
            }

            byte[] decrypted = CypherUtils.decrypt(encryptedMessage);
            if (decrypted.length < 8) {
                throw new IllegalArgumentException("Message too short");
            }

            ByteBuffer msgBuffer = ByteBuffer.wrap(decrypted).order(ByteOrder.BIG_ENDIAN);
            int cType = msgBuffer.getInt();
            int bUserId = msgBuffer.getInt();

            int jsonLen = decrypted.length - 8;
            byte[] jsonBytes = new byte[jsonLen];
            msgBuffer.get(jsonBytes, 0, jsonLen);

            return new String(jsonBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid message CRC16");
        }
    }


    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

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
