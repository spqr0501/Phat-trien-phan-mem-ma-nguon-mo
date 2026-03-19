package com.mycompany.controllers.api;

import com.mycompany.models.Category;
import com.mycompany.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
public class ApiCategoryController {

    @Autowired
    private CategoryService categoryService;

    // GET /api/v1/categories — Lấy tất cả danh mục
    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        List<Category> categories = categoryService.getAll();
        return ResponseEntity.ok(categories);
    }

    // GET /api/v1/categories/{id} — Lấy danh mục theo mã
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") int id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy danh mục với mã: " + id));
        }
        return ResponseEntity.ok(category);
    }

    // POST /api/v1/categories — Thêm danh mục mới
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Category category) {
        Map<String, Object> response = new HashMap<>();

        if (category.getTenloai() == null || category.getTenloai().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Tên danh mục không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        boolean ok = categoryService.create(category);
        if (ok) {
            response.put("success", true);
            response.put("message", "Thêm danh mục thành công!");
            response.put("data", category);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("success", false);
            response.put("message", "Thêm danh mục thất bại!");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // PUT /api/v1/categories/{id} — Cập nhật danh mục
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable("id") int id,
            @RequestBody Category category) {
        Map<String, Object> response = new HashMap<>();

        category.setMaloai(id);

        boolean ok = categoryService.update(category);
        if (ok) {
            response.put("success", true);
            response.put("message", "Cập nhật danh mục thành công!");
            response.put("data", category);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Cập nhật thất bại! Danh mục không tồn tại.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // DELETE /api/v1/categories/{id} — Xóa danh mục
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") int id) {
        Map<String, Object> response = new HashMap<>();

        boolean ok = categoryService.delete(id);
        if (ok) {
            response.put("success", true);
            response.put("message", "Xóa danh mục thành công!");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Xóa thất bại! Danh mục đang được sử dụng.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
