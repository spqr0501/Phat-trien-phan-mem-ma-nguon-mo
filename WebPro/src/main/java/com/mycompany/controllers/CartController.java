package com.mycompany.controllers;

import com.mycompany.models.Product;
import com.mycompany.services.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    // ==================== ADD TO CART (AJAX) ====================
    @PostMapping("/add-to-cart")
    @ResponseBody
    public Map<String, Object> addToCart(
            @RequestParam("mahh") String mahh,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validate stock before adding to session cart
            Product product = productService.findByMahh(mahh);
            if (product == null) {
                response.put("success", false);
                response.put("cartSize", 0);
                response.put("message", "Sản phẩm không tồn tại!");
                response.put("errorType", "product");
                return response;
            }

            int availableStock = product.getSoluong() != null ? product.getSoluong() : 0;
            if (availableStock <= 0) {
                response.put("success", false);
                response.put("cartSize", 0);
                response.put("message", "Hết hàng!");
                response.put("errorType", "stock");
                return response;
            }

            // Get cart from session
            @SuppressWarnings("unchecked")
            Map<String, Integer> cart = (Map<String, Integer>) session.getAttribute("cart");
            if (cart == null) {
                cart = new HashMap<>();
            }

            // Add product to cart
            int existingQty = cart.getOrDefault(mahh, 0);
            int newQty = existingQty + quantity;

            if (newQty > availableStock) {
                response.put("success", false);
                response.put("cartSize", existingQty > 0 ? existingQty : 0);
                response.put("message", "Chỉ còn " + availableStock + " sản phẩm trong kho.");
                response.put("errorType", "stock");
                return response;
            }

            cart.put(mahh, newQty);

            // Save to session
            session.setAttribute("cart", cart);

            // Calculate total cart size
            int cartSize = cart.values().stream().mapToInt(Integer::intValue).sum();

            System.out.println(
                    "[ADD TO CART] Product: " + mahh + " | Quantity: " + quantity + " | Cart Size: " + cartSize);

            response.put("success", true);
            response.put("cartSize", cartSize);
            response.put("message", "Đã thêm vào giỏ hàng!");

        } catch (Exception e) {
            System.err.println("[ADD TO CART ERROR] " + e.getMessage());
            response.put("success", false);
            response.put("cartSize", 0);
            response.put("message", "Lỗi khi thêm vào giỏ hàng!");
            response.put("errorType", "server");
        }

        return response;
    }

    // ==================== GET CART SIZE ====================
    @GetMapping("/cart-size")
    @ResponseBody
    public Map<String, Object> getCartSize(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        @SuppressWarnings("unchecked")
        Map<String, Integer> cart = (Map<String, Integer>) session.getAttribute("cart");

        int cartSize = 0;
        if (cart != null) {
            cartSize = cart.values().stream().mapToInt(Integer::intValue).sum();
        }

        response.put("cartSize", cartSize);
        return response;
    }

    // ==================== VIEW CART ====================
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {

        @SuppressWarnings("unchecked")
        Map<String, Integer> cart = (Map<String, Integer>) session.getAttribute("cart");

        System.out.println("\n========== VIEW CART ==========");
        System.out.println("Session ID: " + session.getId());
        System.out.println("Cart: " + cart);

        if (cart == null || cart.isEmpty()) {
            System.out.println("Cart is EMPTY");
            System.out.println("===============================\n");
            model.addAttribute("cartItems", new ArrayList<>());
            model.addAttribute("total", 0);
            return "cart";
        }

        List<CartItemDTO> cartItems = new ArrayList<>();
        int total = 0;

        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            Product product = productService.findByMahh(entry.getKey());
            if (product != null) {
                CartItemDTO item = new CartItemDTO();
                item.setProduct(product);
                item.setQuantity(entry.getValue());
                item.setSubtotal(product.getDongia() * entry.getValue());

                cartItems.add(item);
                total += item.getSubtotal();

                System.out.println("Item: " + product.getTenhh() + " x " + entry.getValue());
            }
        }

        System.out.println("Total items: " + cartItems.size());
        System.out.println("Total: " + total);
        System.out.println("===============================\n");

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        return "cart";
    }

    // ==================== UPDATE CART QUANTITY ====================
    @PostMapping("/cart/update")
    @ResponseBody
    public Map<String, Object> updateCartQuantity(
            @RequestParam("mahh") String mahh,
            @RequestParam("quantity") int quantity,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        @SuppressWarnings("unchecked")
        Map<String, Integer> cart = (Map<String, Integer>) session.getAttribute("cart");

        if (cart != null) {
            if (quantity <= 0) {
                cart.remove(mahh);
            } else {
                // Validate stock when updating quantity
                Product product = productService.findByMahh(mahh);
                if (product == null) {
                    response.put("success", false);
                    response.put("message", "Sản phẩm không tồn tại!");
                    response.put("errorType", "product");
                    return response;
                }

                int availableStock = product.getSoluong() != null ? product.getSoluong() : 0;
                if (availableStock <= 0) {
                    response.put("success", false);
                    response.put("message", "Hết hàng!");
                    response.put("errorType", "stock");
                    return response;
                }

                if (quantity > availableStock) {
                    response.put("success", false);
                    response.put("message", "Chỉ còn " + availableStock + " sản phẩm trong kho.");
                    response.put("errorType", "stock");
                    response.put("cartSize", cart.values().stream().mapToInt(Integer::intValue).sum());
                    return response;
                }

                cart.put(mahh, quantity);
            }
            session.setAttribute("cart", cart);

            int cartSize = cart.values().stream().mapToInt(Integer::intValue).sum();

            response.put("success", true);
            response.put("cartSize", cartSize);
            response.put("message", "Cập nhật giỏ hàng thành công!");
        } else {
            response.put("success", false);
            response.put("cartSize", 0);
            response.put("message", "Giỏ hàng không tồn tại!");
            response.put("errorType", "server");
        }

        return response;
    }

    // ==================== REMOVE FROM CART ====================
    @PostMapping("/cart/remove")
    @ResponseBody
    public Map<String, Object> removeFromCart(
            @RequestParam("mahh") String mahh,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        @SuppressWarnings("unchecked")
        Map<String, Integer> cart = (Map<String, Integer>) session.getAttribute("cart");

        if (cart != null) {
            cart.remove(mahh);
            session.setAttribute("cart", cart);

            int cartSize = cart.values().stream().mapToInt(Integer::intValue).sum();

            response.put("success", true);
            response.put("cartSize", cartSize);
        } else {
            response.put("success", false);
            response.put("cartSize", 0);
        }

        return response;
    }

    // ==================== CLEAR CART ====================
    @PostMapping("/cart/clear")
    @ResponseBody
    public Map<String, Object> clearCart(HttpSession session) {
        session.removeAttribute("cart");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("cartSize", 0);
        return response;
    }

    // ==================== DTO CLASS ====================
    public static class CartItemDTO {
        private Product product;
        private int quantity;
        private int subtotal;

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(int subtotal) {
            this.subtotal = subtotal;
        }
    }
}
