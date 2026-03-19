package com.mycompany.controllers;

import com.mycompany.models.User;
import com.mycompany.services.Helper;
import com.mycompany.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminProfileController {

    @Autowired
    private UserService userService;

    // Hiển thị form profile
    @GetMapping("/profile")
    public String showProfile(Model model) {
        // Lấy username của người dùng hiện tại từ Security Context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng!");
            return "admin/fragments/profile-form";
        }

        model.addAttribute("user", user);
        return "admin/fragments/profile-form";
    }

    // Cập nhật profile
    @PostMapping("/profile/update")
    @ResponseBody
    public Map<String, Object> updateProfile(
            @ModelAttribute User user,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Lấy username từ Security Context
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName();

            // Set username cho user object
            user.setTendangnhap(currentUsername);

            // Xử lý upload avatar nếu có
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/Images/avatars/";
                String fileName = Helper.saveFile(uploadDir, avatarFile, 20);
                user.setAvatar(fileName);
            }

            // Cập nhật profile
            boolean success = userService.updateProfile(user);

            if (success) {
                response.put("success", true);
                response.put("message", "Cập nhật hồ sơ thành công!");
            } else {
                response.put("success", false);
                response.put("message", "Cập nhật hồ sơ thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        return response;
    }

    // Đổi mật khẩu
    @PostMapping("/profile/change-password")
    @ResponseBody
    public Map<String, Object> changePassword(@RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Validation: Kiểm tra mật khẩu mới trùng khớp
            if (!newPassword.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "Mật khẩu mới không trùng khớp!");
                return response;
            }

            // Kiểm tra độ dài mật khẩu
            if (newPassword.length() < 6) {
                response.put("success", false);
                response.put("message", "Mật khẩu phải có ít nhất 6 ký tự!");
                return response;
            }

            // Lấy username từ Security Context
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName();

            // Đổi mật khẩu
            boolean success = userService.changePassword(currentUsername, newPassword);

            if (success) {
                response.put("success", true);
                response.put("message", "Đổi mật khẩu thành công!");
            } else {
                response.put("success", false);
                response.put("message", "Đổi mật khẩu thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        return response;
    }
}
