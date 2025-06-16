package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "warehouse_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseProduct {
    @Id
    @Column(name = "product_id", nullable = false)
    private String productId;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Column(name = "fragile", nullable = false)
    private boolean fragile;
    @Column(name = "weight", nullable = false)
    private double weight;
    private Size dimension;
}