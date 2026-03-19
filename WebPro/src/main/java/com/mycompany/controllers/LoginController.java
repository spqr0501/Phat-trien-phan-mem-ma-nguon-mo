package com.mycompany.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";  // templates/login.html
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";  // templates/access-denied.html (tạo sau)
    }
}