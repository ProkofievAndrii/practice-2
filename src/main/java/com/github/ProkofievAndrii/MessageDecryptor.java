package com.github.ProkofievAndrii;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageDecryptor {

    private final ObjectMapper mapper = new ObjectMapper();

    public Message decrypt(byte[] data) {
        try {
            String json = Main.decode(data);
            return mapper.readValue(json, Message.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decrypt or parse message", e);
        }
    }
}

