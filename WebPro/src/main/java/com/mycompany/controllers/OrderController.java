package com.mycompany.controllers;

import com.mycompany.models.OrderNew;
import com.mycompany.services.OrderService;
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
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Redirect /orders to /orders/history
    @GetMapping
    public String redirectToHistory() {
        return "redirect:/orders/history";
    }

    // Order history page
    @GetMapping("/history")
    public String orderHistory(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String userId = authentication.getName();
        List<OrderNew> orders = orderService.getUserOrders(userId);

        model.addAttribute("orders", orders);
        return "order-history";
    }

    // Order detail page
    @GetMapping("/{orderId}")
    public String orderDetail(@PathVariable Integer orderId,
            Authentication authentication,
            Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Optional<OrderNew> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isEmpty()) {
            return "redirect:/orders/history";
        }

        OrderNew order = orderOpt.get();
        String userId = authentication.getName();

        // Check if user owns this order
        if (!order.getUserId().equals(userId)) {
            return "redirect:/orders/history";
        }

        model.addAttribute("order", order);
        return "order-detail";
    }

    // Cancel order (AJAX)
    @PostMapping("/api/orders/{orderId}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Integer orderId,
            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "Bạn cần đăng nhập!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            String userId = authentication.getName();
            orderService.cancelOrder(orderId, userId);

            response.put("success", true);
            response.put("message", "Đã hủy đơn hàng!");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
