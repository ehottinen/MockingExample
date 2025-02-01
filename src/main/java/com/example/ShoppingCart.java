package com.example;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private final List<CartItem> items = new ArrayList<>();

    public void addItem(Item item, int quantity) {
        items.add(new CartItem(item, quantity));
    }

    public List<CartItem> getItems() {
        return items;
    }
}
