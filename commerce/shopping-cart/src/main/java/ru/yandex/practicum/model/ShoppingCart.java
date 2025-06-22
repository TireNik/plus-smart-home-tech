package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Entity
@Table(name = "shopping_cart")
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shopping_cart_id", nullable = false)
    String shoppingCartId;
    @Column(name = "username", nullable = false)
    String username;
    @Column(name = "cart_state", nullable = false)
    boolean cartState;
    @ElementCollection
    @CollectionTable(name = "shopping_cart_products", joinColumns = @JoinColumn(name = "cart_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    Map<String, Long> products;
}