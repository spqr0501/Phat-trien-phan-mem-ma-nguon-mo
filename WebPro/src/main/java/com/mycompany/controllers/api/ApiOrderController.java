package com.mycompany.controllers.api;

import com.mycompany.models.OrderNew;
import com.mycompany.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
public class ApiOrderController {

    @Autowired
    private OrderService orderService;

    // GET /api/v1/orders — Lấy tất cả đơn hàng (admin)
    @GetMapping
    public ResponseEntity<List<OrderNew>> getAll() {
        List<OrderNew> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // GET /api/v1/orders/{id} — Chi tiết đơn hàng
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
        Optional<OrderNew> orderOpt = orderService.getOrderById(id);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy đơn hàng với mã: " + id));
        }
        return ResponseEntity.ok(orderOpt.get());
    }

    // GET /api/v1/orders/user/{userId} — Đơn hàng theo user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderNew>> getByUser(@PathVariable("userId") String userId) {
        List<OrderNew> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    // GET /api/v1/orders/status/{status} — Đơn hàng theo trạng thái
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderNew>> getByStatus(@PathVariable("status") String status) {
        List<OrderNew> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    // PUT /api/v1/orders/{id}/status — Cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        String newStatus = body.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Trạng thái không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            OrderNew updatedOrder = orderService.updateOrderStatus(id, newStatus);
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái thành công!");
            response.put("data", updatedOrder);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // DELETE /api/v1/orders/{id}/cancel — Hủy đơn hàng
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        String userId = body.get("userId");
        if (userId == null || userId.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "userId không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            orderService.cancelOrder(id, userId);
            response.put("success", true);
            response.put("message", "Đã hủy đơn hàng thành công!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // GET /api/v1/orders/stats/revenue — Tổng doanh thu
    @GetMapping("/stats/revenue")
    public ResponseEntity<Map<String, Object>> getRevenue() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", orderService.getTotalRevenue());
        return ResponseEntity.ok(response);
    }
}
