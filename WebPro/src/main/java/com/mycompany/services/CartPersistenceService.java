package com.mycompany.services;

import com.mycompany.models.CartItemEntity;
import com.mycompany.repository.CartItemEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartPersistenceService {

    @Autowired
    private CartItemEntityRepository cartItemRepository;

    // Get all cart items for user
    public List<CartItemEntity> getCartItems(String userId) {
        return cartItemRepository.findByUserIdOrderByAddedDateDesc(userId);
    }

    // Add item to cart or update quantity if exists
    @Transactional
    public CartItemEntity addToCart(String userId, String productId, Integer quantity) {
        Optional<CartItemEntity> existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem.isPresent()) {
            // Update quantity
            CartItemEntity item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartItemRepository.save(item);
        } else {
            // Create new cart item
            CartItemEntity newItem = new CartItemEntity(userId, productId, quantity);
            return cartItemRepository.save(newItem);
        }
    }

    // Update cart item quantity
    @Transactional
    public CartItemEntity updateQuantity(String userId, String productId, Integer newQuantity) {
        Optional<CartItemEntity> itemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (itemOpt.isPresent()) {
            CartItemEntity item = itemOpt.get();

            if (newQuantity <= 0) {
                // Remove if quantity is 0 or negative
                cartItemRepository.delete(item);
                return null;
            } else {
                item.setQuantity(newQuantity);
                return cartItemRepository.save(item);
            }
        }

        throw new RuntimeException("Cart item not found");
    }

    // Remove item from cart
    @Transactional
    public void removeFromCart(String userId, String productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    // Clear entire cart
    @Transactional
    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    // Get cart count
    public Long getCartCount(String userId) {
        return cartItemRepository.countByUserId(userId);
    }

    // Check if item is in cart
    public boolean isInCart(String userId, String productId) {
        return cartItemRepository.existsByUserIdAndProductId(userId, productId);
    }
}
