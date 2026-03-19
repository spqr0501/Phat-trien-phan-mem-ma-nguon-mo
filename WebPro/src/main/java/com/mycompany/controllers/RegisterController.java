package com.mycompany.controllers;

import com.mycompany.models.Customer;
import com.mycompany.models.User;
import com.mycompany.services.CustomerService;
import com.mycompany.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // trang form đăng ký
    }

    @PostMapping("/register")
    public String register(User user, Model model) {
        
        // Kiểm tra tên đăng nhập đã tồn tại chưa
        if (userService.findById(user.getTendangnhap()) != null) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.");
            return "register"; // trả lại form với lỗi
        }

        // Kiểm tra mật khẩu không để trống
        if (user.getMatkhau() == null || user.getMatkhau().trim().isEmpty()) {
            model.addAttribute("error", "Mật khẩu không được để trống!");
            return "register";
        }

        // Tự động set là user thường (không phải admin)
        user.setAdmin(false);

        // Lưu vào DB (pass lưu dạng thường vì đã tắt BCrypt)
        userService.create(user);

        // Auto-create Customer record for admin/customer list
        // Admin hiện đang quản lý bảng `khachhang` (entity Customer), nên cần tạo song song khi register.
        try {
            Customer customer = new Customer();
            customer.setMakh(user.getTendangnhap());
            customer.setTenkh(user.getHoten());
            customer.setDiachi("");
            customer.setSdt("");
            customer.setEmail(user.getEmail());
            customerService.create(customer);
        } catch (Exception ignore) {
            // Nếu trùng/không hợp lệ thì bỏ qua, admin vẫn có thể thêm thủ công.
        }

        // Chuyển hướng về login với thông báo thành công
        return "redirect:/login?registered";
    }
}