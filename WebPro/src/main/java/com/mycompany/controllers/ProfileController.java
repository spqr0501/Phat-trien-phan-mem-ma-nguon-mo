package com.mycompany.controllers;

import com.mycompany.models.User;
import com.mycompany.models.Customer;
import com.mycompany.services.CustomerService;
import com.mycompany.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    // Lưu avatar vào uploads để map sẵn theo /Images/** (xem MvcConfig)
    private static final String UPLOAD_DIR = "uploads/images/avatars/";

    // Show profile page
    @GetMapping
    public String profilePage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    // Update profile information
    @PostMapping("/update")
    public String updateProfile(
            @RequestParam String hoten,
            @RequestParam String sodienthoai,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/profile";
            }

            // Update user information
            user.setHoten(hoten);
            user.setSodienthoai(sodienthoai);

            userService.save(user);

            // Sync to Customer table (admin /admin/customer)
            // Customer.makh đang được tạo bằng tendangnhap (xem RegisterController).
            try {
                Customer customer = customerService.findById(username);
                if (customer != null) {
                    customer.setTenkh(hoten);
                    customer.setSdt(sodienthoai);
                    // email có thể lưu/đồng bộ thêm nếu muốn
                    customer.setEmail(user.getEmail());
                    customerService.update(customer);
                }
            } catch (Exception ignore) {
                // Nếu không có customer record hoặc update thất bại, vẫn cho phép profile cập nhật.
            }

            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    // Upload avatar
    @PostMapping("/avatar")
    public String uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file!");
                return "redirect:/profile";
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                redirectAttributes.addFlashAttribute("error", "Only JPEG and PNG images are allowed!");
                return "redirect:/profile";
            }

            // Validate file size (max 2MB)
            if (file.getSize() > 2 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("error", "File size must be less than 2MB!");
                return "redirect:/profile";
            }

            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/profile";
            }

            // Create upload directory if not exists
            String uploadDirAbsPath = System.getProperty("user.dir") + "/" + UPLOAD_DIR;
            File uploadDir = new File(uploadDirAbsPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String newFilename = "avatar_" + user.getTendangnhap() + "_" + UUID.randomUUID() + fileExtension;

            // Save file
            Path filePath = Paths.get(uploadDirAbsPath, newFilename);
            Files.write(filePath, file.getBytes());

            // Update user avatar path (store relative path)
            user.setAvatar("/Images/avatars/" + newFilename);
            userService.save(user);

            redirectAttributes.addFlashAttribute("success", "Avatar uploaded successfully!");

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error uploading avatar: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    // API: Get current user's avatar URL
    @GetMapping("/api/avatar")
    @ResponseBody
    public java.util.Map<String, Object> getAvatar(Authentication authentication) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(authentication.getName());
            if (user != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                response.put("avatar", user.getAvatar());
            }
        }
        return response;
    }

    // Backward compatibility for admin template (it calls /profile/api/avatar)
    @GetMapping("/profile/api/avatar")
    @ResponseBody
    public java.util.Map<String, Object> getAvatarLegacy(Authentication authentication) {
        return getAvatar(authentication);
    }
}
