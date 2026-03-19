// AdminUserController.java - Enhanced with AJAX response
package com.mycompany.controllers;

import com.mycompany.models.User;
import com.mycompany.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserService userService;

    // Danh sách người dùng
    @GetMapping("/user")
    public String listFragment(Model model) {
        model.addAttribute("listUser", userService.getAll());
        return "admin/fragments/user-list";
    }

    // Xem chi tiết người dùng
    @GetMapping("/user/detail/{id}")
    public String detailFragment(@PathVariable("id") String id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            model.addAttribute("error", "Không tìm thấy người dùng!");
            return listFragment(model);
        }
        model.addAttribute("user", user);
        return "admin/fragments/user-detail";
    }

    // Form thêm/sửa người dùng
    @GetMapping({ "/user-add", "/user/edit/{id}" })
    public String userForm(@PathVariable(required = false) String id, Model model) {
        User user = (id == null) ? new User() : userService.findById(id);
        if (id != null && user == null) {
            model.addAttribute("error", "Không tìm thấy người dùng!");
            return listFragment(model);
        }
        model.addAttribute("user", user);
        model.addAttribute("action", id == null ? "/admin/user-add" : "/admin/user/update");
        model.addAttribute("title", id == null ? "Thêm người dùng mới" : "Chỉnh sửa người dùng");
        return "admin/fragments/user-form :: content";
    }

    // Thêm người dùng mới
    @PostMapping("/user-add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> add(@ModelAttribute User user) {
        Map<String, Object> response = new HashMap<>();
        boolean success = userService.create(user);
        response.put("success", success);
        response.put("message",
                success ? "Thêm người dùng thành công!" : "Thêm thất bại! Vui lòng kiểm tra lại thông tin.");
        return ResponseEntity.ok(response);
    }

    // Cập nhật người dùng
    @PostMapping("/user/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> update(@ModelAttribute User user) {
        Map<String, Object> response = new HashMap<>();
        boolean success = userService.update(user);
        response.put("success", success);
        response.put("message", success ? "Cập nhật thành công!" : "Cập nhật thất bại! Vui lòng kiểm tra lại.");
        return ResponseEntity.ok(response);
    }

    // Xóa người dùng
    @GetMapping("/user/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = userService.delete(id);
        response.put("success", success);
        response.put("message", success ? "Xóa thành công!" : "Xóa thất bại! Có thể đang được sử dụng.");
        return ResponseEntity.ok(response);
    }
}