package com.example;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private final List<CartItem> items = new ArrayList<>();

    public void addItem(Item item, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Kvantitet måste vara positivt");
        }
        items.add(new CartItem(item, quantity));
    }

    public List<CartItem> getItems() {
        return items;
    }
    public void removeItem(String itemName) {
        items.removeIf(cartItem -> cartItem.getItem().getName().toLowerCase().equalsIgnoreCase(itemName));
    }
}
