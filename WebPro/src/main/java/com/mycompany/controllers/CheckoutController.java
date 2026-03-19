package com.mycompany.controllers;

import com.mycompany.models.OrderNew;
import com.mycompany.models.OrderItem;
import com.mycompany.models.Product;
import com.mycompany.services.OrderService;
import com.mycompany.services.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CheckoutController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // Trang checkout
    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model, RedirectAttributes redirectAttributes,
            Authentication authentication) {
        @SuppressWarnings("unchecked")
        Map<String, Integer> cart = (Map<String, Integer>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Giỏ hàng trống! Vui lòng thêm sản phẩm trước khi thanh toán.");
            return "redirect:/cart";
        }

        // Tính toán chi tiết đơn hàng
        Map<Product, Integer> cartDetails = new HashMap<>();
        long subtotal = 0;

        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            Product product = productService.findByMahh(entry.getKey());
            if (product != null) {
                cartDetails.put(product, entry.getValue());
                subtotal += product.getGiaHienTai() * entry.getValue();
            }
        }

        OrderNew order = new OrderNew();
        if (authentication != null && authentication.isAuthenticated()) {
            order.setUserId(authentication.getName());
            // Set default email/customerName if available in UserDetails service?
            // For now user has to fill form
        }

        model.addAttribute("cartDetails", cartDetails);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shippingFee", 30000);
        model.addAttribute("total", subtotal + 30000);
        model.addAttribute("order", order);

        return "checkout";
    }

    // Xử lý đặt hàng
    @PostMapping("/checkout/place-order")
    @Transactional
    public String placeOrder(
            @ModelAttribute OrderNew order,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        @SuppressWarnings("unchecked")
        Map<String, Integer> cart = (Map<String, Integer>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng trống!");
            return "redirect:/cart";
        }

        // Validate stock for all cart items BEFORE saving order/deduct stock
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String productId = entry.getKey();
            int qty = entry.getValue() != null ? entry.getValue() : 0;
            if (qty <= 0) continue;

            Product product = productService.findByMahh(productId);
            if (product == null) {
                redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại trong giỏ hàng.");
                return "redirect:/cart";
            }

            int availableStock = product.getSoluong() != null ? product.getSoluong() : 0;
            if (availableStock < qty) {
                redirectAttributes.addFlashAttribute("error",
                        "Sản phẩm '" + product.getTenhh() + "' chỉ còn " + availableStock + " trong kho.");
                return "redirect:/cart";
            }
        }

        if (authentication != null && authentication.isAuthenticated()) {
            order.setUserId(authentication.getName());
        } else {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để đặt hàng!");
            return "redirect:/login";
        }

        // Tạo các chi tiết đơn hàng
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            Product product = productService.findByMahh(entry.getKey());
            if (product != null) {
                OrderItem item = new OrderItem();
                item.setProduct(product);
                item.setQuantity(entry.getValue());

                // Convert int price to BigDecimal
                BigDecimal price = BigDecimal.valueOf(product.getGiaHienTai());
                item.setPrice(price);

                // Add to order (this sets relation)
                order.addOrderItem(item);

                // Add to total: price * quantity
                total = total.add(price.multiply(BigDecimal.valueOf(entry.getValue())));
            }
        }

        // Deduct stock after validation & before creating the order
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            Product product = productService.findByMahh(entry.getKey());
            if (product == null) continue;

            int qty = entry.getValue() != null ? entry.getValue() : 0;
            if (qty <= 0) continue;

            int availableStock = product.getSoluong() != null ? product.getSoluong() : 0;
            // Ensure non-negative after deduction
            product.setSoluong(Math.max(0, availableStock - qty));
            productService.update(product);
        }

        // Cộng phí ship 30k
        order.setTotalAmount(total.add(BigDecimal.valueOf(30000)));

        // Set trạng thái theo phương thức thanh toán
        String paymentMethod = order.getPaymentMethod();
        if ("BANK".equals(paymentMethod) || "MOMO".equals(paymentMethod)) {
            order.setStatus("Chờ xác nhận TT"); // Chờ admin xác nhận đã nhận tiền
        } else {
            order.setStatus("Pending"); // COD - chờ giao hàng
        }

        // Lưu đơn hàng vào database
        try {
            OrderNew savedOrder = orderService.createOrder(order);

            // Xóa giỏ hàng
            session.removeAttribute("cart");
            session.setAttribute("cartSize", 0);

            String paymentMsg = "";
            if ("BANK".equals(paymentMethod)) {
                paymentMsg = " (Chuyển khoản ngân hàng - Chờ xác nhận)";
            } else if ("MOMO".equals(paymentMethod)) {
                paymentMsg = " (MoMo - Chờ xác nhận)";
            } else {
                paymentMsg = " (Thanh toán khi nhận hàng)";
            }

            redirectAttributes.addFlashAttribute("success",
                    "Đặt hàng thành công! Mã đơn hàng: #" + savedOrder.getOrderId() + paymentMsg);
            redirectAttributes.addFlashAttribute("orderId", savedOrder.getOrderId());
            redirectAttributes.addFlashAttribute("paymentMethod", paymentMethod);

            return "redirect:/order-success";
        } catch (Exception e) {
            e.printStackTrace(); // Log error for debug
            redirectAttributes.addFlashAttribute("error",
                    "Có lỗi xảy ra khi đặt hàng: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    // Trang xác nhận đơn hàng
    @GetMapping("/order-success")
    public String orderSuccess(Model model) {
        return "order-success";
    }
}
