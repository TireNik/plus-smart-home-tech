package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exeption.NoProductsInShoppingCartException;
import ru.yandex.practicum.exeption.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.CartRepository;
import ru.yandex.practicum.warehouse.WarehouseClient;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartMapper mapper;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCart(String username) {
        checkUserPresence(username);
        return cartRepository.findByUsername(username)
                .map(mapper::toShoppingCartDto)
                .orElseGet(() -> createNewCartDto(username));
    }

    @Transactional
    @Override
    public ShoppingCartDto addToShoppingCart(String username, Map<UUID, Long> products) {
        checkUserPresence(username);

        Optional<ShoppingCart> shoppingCartOpt = cartRepository.findByUsername(username);
        ShoppingCart shoppingCart;

        if (shoppingCartOpt.isPresent()) {
            shoppingCart = shoppingCartOpt.get();
            products.forEach((productId, quantity) -> shoppingCart.getProducts().merge(productId, quantity, Long::sum));

        } else {
            shoppingCart = ShoppingCart.builder().username(username).products(products).isActive(true).build();
        }

        warehouseClient.checkShoppingCart(mapper.toShoppingCartDto(shoppingCart));
        return mapper.toShoppingCartDto(cartRepository.save(shoppingCart));
    }

    @Transactional
    @Override
    public void deleteUserCart(String username) {
        checkUserPresence(username);
        cartRepository.findByUsername(username)
                .ifPresent(shoppingCart -> {
                    shoppingCart.setIsActive(false);
                    cartRepository.save(shoppingCart);
                });
    }

    @Transactional
    @Override
    public ShoppingCartDto changeCart(String username, List<UUID> items) {
        checkUserPresence(username);
        ShoppingCart cart = findCart(username);
        for (UUID productId : items) {
            if (!cart.getProducts().containsKey(productId)) {
                throw new NoProductsInShoppingCartException("Товар = " + productId + " не найден в корзине");
            }
            cart.getProducts().remove(productId);
        }
        return mapper.toShoppingCartDto(cartRepository.save(cart));
    }

    @Transactional
    @Override
    public ShoppingCartDto changeCountProductInCart(String username, ChangeProductQuantityRequest request) {
        checkUserPresence(username);
        ShoppingCart cart = findCart(username);
        if (cart == null || !cart.getProducts().containsKey(request.getProductId()))
            throw new NoProductsInShoppingCartException("Отсутствует корзина у пользователя " + username);

        cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        return mapper.toShoppingCartDto(cartRepository.save(cart));
    }

    private ShoppingCartDto createNewCartDto(String username) {
        ShoppingCart newCart = ShoppingCart.builder()
                .username(username)
                .isActive(true)
                .build();
        cartRepository.save(newCart);
        return mapper.toShoppingCartDto(newCart);
    }

    private ShoppingCart findCart(String username) {
        return cartRepository.findByUsername(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Корзина не найдена для пользователя = " +
                        username));
    }

    private void checkUserPresence(String username) {
        if (username == null || username.isEmpty())
            throw new NotAuthorizedUserException("Отсутствует пользователь");
    }
}

