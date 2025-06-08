package com.github.ProkofievAndrii;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class MessageHandler {

    private final Map<String, Integer> inventory = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> groups = new ConcurrentHashMap<>();
    private final Map<String, Double> prices = new ConcurrentHashMap<>();

    public String handle(Message message) {
        String command = message.getCommand().name();
        String product = message.getProduct();

        switch (command) {
            case "GET_QUANTITY":
                return String.valueOf(inventory.getOrDefault(product, 0));

            case "ADD_QUANTITY":
                inventory.merge(product, message.getAmount(), Integer::sum);
                return "OK";

            case "REMOVE_QUANTITY":
                inventory.compute(product, (k, v) -> {
                    if (v == null || v < message.getAmount()) {
                        throw new IllegalArgumentException("Insufficient stock");
                    }
                    return v - message.getAmount();
                });
                return "OK";

            case "ADD_GROUP":
                groups.putIfAbsent(product, new ConcurrentSkipListSet<>());
                return "OK";

            case "ADD_PRODUCT_TO_GROUP":
                String group = message.getGroup();
                if (group == null) return "Missing group name";
                groups.computeIfAbsent(group, g -> new ConcurrentSkipListSet<>()).add(product);
                return "OK";

            case "SET_PRICE":
                prices.put(product, (double) message.getAmount());
                return "OK";

            default:
                return "UNKNOWN_COMMAND";
        }
    }
}

