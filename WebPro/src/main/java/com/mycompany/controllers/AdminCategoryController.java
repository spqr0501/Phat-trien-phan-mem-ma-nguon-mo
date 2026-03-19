// AdminCategoryController.java - Enhanced with AJAX response
package com.mycompany.controllers;

import com.mycompany.models.Category;
import com.mycompany.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    // Danh sách loại hàng
    @GetMapping("/category")
    public String listFragment(Model model) {
        model.addAttribute("listCat", categoryService.getAll());
        return "admin/fragments/category-list";
    }

    // Xem chi tiết loại hàng
    @GetMapping("/category/detail/{id}")
    public String detailFragment(@PathVariable("id") int id, Model model) {
        Category category = categoryService.findById(id);
        if (category == null) {
            model.addAttribute("error", "Không tìm thấy loại hàng!");
            return listFragment(model);
        }
        model.addAttribute("category", category);
        return "admin/fragments/category-detail";
    }

    // Form thêm/sửa loại hàng
    @GetMapping({ "/category-add", "/category/edit/{id}" })
    public String categoryForm(@PathVariable(required = false) Integer id, Model model) {
        Category category = (id == null) ? new Category() : categoryService.findById(id);
        if (id != null && category == null) {
            model.addAttribute("error", "Không tìm thấy loại hàng!");
            return listFragment(model);
        }
        model.addAttribute("category", category);
        model.addAttribute("action", id == null ? "/admin/category-add" : "/admin/category/update");
        model.addAttribute("title", id == null ? "Thêm loại hàng mới" : "Chỉnh sửa loại hàng");
        return "admin/fragments/category-form :: content";
    }

    // Thêm loại hàng mới
    @PostMapping("/category-add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> add(@ModelAttribute Category cat) {
        Map<String, Object> response = new HashMap<>();
        boolean success = categoryService.create(cat);
        response.put("success", success);
        response.put("message",
                success ? "Thêm loại hàng thành công!" : "Thêm thất bại! Vui lòng kiểm tra lại thông tin.");
        return ResponseEntity.ok(response);
    }

    // Cập nhật loại hàng
    @PostMapping("/category/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> update(@ModelAttribute Category cat) {
        Map<String, Object> response = new HashMap<>();
        boolean success = categoryService.update(cat);
        response.put("success", success);
        response.put("message", success ? "Cập nhật thành công!" : "Cập nhật thất bại! Vui lòng kiểm tra lại.");
        return ResponseEntity.ok(response);
    }

    // Xóa loại hàng
    @GetMapping("/category/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") int id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = categoryService.delete(id);
        response.put("success", success);
        response.put("message", success ? "Xóa thành công!" : "Xóa thất bại! Có thể đang được sử dụng.");
        return ResponseEntity.ok(response);
    }
}