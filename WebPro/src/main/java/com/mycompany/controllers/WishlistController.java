package com.mycompany.controllers;

import com.mycompany.models.Wishlist;
import com.mycompany.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    // Wishlist page
    @GetMapping("/wishlist")
    public String wishlistPage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String userId = authentication.getName();
        List<Wishlist> wishlistItems = wishlistService.getUserWishlist(userId);

        model.addAttribute("wishlistItems", wishlistItems);
        model.addAttribute("wishlistCount", wishlistItems.size());

        return "wishlist";
    }

    // Toggle wishlist (AJAX)
    @PostMapping("/api/wishlist/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleWishlist(
            @RequestParam String productId,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Bạn cần đăng nhập!");
            response.put("requireLogin", true);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            String userId = authentication.getName();
            boolean added = wishlistService.toggleWishlist(userId, productId);

            response.put("success", true);
            response.put("added", added);
            response.put("message", added ? "Đã thêm vào wishlist!" : "Đã xóa khỏi wishlist!");

            Long count = wishlistService.getWishlistCount(userId);
            response.put("wishlistCount", count);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Check if product is in wishlist (AJAX)
    @GetMapping("/api/wishlist/check/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkWishlist(
            @PathVariable String productId,
            Authentication authentication) {

        Map<String, Boolean> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("inWishlist", false);
            return ResponseEntity.ok(response);
        }

        String userId = authentication.getName();
        boolean inWishlist = wishlistService.isInWishlist(userId, productId);
        response.put("inWishlist", inWishlist);

        return ResponseEntity.ok(response);
    }

    // Remove from wishlist
    @DeleteMapping("/api/wishlist/remove/{productId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromWishlist(
            @PathVariable String productId,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Bạn cần đăng nhập!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            String userId = authentication.getName();
            wishlistService.removeFromWishlist(userId, productId);

            response.put("success", true);
            response.put("message", "Đã xóa khỏi wishlist!");

            Long count = wishlistService.getWishlistCount(userId);
            response.put("wishlistCount", count);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get wishlist count
    @GetMapping("/api/wishlist/count")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getWishlistCount(Authentication authentication) {
        Map<String, Long> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("count", 0L);
            return ResponseEntity.ok(response);
        }

        String userId = authentication.getName();
        Long count = wishlistService.getWishlistCount(userId);
        response.put("count", count);

        return ResponseEntity.ok(response);
    }
}
