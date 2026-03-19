package com.mycompany.controllers;

import com.mycompany.models.User;
import com.mycompany.services.PasswordResetService;
import com.mycompany.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetService resetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Show forgot password page
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    // Handle forgot password request
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByEmail(email);

            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "No account found with this email address.");
                return "redirect:/forgot-password";
            }

            // Generate reset token
            String token = resetService.createPasswordResetToken(user.getTendangnhap());

            // Send email (mock - prints to console)
            resetService.sendPasswordResetEmail(email, token);

            redirectAttributes.addFlashAttribute("success",
                    "Password reset link has been sent to your email. Check the console for the link.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred. Please try again.");
        }

        return "redirect:/forgot-password";
    }

    // Show reset password page
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        Optional<String> userId = resetService.validateTokenAndGetUserId(token);

        if (userId.isEmpty()) {
            model.addAttribute("error", "Invalid or expired reset link.");
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // Handle password reset
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        try {
            // Validate passwords match
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
                return "redirect:/reset-password?token=" + token;
            }

            // Validate password strength
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long!");
                return "redirect:/reset-password?token=" + token;
            }

            // Validate token
            Optional<String> userIdOpt = resetService.validateTokenAndGetUserId(token);
            if (userIdOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Invalid or expired reset link.");
                return "redirect:/login";
            }

            String userId = userIdOpt.get();

            // Update password
            User user = userService.findByUsername(userId);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found.");
                return "redirect:/login";
            }

            user.setMatkhau(passwordEncoder.encode(password));

            // Check if UserService has update method, use alternative if needed
            try {
                userService.updateUser(user);
            } catch (Exception e) {
                // Fallback: try save method or repository directly
                userService.save(user);
            }

            // Mark token as used
            resetService.markTokenAsUsed(token);

            redirectAttributes.addFlashAttribute("success",
                    "Password successfully reset! You can now login with your new password.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/reset-password?token=" + token;
        }

        return "redirect:/login";
    }
}
