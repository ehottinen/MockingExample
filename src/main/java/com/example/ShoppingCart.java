package com.example;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private final List<CartItem> items = new ArrayList<>();
    private double discountPercentage = 0.0;

    public void addItem(Item item, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Kvantitet måste vara positivt");
        }
        items.add(new CartItem(item, quantity));
    }
    public void removeItem(String itemName) {
        items.removeIf(cartItem -> cartItem.getItem().getName().toLowerCase().equalsIgnoreCase(itemName));
    }
    public void updateQuantity(String itemName, int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Kvantitet måste vara positiv");
        }
    }

        for (CartItem cartItem : items) {
        if (cartItem.getItem().getName().equalsIgnoreCase(itemName)) {
            cartItem.setQuantity(newQuantity);
            return;
        }
    }
    public void applyDiscount(double percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Rabatt måste vara mellan 0 och 100%");
        }
        this.discountPercentage = percent;
    }
    public double calculateSubtotal() {
        return items.stream()
                .mapToDouble(cartItem -> cartItem.getItem().getPrice() * cartItem.getQuantity())
                .sum();
    }

    public double calculateTotalPrice() {
        return items.stream()
                .mapToDouble(cartItem -> cartItem.getItem().getPrice() * cartItem.getQuantity())
                .sum();
    }
    public void applyDiscount(double percent) {
        double subtotal = calculateSubtotal();
        double discountAmount = subtotal * (discountPercentage / 100);
        return subtotal - discountAmount;
    }
    public List<CartItem> getItems() {
        return items;
    }
}
