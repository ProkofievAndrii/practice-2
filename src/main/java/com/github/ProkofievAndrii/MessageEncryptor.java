package com.github.ProkofievAndrii;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageEncryptor {

    private final MessageSender sender;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public MessageEncryptor(MessageSender sender) {
        this.sender = sender;
    }

    public void encryptAndSend(String response) {
        executor.submit(() -> {
            try {
                byte[] encrypted = Main.encode(response);
                sender.sendMessage(encrypted);
            } catch (Exception e) {
                System.err.println("Failed to encrypt response: " + e.getMessage());
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
