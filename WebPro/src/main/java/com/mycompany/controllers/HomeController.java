
// src/main/java/com/mycompany/controllers/HomeController.java
package com.mycompany.controllers;

import com.mycompany.models.Product;
import com.mycompany.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    // Trang chủ - Chỉ hiển thị top 10 sản phẩm xu hướng và phổ biến
    @GetMapping({ "/", "/home", "/index" })
    public String home(Model model) {
        // Limit to top 10 products
        List<Product> xhProducts = productService.findByXuhuong(1).stream().limit(10).collect(Collectors.toList());
        List<Product> pbProducts = productService.findByPhobien(1).stream().limit(10).collect(Collectors.toList());

        model.addAttribute("listPro", xhProducts);
        model.addAttribute("listProPhobien", pbProducts);
        return "index";
    }

    // NOTE: All cart operations are now handled by CartController
    // - /add-to-cart -> CartController#addToCart
    // - /cart -> CartController#viewCart
    // - /cart/update -> CartController#updateCart
    // - /cart/remove -> CartController#removeFromCart
    // - /cart/clear -> CartController#clearCart

}