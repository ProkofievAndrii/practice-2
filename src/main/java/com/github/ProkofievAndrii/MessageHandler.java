package com.github.ProkofievAndrii;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandler {

    private final Map<String, Integer> stock = new ConcurrentHashMap<>();

    public synchronized String handle(Message message) {
        String product = message.getProduct();
        int amount = message.getAmount();
        Command command = message.getCommand();

        return switch (command) {
            case GET_QUANTITY -> "Stock of " + product + ": " + stock.getOrDefault(product, 0);
            case ADD_QUANTITY -> {
                stock.put(product, stock.getOrDefault(product, 0) + amount);
                yield "Added " + amount + " to " + product;
            }
            case REMOVE_QUANTITY -> {
                stock.put(product, Math.max(0, stock.getOrDefault(product, 0) - amount));
                yield "Removed " + amount + " from " + product;
            }
            case ADD_GROUP, ADD_PRODUCT_TO_GROUP, SET_PRICE -> "Not implemented yet";
        };
    }
}
