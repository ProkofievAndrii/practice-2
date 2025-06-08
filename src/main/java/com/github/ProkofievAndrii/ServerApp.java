package com.github.ProkofievAndrii;

public class ServerApp {

    public static void main(String[] args) throws InterruptedException {
        MessageReceiver receiver = new FakeMessageReceiver();
        MessageSender sender = new ConsoleSender();
        MessageEncryptor encryptor = new MessageEncryptor(sender);
        MessageHandler handler = new MessageHandler();
        MessageProcessor processor = new MessageProcessor(handler, encryptor);
        MessageDecryptor decryptor = new MessageDecryptor();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            processor.shutdown();
            encryptor.shutdown();
        }));

        for (int i = 0; i < 100; i++) {
            byte[] incoming = receiver.receiveMessage();
            Message msg = decryptor.decrypt(incoming);
            processor.process(msg);
            Thread.sleep(100);
        }
    }
}

