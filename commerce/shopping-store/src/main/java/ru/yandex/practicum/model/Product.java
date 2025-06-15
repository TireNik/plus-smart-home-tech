package ru.yandex.practicum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.type.ProductCategory;
import ru.yandex.practicum.type.ProductState;
import ru.yandex.practicum.type.QuantityState;

@Entity
@Table(name = "products")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    String productId;
    String productName;
    String description;
    String imageSrc;

    @Enumerated(EnumType.STRING)
    QuantityState quantityState;
    @Enumerated(EnumType.STRING)
    ProductState productState;
    @Enumerated(EnumType.STRING)
    ProductCategory productCategory;

    Float price;
}
