package com.github.ProkofievAndrii;

public class Message {
    private Command command;
    private String product;
    private int amount;
    private String group;

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }
}

