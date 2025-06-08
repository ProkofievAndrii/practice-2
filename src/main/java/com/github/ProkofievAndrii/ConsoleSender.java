package com.github.ProkofievAndrii;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ConsoleSender implements MessageSender {
    @Override
    public void sendMessage(byte[] response) {
        System.out.println("Sending response bytes: " + Arrays.toString(response));
        System.out.println("As string (possibly encrypted): " + new String(response, StandardCharsets.UTF_8));
    }
}
