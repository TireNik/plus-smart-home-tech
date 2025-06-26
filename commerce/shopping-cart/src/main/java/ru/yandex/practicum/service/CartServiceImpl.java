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

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartMapper mapper;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        checkUserPresence(username);
        ShoppingCart cart = cartRepository.findByUsername(username);
        return mapper.toShoppingCartDto(cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto addToShoppingCart(String userId, Map<String, Long> products) {
        ShoppingCartDto shoppingCartDto = ShoppingCartDto.builder()
                .shoppingCartId(userId)
                .products(products)
                .build();

        warehouseClient.checkShoppingCart(shoppingCartDto);

        checkUserPresence(userId);
        ShoppingCart shoppingCart = cartRepository.findByUsername(userId);

        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setUsername(userId);
            shoppingCart.setProducts(new HashMap<>());
        }

        Map<String, Long> currentProducts = shoppingCart.getProducts();

        for (Map.Entry<String, Long> entry : products.entrySet()) {
            String productId = entry.getKey();
            Long quantityToAdd = entry.getValue();

            currentProducts.merge(productId, quantityToAdd, Long::sum);
        }
        cartRepository.save(shoppingCart);

        return mapper.toShoppingCartDto(shoppingCart);
    }

    @Transactional
    @Override
    public void deleteUserCart(String userId) {
        checkUserPresence(userId);
        ShoppingCart cart = cartRepository.findByUsername(userId);
        cart.setCartState(false);
        cartRepository.save(cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto changeCart(String userId, Map<String, Long> items) {
        checkUserPresence(userId);
        ShoppingCart cart = cartRepository.findByUsername(userId);
        if (cart == null)
            throw new NoProductsInShoppingCartException("Отсутствует корзина у пользователя " + userId);
        cart.setProducts(items);
        return mapper.toShoppingCartDto(cartRepository.save(cart));
    }

    @Transactional
    @Override
    public ShoppingCartDto changeCountProductInCart(String userId, ChangeProductQuantityRequest request) {
        checkUserPresence(userId);
        ShoppingCart cart = cartRepository.findByUsername(userId);
        if (cart == null || !cart.getProducts().containsKey(request.getProductId()))
            throw new NoProductsInShoppingCartException("Отсутствует корзина у пользователя " + userId);
        cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        return mapper.toShoppingCartDto(cartRepository.save(cart));
    }

    private void checkUserPresence(String username) {
        if (username == null || username.isEmpty())
            throw new NotAuthorizedUserException("Отсутствует пользователь");
    }
}
