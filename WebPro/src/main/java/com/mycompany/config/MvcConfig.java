package com.mycompany.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve images from external uploads folder
        String uploadPath = "file:///" + System.getProperty("user.dir") + "/uploads/images/";
        registry.addResourceHandler("/Images/**")
                // 1) Ưu tiên ảnh upload (để tương thích logic hiện tại)
                // 2) Fallback về classpath static/Images để dùng ảnh tĩnh (vd payment QR)
                .addResourceLocations(uploadPath, "classpath:/static/Images/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {

                String path = request.getRequestURI();

                // Chỉ áp dụng cho HTML pages (không phải static resources)
                if (!isStaticResource(path)) {
                    // Ngăn browser cache
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                    response.setHeader("Vary", "Cookie");
                }

                return true;
            }

            private boolean isStaticResource(String path) {
                return path.startsWith("/css/") ||
                        path.startsWith("/js/") ||
                        path.startsWith("/Images/") ||
                        path.startsWith("/images/") ||
                        path.startsWith("/fonts/") ||
                        path.startsWith("/static/") ||
                        path.endsWith(".css") ||
                        path.endsWith(".js") ||
                        path.endsWith(".png") ||
                        path.endsWith(".jpg") ||
                        path.endsWith(".gif") ||
                        path.endsWith(".ico") ||
                        path.endsWith(".woff") ||
                        path.endsWith(".woff2");
            }
        });
    }
}
