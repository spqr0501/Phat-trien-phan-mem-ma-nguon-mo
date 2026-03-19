package com.mycompany.services;

import com.mycompany.models.Wishlist;
import com.mycompany.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    // Get user's wishlist
    public List<Wishlist> getUserWishlist(String userId) {
        return wishlistRepository.findByUserIdOrderByAddedDateDesc(userId);
    }

    // Add to wishlist
    @Transactional
    public Wishlist addToWishlist(String userId, String productId) {
        // Check if already in wishlist
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Sản phẩm đã có trong wishlist!");
        }

        Wishlist wishlist = new Wishlist(userId, productId);
        return wishlistRepository.save(wishlist);
    }

    // Remove from wishlist
    @Transactional
    public void removeFromWishlist(String userId, String productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    // Toggle wishlist (add if not exists, remove if exists)
    @Transactional
    public boolean toggleWishlist(String userId, String productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            wishlistRepository.deleteByUserIdAndProductId(userId, productId);
            return false; // Removed
        } else {
            wishlistRepository.save(new Wishlist(userId, productId));
            return true; // Added
        }
    }

    // Check if product is in wishlist
    public boolean isInWishlist(String userId, String productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    // Get wishlist count
    public Long getWishlistCount(String userId) {
        return wishlistRepository.countByUserId(userId);
    }
}
