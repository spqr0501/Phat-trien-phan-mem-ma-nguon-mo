package com.mycompany.controllers.api;

import com.mycompany.models.Product;
import com.mycompany.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ApiProductController {

    @Autowired
    private ProductService productService;

    // GET /api/v1/products — Lấy tất cả sản phẩm
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        List<Product> products = productService.getAll();
        return ResponseEntity.ok(products);
    }

    // GET /api/v1/products/{id} — Lấy sản phẩm theo mã
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String id) {
        Product product = productService.findByMahh(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy sản phẩm với mã: " + id));
        }
        return ResponseEntity.ok(product);
    }

    // GET /api/v1/products/trending — Sản phẩm xu hướng
    @GetMapping("/trending")
    public ResponseEntity<List<Product>> getTrending() {
        List<Product> products = productService.findByXuhuong(1);
        return ResponseEntity.ok(products);
    }

    // GET /api/v1/products/popular — Sản phẩm phổ biến
    @GetMapping("/popular")
    public ResponseEntity<List<Product>> getPopular() {
        List<Product> products = productService.findByPhobien(1);
        return ResponseEntity.ok(products);
    }

    // POST /api/v1/products — Thêm sản phẩm mới
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();

        if (product.getMahh() == null || product.getMahh().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Mã hàng hóa không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        if (product.getTenhh() == null || product.getTenhh().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Tên sản phẩm không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        boolean ok = productService.create(product);
        if (ok) {
            response.put("success", true);
            response.put("message", "Thêm sản phẩm thành công!");
            response.put("data", product);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("success", false);
            response.put("message", "Mã hàng đã tồn tại hoặc dữ liệu không hợp lệ!");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // PUT /api/v1/products/{id} — Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable("id") String id,
            @RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();

        product.setMahh(id); // Đảm bảo mã hàng đúng với path

        boolean ok = productService.update(product);
        if (ok) {
            response.put("success", true);
            response.put("message", "Cập nhật sản phẩm thành công!");
            response.put("data", product);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Cập nhật thất bại! Sản phẩm không tồn tại hoặc dữ liệu không hợp lệ.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // DELETE /api/v1/products/{id} — Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();

        boolean ok = productService.delete(id);
        if (ok) {
            response.put("success", true);
            response.put("message", "Xóa sản phẩm thành công!");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Xóa thất bại! Sản phẩm không tồn tại hoặc đang được sử dụng.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
