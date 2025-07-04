package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.type.ProductCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Product, UUID> {

    Page<Product> findAllByProductCategory(ProductCategory category, Pageable pageable);
}
