package com.github.ProkofievAndrii;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ProkofievAndrii.Main;
import com.github.ProkofievAndrii.Command;
import com.github.ProkofievAndrii.Message;
import com.github.ProkofievAndrii.MessageReceiver;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class FakeMessageReceiver implements MessageReceiver {

    private final Random random = new Random();

    @Override
    public byte[] receiveMessage() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Message msg = new Message();
            msg.setCommand(Command.GET_QUANTITY);
            msg.setProduct("Гречка");
            msg.setAmount(0);

            String json = mapper.writeValueAsString(msg);
            return Main.encode(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate fake message", e);
        }
    }
}
