package com.mycompany.controllers;

import com.mycompany.models.Product;
import com.mycompany.services.AdminAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    @Autowired
    private AdminAnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get all statistics
        Map<String, Object> stats = analyticsService.getDashboardStats();
        model.addAttribute("stats", stats);

        // Get low stock products (threshold: 10)
        List<Product> lowStockProducts = analyticsService.getLowStockProducts(10);
        model.addAttribute("lowStockProducts", lowStockProducts);

        // Get top products
        List<Product> topProducts = analyticsService.getTopProducts(5);
        model.addAttribute("topProducts", topProducts);

        // Get monthly revenue
        Map<String, Double> monthlyRevenue = analyticsService.getMonthlyRevenue();
        model.addAttribute("monthlyRevenue", monthlyRevenue);

        return "admin-dashboard";
    }
}
