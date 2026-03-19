package com.mycompany.controllers.api;

import com.mycompany.models.Product;
import com.mycompany.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SearchSuggestionController {

    @Autowired
    private ProductService productService;

    // Called by: static/js/shop.js
    // GET /api/search-suggestions?q=...
    @GetMapping("/api/search-suggestions")
    public ResponseEntity<List<Product>> searchSuggestions(@RequestParam("q") String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        String keyword = q.trim().toLowerCase();

        List<Product> suggestions = productService.getAll().stream()
                .filter(p ->
                        (p.getTenhh() != null && p.getTenhh().toLowerCase().contains(keyword)) ||
                                (p.getMahh() != null && p.getMahh().toLowerCase().contains(keyword)))
                .limit(8)
                .collect(Collectors.toList());

        return ResponseEntity.ok(suggestions);
    }
}

