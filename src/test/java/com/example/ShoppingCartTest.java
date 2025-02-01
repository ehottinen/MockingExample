package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    void addItem_shouldIncreaseCartSize() {
        // 🔹 Förbered testdata
        Item item = new Item("Leek", 10.0);

        // 🔹 Anropa metoden som testas
        cart.addItem(item, 2);

        // 🔹 Verifiera resultatet
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
    }
}

