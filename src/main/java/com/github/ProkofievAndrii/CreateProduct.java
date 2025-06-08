package com.github.ProkofievAndrii;

public class CreateProduct {
    public String name;
    public double price;
    public boolean available;

    public CreateProduct() {}

    public CreateProduct(String name, double price, boolean available) {
        this.name = name;
        this.price = price;
        this.available = available;
    }
}
