package com.github.ProkofievAndrii;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageProcessor {

    private final ExecutorService executor = Executors.newFixedThreadPool(8);
    private final MessageHandler handler;
    private final MessageEncryptor encryptor;

    public MessageProcessor(MessageHandler handler, MessageEncryptor encryptor) {
        this.handler = handler;
        this.encryptor = encryptor;
    }

    public void process(Message message) {
        executor.submit(() -> {
            String responseText = handler.handle(message);
            encryptor.encryptAndSend(responseText);
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}
