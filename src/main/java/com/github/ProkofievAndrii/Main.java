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
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(product);

            byte[] encoded = encode(json);
            System.out.println(bytesToHex(encoded));

            String decodedJson = decode(encoded);
            System.out.println(decodedJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] encode(String json) {
        try {
            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
            ByteBuffer plainMessage = ByteBuffer.allocate(8 + jsonBytes.length).order(ByteOrder.BIG_ENDIAN);
            plainMessage.putInt(0x00000001);
            plainMessage.putInt(0x00000042);
            plainMessage.put(jsonBytes);

            byte[] encrypted = CypherUtils.encrypt(plainMessage.array());
            int msgLen = encrypted.length;
            int headerSize = 1 + 1 + 8 + 4 + 2 + 2;
            int total = headerSize + msgLen + 2;
            ByteBuffer buf = ByteBuffer.allocate(total).order(ByteOrder.BIG_ENDIAN);
            buf.put((byte)0x13);
            buf.put((byte)0x01);
            buf.putLong(1L);
            buf.putInt(msgLen);
            short crcHeader = Crc16.calculateCRC(buf.array(), 0, 14);
            buf.putShort(crcHeader);
            buf.put(encrypted);
            short crcMsg = Crc16.calculateCRC(encrypted, 0, encrypted.length);
            buf.putShort(crcMsg);
            return buf.array();
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
