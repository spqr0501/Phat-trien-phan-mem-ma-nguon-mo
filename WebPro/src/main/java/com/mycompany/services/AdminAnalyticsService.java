package com.mycompany.services;

import com.mycompany.models.Product;
import com.mycompany.repository.OrderNewRepository;
import com.mycompany.repository.ProductRepository;
import com.mycompany.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class AdminAnalyticsService {

    @Autowired
    private OrderNewRepository orderNewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // Get dashboard statistics
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total revenue
        Double totalRevenue = orderNewRepository.getTotalRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);

        // Order counts by status
        stats.put("pendingOrders", orderNewRepository.countByStatus("Pending"));
        stats.put("processingOrders", orderNewRepository.countByStatus("Processing"));
        stats.put("completedOrders", orderNewRepository.countByStatus("Completed"));
        stats.put("totalOrders", orderNewRepository.count());

        // Product statistics
        stats.put("totalProducts", productRepository.count());
        stats.put("lowStockProducts", productRepository.countBySoluongLessThan(10));
        stats.put("outOfStockProducts", productRepository.countBySoluong(0));

        // Review statistics
        stats.put("totalReviews", reviewRepository.count());

        return stats;
    }

    // Get low stock products
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findBySoluongLessThanOrderBySoluongAsc(threshold);
    }

    // Get top selling products based on completed orders' order items
    @Transactional(readOnly = true)
    public List<Product> getTopProducts(int limit) {
        // Use Completed orders as source of truth.
        List<com.mycompany.models.OrderNew> completedOrders = orderNewRepository.findByStatusOrderByCreatedDateDesc("Completed");

        // Aggregate revenue by product
        Map<String, Product> productById = new HashMap<>();
        Map<String, BigDecimal> revenueByProduct = new HashMap<>();

        for (com.mycompany.models.OrderNew order : completedOrders) {
            if (order.getOrderItems() == null) continue;
            order.getOrderItems().forEach(item -> {
                if (item == null || item.getProduct() == null) return;

                Product p = item.getProduct();
                String mahh = p.getMahh();
                if (mahh == null) return;

                productById.put(mahh, p);
                BigDecimal subtotal = item.getSubtotal();
                revenueByProduct.merge(mahh, subtotal, BigDecimal::add);
            });
        }

        // Sort by revenue desc and limit
        return revenueByProduct.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(e -> productById.get(e.getKey()))
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }

    // Get revenue by month (real grouping from completed orders)
    @Transactional(readOnly = true)
    public Map<String, Double> getMonthlyRevenue() {
        List<com.mycompany.models.OrderNew> completedOrders = orderNewRepository.findByStatusOrderByCreatedDateDesc("Completed");
        if (completedOrders == null || completedOrders.isEmpty()) {
            return new LinkedHashMap<>();
        }

        String[] monthNamesVi = {
                "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        };

        // Decide if we need to include year in labels
        Set<Integer> years = completedOrders.stream()
                .map(o -> o.getCreatedDate() != null ? o.getCreatedDate().getYear() : null)
                .filter(y -> y != null)
                .collect(Collectors.toSet());
        boolean includeYear = years.size() > 1;

        // monthIndex -> revenue
        // Use TreeMap to keep chronological order by month number.
        Map<Integer, Double> revenueByMonth = new TreeMap<>();

        for (com.mycompany.models.OrderNew order : completedOrders) {
            if (order == null) continue;
            LocalDateTime dt = order.getCreatedDate();
            if (dt == null) continue;

            int monthIndex = dt.getMonthValue(); // 1..12
            double amount = order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0;
            revenueByMonth.merge(monthIndex, amount, (oldVal, newVal) -> oldVal + newVal);
        }

        // Build ordered map with Vietnamese month labels, filtering zero revenue
        Map<String, Double> monthlyRevenue = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> e : revenueByMonth.entrySet()) {
            double value = e.getValue() != null ? e.getValue() : 0.0;
            if (value <= 0) continue;

            int monthIndex = e.getKey();
            String label = monthNamesVi[monthIndex - 1];

            // Optional: include year if multiple years exist
            if (includeYear) {
                // Try to get year from one representative order for this month.
                // (kept simple: first matching order)
                Integer year = completedOrders.stream()
                        .filter(o -> o.getCreatedDate() != null
                                && o.getCreatedDate().getMonthValue() == monthIndex)
                        .map(o -> o.getCreatedDate().getYear())
                        .findFirst()
                        .orElse(null);
                if (year != null) {
                    label = monthNamesVi[monthIndex - 1] + "/" + year;
                }
            }

            monthlyRevenue.put(label, value);
        }

        return monthlyRevenue;
    }
}
