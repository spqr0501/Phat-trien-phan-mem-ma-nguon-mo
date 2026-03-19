package com.mycompany.controllers.api;

import com.mycompany.models.User;
import com.mycompany.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class ApiUserController {

    @Autowired
    private UserService userService;

    // GET /api/v1/users — Lấy tất cả người dùng
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    // GET /api/v1/users/{username} — Lấy người dùng theo username
    @GetMapping("/{username}")
    public ResponseEntity<?> getByUsername(@PathVariable("username") String username) {
        User user = userService.findById(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy người dùng: " + username));
        }
        return ResponseEntity.ok(user);
    }

    // POST /api/v1/users — Tạo người dùng mới
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (user.getTendangnhap() == null || user.getTendangnhap().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Tên đăng nhập không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        if (user.getMatkhau() == null || user.getMatkhau().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Mật khẩu không được để trống!");
            return ResponseEntity.badRequest().body(response);
        }

        boolean ok = userService.create(user);
        if (ok) {
            response.put("success", true);
            response.put("message", "Tạo người dùng thành công!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("success", false);
            response.put("message", "Tên đăng nhập đã tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // PUT /api/v1/users/{username} — Cập nhật người dùng
    @PutMapping("/{username}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable("username") String username,
            @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        user.setTendangnhap(username);

        boolean ok = userService.update(user);
        if (ok) {
            response.put("success", true);
            response.put("message", "Cập nhật người dùng thành công!");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Cập nhật thất bại! Người dùng không tồn tại.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // DELETE /api/v1/users/{username} — Xóa người dùng
    @DeleteMapping("/{username}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("username") String username) {
        Map<String, Object> response = new HashMap<>();

        boolean ok = userService.delete(username);
        if (ok) {
            response.put("success", true);
            response.put("message", "Xóa người dùng thành công!");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Xóa thất bại! Người dùng không tồn tại.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
