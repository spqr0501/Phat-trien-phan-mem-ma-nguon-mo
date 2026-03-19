// AdminCustomerController.java - Enhanced with AJAX response
package com.mycompany.controllers;

import com.mycompany.models.Customer;
import com.mycompany.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminCustomerController {

    @Autowired
    private CustomerService customerService;

    // Danh sách khách hàng
    @GetMapping("/customer")
    public String listFragment(Model model) {
        model.addAttribute("listCust", customerService.getAll());
        return "admin/fragments/customer-list";
    }

    // Xem chi tiết khách hàng
    @GetMapping("/customer/detail/{id}")
    public String detailFragment(@PathVariable("id") String id, Model model) {
        Customer customer = customerService.findById(id);
        if (customer == null) {
            model.addAttribute("error", "Không tìm thấy khách hàng!");
            return listFragment(model);
        }
        model.addAttribute("customer", customer);
        return "admin/fragments/customer-detail";
    }

    // Form thêm/sửa khách hàng
    @GetMapping({ "/customer-add", "/customer/edit/{id}" })
    public String customerForm(@PathVariable(required = false) String id, Model model) {
        Customer customer = (id == null) ? new Customer() : customerService.findById(id);
        if (id != null && customer == null) {
            model.addAttribute("error", "Không tìm thấy khách hàng!");
            return listFragment(model);
        }
        model.addAttribute("customer", customer);
        model.addAttribute("action", id == null ? "/admin/customer-add" : "/admin/customer/update");
        model.addAttribute("title", id == null ? "Thêm khách hàng mới" : "Chỉnh sửa khách hàng");
        return "admin/fragments/customer-form :: content";
    }

    // Thêm khách hàng mới
    @PostMapping("/customer-add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> add(@ModelAttribute Customer cust) {
        Map<String, Object> response = new HashMap<>();
        boolean success = customerService.create(cust);
        response.put("success", success);
        response.put("message",
                success ? "Thêm khách hàng thành công!" : "Thêm thất bại! Vui lòng kiểm tra lại thông tin.");
        return ResponseEntity.ok(response);
    }

    // Cập nhật khách hàng
    @PostMapping("/customer/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> update(@ModelAttribute Customer cust) {
        Map<String, Object> response = new HashMap<>();
        boolean success = customerService.update(cust);
        response.put("success", success);
        response.put("message", success ? "Cập nhật thành công!" : "Cập nhật thất bại! Vui lòng kiểm tra lại.");
        return ResponseEntity.ok(response);
    }

    // Xóa khách hàng
    @GetMapping("/customer/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        boolean success = customerService.delete(id);
        response.put("success", success);
        response.put("message", success ? "Xóa thành công!" : "Xóa thất bại! Có thể đang được sử dụng.");
        return ResponseEntity.ok(response);
    }
}