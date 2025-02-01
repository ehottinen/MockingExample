package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    @Test
    void addItem_shouldIncreaseCartSize() {
        // 🔹 Förbered testdata
        Item item = new Item("Purjolök", 10.0);

        // 🔹 Anropa metoden som testas
        cart.addItem(item, 2);

        // 🔹 Verifiera resultatet
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
    }
    @Test
    void addItem_shouldThrowExceptionForNegativeQuantity() {
        Item item = new Item("Juice", 30.0);

        assertThatThrownBy(() -> cart.addItem(item, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kvantitet måste vara positiv");
    }
    @Test
    void removeItem_shouldDecreaseCartSize() {
        // 🔹 Förbered testdata
        Item item = new Item("Banana", 5.0);
        cart.addItem(item, 3);

        // 🔹 Anropa metoden som testas
        cart.removeItem("Banana");

        // 🔹 Verifiera resultatet
        assertThat(cart.getItems()).isEmpty();
    }

}

