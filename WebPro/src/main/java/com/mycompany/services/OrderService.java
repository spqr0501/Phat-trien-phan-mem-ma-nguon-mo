package com.mycompany.services;

import com.mycompany.models.OrderNew;
import com.mycompany.repository.OrderNewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderNewRepository orderNewRepository;

    // Create new order
    @Transactional
    public OrderNew createOrder(OrderNew order) {
        return orderNewRepository.save(order);
    }

    // Get order by ID
    public Optional<OrderNew> getOrderById(Integer orderId) {
        return orderNewRepository.findById(orderId);
    }

    // Get user's orders
    public List<OrderNew> getUserOrders(String userId) {
        return orderNewRepository.findByUserIdOrderByCreatedDateDesc(userId);
    }

    // Get orders by status
    public List<OrderNew> getOrdersByStatus(String status) {
        return orderNewRepository.findByStatusOrderByCreatedDateDesc(status);
    }

    // Get all orders (for admin)
    public List<OrderNew> getAllOrders() {
        return orderNewRepository.findAll(org.springframework.data.domain.Sort
                .by(org.springframework.data.domain.Sort.Direction.DESC, "createdDate"));
    }

    // Update order status
    @Transactional
    public OrderNew updateOrderStatus(Integer orderId, String newStatus) {
        Optional<OrderNew> orderOpt = orderNewRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            OrderNew order = orderOpt.get();
            order.setStatus(newStatus);
            return orderNewRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }

    // Cancel order
    @Transactional
    public void cancelOrder(Integer orderId, String userId) {
        Optional<OrderNew> orderOpt = orderNewRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            OrderNew order = orderOpt.get();

            // Check if user owns this order
            if (!order.getUserId().equals(userId)) {
                throw new RuntimeException("Bạn không có quyền hủy đơn hàng này!");
            }

            // Only allow cancellation if order is pending
            if ("Pending".equals(order.getStatus())) {
                order.setStatus("Cancelled");
                orderNewRepository.save(order);
            } else {
                throw new RuntimeException("Chỉ có thể hủy đơn hàng đang chờ xử lý!");
            }
        } else {
            throw new RuntimeException("Không tìm thấy đơn hàng!");
        }
    }

    // Get order total
    public Long countUserOrders(String userId) {
        return orderNewRepository.countByUserId(userId);
    }

    // Calculate total revenue (for admin)
    public Double getTotalRevenue() {
        Double revenue = orderNewRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    // Get user's total spending
    public Double getUserTotalSpending(String userId) {
        Double spending = orderNewRepository.getTotalRevenueByUser(userId);
        return spending != null ? spending : 0.0;
    }
}
