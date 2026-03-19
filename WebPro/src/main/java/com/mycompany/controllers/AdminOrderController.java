package com.mycompany.controllers;

import com.mycompany.models.OrderNew;
import com.mycompany.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    // List all orders
    @GetMapping
    public String listOrders(Model model) {
        List<OrderNew> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/fragments/order-list";
    }

    // View order detail
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<OrderNew> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isPresent()) {
            model.addAttribute("order", orderOpt.get());
            return "admin/fragments/order-detail";
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng!");
            return "redirect:/admin/orders";
        }
    }

    // Update order status
    // Update order status
    @PostMapping("/update-status")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.Map<String, Object>> updateStatus(
            @RequestParam Integer orderId,
            @RequestParam String status) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        try {
            orderService.updateOrderStatus(orderId, status);
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái thành công!");
            return org.springframework.http.ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return org.springframework.http.ResponseEntity.ok(response);
        }
    }
    // API: Get pending orders for notification bell
    @GetMapping("/api/pending")
    @ResponseBody
    public java.util.Map<String, Object> getPendingOrders() {
        java.util.Map<String, Object> response = new java.util.HashMap<>();

        List<OrderNew> pending = orderService.getOrdersByStatus("Pending");
        List<OrderNew> waitingPayment = orderService.getOrdersByStatus("Chờ xác nhận TT");

        java.util.List<java.util.Map<String, Object>> orderList = new java.util.ArrayList<>();

        // Combine both lists
        java.util.List<OrderNew> allPending = new java.util.ArrayList<>(pending);
        allPending.addAll(waitingPayment);

        // Limit to 5 most recent
        int limit = Math.min(allPending.size(), 5);
        for (int i = 0; i < limit; i++) {
            OrderNew order = allPending.get(i);
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("orderId", order.getOrderId());
            item.put("customerName", order.getCustomerName());
            item.put("totalAmount", order.getTotalAmount());
            item.put("status", order.getStatus());
            orderList.add(item);
        }

        response.put("count", allPending.size());
        response.put("orders", orderList);
        return response;
    }
}
