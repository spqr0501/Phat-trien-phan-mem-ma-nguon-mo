package com.mycompany.controllers;

import com.mycompany.models.Product;
import com.mycompany.services.CategoryService;
import com.mycompany.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // Trang SHOP - Hiển thị TẤT CẢ sản phẩm với filters
    @GetMapping("/shop")
    public String shopPage(
            @RequestParam(value = "category", required = false) Integer categoryId,
            @RequestParam(value = "sort", defaultValue = "default") String sort,
            @RequestParam(value = "minPrice", required = false) Integer minPrice,
            @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
            @RequestParam(value = "brands", required = false) String brands,
            @RequestParam(value = "inStock", required = false) Boolean inStock,
            @RequestParam(value = "perPage", defaultValue = "12") Integer perPage,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        List<Product> products;

        // Lọc theo category nếu có
        if (categoryId != null) {
            products = productService.getAll().stream()
                    .filter(p -> p.getLoaihh() != null && p.getLoaihh().equals(categoryId))
                    .toList();
            model.addAttribute("selectedCategory", categoryId);
        } else {
            products = productService.getAll();
        }

        // Search by keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase();
            products = products.stream()
                    .filter(p -> p.getTenhh().toLowerCase().contains(kw) ||
                            p.getMahh().toLowerCase().contains(kw))
                    .toList();
            model.addAttribute("keyword", keyword);
        }

        // Filter by price range
        if (minPrice != null) {
            products = products.stream()
                    .filter(p -> p.getDongia() >= minPrice)
                    .toList();
        }
        if (maxPrice != null) {
            products = products.stream()
                    .filter(p -> p.getDongia() <= maxPrice)
                    .toList();
        }

        // Filter by brands (comma-separated)
        if (brands != null && !brands.isEmpty()) {
            List<String> brandList = List.of(brands.split(","));
            products = products.stream()
                    .filter(p -> p.getXuatxu() != null &&
                            brandList.stream()
                                    .anyMatch(brand -> p.getXuatxu().toLowerCase().contains(brand.toLowerCase())))
                    .toList();
        }

        // Filter by stock availability
        if (inStock != null && inStock) {
            products = products.stream()
                    .filter(p -> p.getSoluong() != null && p.getSoluong() > 0)
                    .toList();
        }

        // Sắp xếp
        products = sortProducts(products, sort);

        // Pagination
        int totalProducts = products.size();
        int totalPages = (int) Math.ceil((double) totalProducts / perPage);
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        int startIndex = (page - 1) * perPage;
        int endIndex = Math.min(startIndex + perPage, totalProducts);

        List<Product> pagedProducts = totalProducts > 0 ?
                products.subList(startIndex, endIndex) : products;

        model.addAttribute("products", pagedProducts);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("perPage", perPage);
        return "shop";
    }

    // Tìm kiếm sản phẩm
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam("keyword") String keyword,
            Model model) {

        List<Product> results = productService.getAll().stream()
                .filter(p -> p.getTenhh().toLowerCase().contains(keyword.toLowerCase()) ||
                        p.getMahh().toLowerCase().contains(keyword.toLowerCase()))
                .toList();

        model.addAttribute("products", results);
        model.addAttribute("keyword", keyword);
        model.addAttribute("resultCount", results.size());
        return "search-results";
    }

    // Chi tiết sản phẩm (Frontend)
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") String id, Model model) {
        Product product = productService.findByMahh(id);
        if (product == null) {
            return "redirect:/shop";
        }

        // Lấy sản phẩm liên quan (cùng loại)
        List<Product> relatedProducts = productService.getAll().stream()
                .filter(p -> p.getLoaihh() != null &&
                        p.getLoaihh().equals(product.getLoaihh()) &&
                        !p.getMahh().equals(id))
                .limit(4)
                .toList();

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        return "product-detail";
    }

    // Helper method để sắp xếp
    private List<Product> sortProducts(List<Product> products, String sort) {
        return switch (sort) {
            case "price-asc" -> products.stream()
                    .sorted((a, b) -> Integer.compare(a.getDongia(), b.getDongia()))
                    .toList();
            case "price-desc" -> products.stream()
                    .sorted((a, b) -> Integer.compare(b.getDongia(), a.getDongia()))
                    .toList();
            case "name" -> products.stream()
                    .sorted((a, b) -> a.getTenhh().compareTo(b.getTenhh()))
                    .toList();
            default -> products;
        };
    }
}