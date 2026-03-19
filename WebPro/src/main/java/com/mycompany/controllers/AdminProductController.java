// AdminProductController.java - Enhanced with AJAX response
package com.mycompany.controllers;

import com.mycompany.models.Product;
import com.mycompany.services.CategoryService;
import com.mycompany.services.Helper;
import com.mycompany.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // Danh sách sản phẩm
    @GetMapping("/product")
    public String list(Model model) {
        model.addAttribute("listPro", productService.getAll());
        return "admin/fragments/product-list";
    }

    // Xem chi tiết sản phẩm
    @GetMapping("/product/detail/{id}")
    public String detailFragment(@PathVariable("id") String id, Model model) {
        Product product = productService.findByMahh(id);
        if (product == null) {
            model.addAttribute("error", "Không tìm thấy sản phẩm!");
            return list(model);
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAll());
        return "admin/fragments/product-detail";
    }

    // Form thêm + sửa sản phẩm
    @GetMapping({ "/product-add", "/product/edit/{id}" })
    public String form(@PathVariable(required = false) String id, Model model) {
        Product pro = id == null ? new Product() : productService.findByMahh(id);

        if (id != null && pro == null) {
            model.addAttribute("error", "Không tìm thấy sản phẩm!");
            model.addAttribute("listPro", productService.getAll());
            return "admin/fragments/product-list";
        }

        model.addAttribute("product", pro);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("action", id == null ? "/admin/product-add" : "/admin/product/update");
        model.addAttribute("title", id == null ? "Thêm sản phẩm mới" : "Chỉnh sửa sản phẩm");

        return "admin/fragments/product-form :: content";
    }

    // Thêm sản phẩm mới
    @PostMapping("/product-add")
    @ResponseBody
    public Map<String, Object> add(@ModelAttribute Product pro,
            @RequestParam(value = "filename", required = false) MultipartFile file) {
        Map<String, Object> resp = new HashMap<>();
        try {
            // Validation: Kiểm tra tên sản phẩm
            if (pro.getTenhh() == null || pro.getTenhh().trim().isEmpty()) {
                resp.put("success", false);
                resp.put("message", "Tên sản phẩm không được để trống!");
                return resp;
            }

            // Validation: Kiểm tra đơn giá
            if (pro.getDongia() <= 0) {
                resp.put("success", false);
                resp.put("message", "Đơn giá phải lớn hơn 0!");
                return resp;
            }

            if (file != null && !file.isEmpty()) {
                String uploadDir = System.getProperty("user.dir") + "/uploads/images/";
                String fileName = Helper.saveFile(uploadDir, file, 20);
                pro.setHinh(fileName);
            }
            boolean ok = productService.create(pro);
            resp.put("success", ok);
            resp.put("message", ok ? "Thêm sản phẩm thành công!" : "Mã hàng đã tồn tại hoặc có lỗi xảy ra!");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "Lỗi hệ thống: " + e.getMessage());
        }
        return resp;
    }

    // Cập nhật sản phẩm
    @PostMapping("/product/update")
    @ResponseBody
    public Map<String, Object> update(@ModelAttribute Product pro,
            @RequestParam(value = "filename", required = false) MultipartFile file,
            @RequestParam(value = "oldImage", required = false) String oldImage) {
        Map<String, Object> resp = new HashMap<>();
        try {
            // Validation: Kiểm tra tên sản phẩm
            if (pro.getTenhh() == null || pro.getTenhh().trim().isEmpty()) {
                resp.put("success", false);
                resp.put("message", "Tên sản phẩm không được để trống!");
                return resp;
            }

            // Validation: Kiểm tra đơn giá
            if (pro.getDongia() <= 0) {
                resp.put("success", false);
                resp.put("message", "Đơn giá phải lớn hơn 0!");
                return resp;
            }

            // CRITICAL FIX: Lấy sản phẩm hiện tại từ database để giữ lại hình ảnh cũ
            Product existingProduct = productService.findByMahh(pro.getMahh());
            if (existingProduct == null) {
                resp.put("success", false);
                resp.put("message", "Không tìm thấy sản phẩm!");
                return resp;
            }

            // DEBUG: Log file upload status
            System.out.println("========== DEBUG IMAGE UPDATE ==========");
            System.out.println("File received: " + (file != null ? file.getOriginalFilename() : "null"));
            System.out.println("File empty: " + (file != null ? file.isEmpty() : "N/A"));
            System.out.println("Old image from DB: " + existingProduct.getHinh());
            System.out.println("Old image from form: " + oldImage);

            // Xử lý hình ảnh: chỉ cập nhật khi có file mới
            if (file != null && !file.isEmpty()) {
                String uploadDir = System.getProperty("user.dir") + "/uploads/images/";
                String fileName = Helper.saveFile(uploadDir, file, 20);
                pro.setHinh(fileName);
                System.out.println("NEW image saved: " + fileName);
            } else {
                // Giữ lại hình ảnh cũ từ database
                pro.setHinh(existingProduct.getHinh());
                System.out.println("Keeping OLD image: " + existingProduct.getHinh());
            }

            System.out.println("Image before UPDATE: " + pro.getHinh());
            boolean ok = productService.update(pro);
            System.out.println("Update result: " + ok);
            System.out.println("========================================");

            resp.put("success", ok);
            resp.put("message", ok ? "Cập nhật sản phẩm thành công!" : "Cập nhật thất bại! Vui lòng kiểm tra lại.");
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "Lỗi hệ thống: " + e.getMessage());
        }
        return resp;
    }

    // Xóa sản phẩm
    @GetMapping("/product/delete/{id}")
    @ResponseBody
    public Map<String, Object> delete(@PathVariable("id") String id) {
        Map<String, Object> resp = new HashMap<>();
        boolean ok = productService.delete(id);
        resp.put("success", ok);
        resp.put("message", ok ? "Xóa sản phẩm thành công!" : "Xóa thất bại! Sản phẩm đang được sử dụng.");
        return resp;
    }
}