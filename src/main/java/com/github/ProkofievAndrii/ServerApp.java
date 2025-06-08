package com.github.ProkofievAndrii;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {

    private static final int NUM_THREADS = 8;
    private static final int NUM_MESSAGES = 100;

    public static void main(String[] args) {
        MessageReceiver receiver = new FakeMessageReceiver();
        MessageSender sender = new ConsoleSender();
        MessageEncryptor encryptor = new MessageEncryptor(sender);
        MessageHandler handler = new MessageHandler();
        MessageProcessor processor = new MessageProcessor(handler, encryptor);
        MessageDecryptor decryptor = new MessageDecryptor();

        ExecutorService clientSimulator = Executors.newFixedThreadPool(NUM_THREADS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gracefully...");
            processor.shutdown();
            encryptor.shutdown();
            clientSimulator.shutdown();
        }));

        for (int i = 0; i < NUM_MESSAGES; i++) {
            clientSimulator.submit(() -> {
                try {
                    byte[] incoming = receiver.receiveMessage();
                    Message message = decryptor.decrypt(incoming);
                    processor.process(message);
                } catch (Exception e) {
                    System.err.println("Error in simulated client thread: " + e.getMessage());
                }
            });
        }

        clientSimulator.shutdown();
    }
}


