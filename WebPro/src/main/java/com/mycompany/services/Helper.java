package com.mycompany.services;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class Helper {

    public static String saveFile(String uploadDir, MultipartFile file, int len) throws IOException {
        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate random filename
        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf('.'));
        String fileName = randomString(len - ext.length()) + ext;

        // Save file
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath.toFile());

        System.out.println("✅ Image saved: " + filePath.toAbsolutePath());
        return fileName;
    }

    private static String randomString(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}