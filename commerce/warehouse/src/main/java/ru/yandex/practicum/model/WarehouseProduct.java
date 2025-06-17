package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "warehouse_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class WarehouseProduct {
    @Id
    @Column(name = "product_id", nullable = false)
    String productId;
    @Column(name = "quantity", nullable = false)
    int quantity;
    @Column(name = "fragile", nullable = false)
    boolean fragile;
    @Column(name = "weight", nullable = false)
    double weight;
    Size dimension;
}