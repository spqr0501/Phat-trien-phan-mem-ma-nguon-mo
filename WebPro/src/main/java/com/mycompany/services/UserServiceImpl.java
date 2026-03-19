package com.mycompany.services;

import com.mycompany.models.User;
import com.mycompany.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findById(username).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean create(User user) {
        try {
            if (user.getTendangnhap() == null || user.getTendangnhap().trim().isEmpty()) {
                System.err.println("Lỗi: Tên đăng nhập không được để trống!");
                return false;
            }

            if (user.getMatkhau() == null || user.getMatkhau().trim().isEmpty()) {
                System.err.println("Lỗi: Mật khẩu không được để trống!");
                return false;
            }

            if (userRepository.existsById(user.getTendangnhap())) {
                System.err.println("Lỗi: Tên đăng nhập " + user.getTendangnhap() + " đã tồn tại!");
                return false;
            }

            userRepository.save(user);
            System.out.println("Thêm người dùng thành công: " + user.getTendangnhap());
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi thêm người dùng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(User user) {
        try {
            if (user.getTendangnhap() == null || user.getTendangnhap().trim().isEmpty()) {
                System.err.println("Lỗi: Tên đăng nhập không được để trống!");
                return false;
            }

            if (!userRepository.existsById(user.getTendangnhap())) {
                System.err.println("Lỗi: Không tìm thấy người dùng với tên đăng nhập " + user.getTendangnhap());
                return false;
            }

            if (user.getMatkhau() == null || user.getMatkhau().trim().isEmpty()) {
                User existingUser = userRepository.findById(user.getTendangnhap()).orElse(null);
                if (existingUser != null) {
                    user.setMatkhau(existingUser.getMatkhau());
                }
            }

            userRepository.save(user);
            System.out.println("Cập nhật người dùng thành công: " + user.getTendangnhap());
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật người dùng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateProfile(User user) {
        try {
            User existingUser = userRepository.findById(user.getTendangnhap()).orElse(null);
            if (existingUser == null) {
                System.err.println("Lỗi: Không tìm thấy người dùng!");
                return false;
            }

            existingUser.setHoten(user.getHoten());
            existingUser.setEmail(user.getEmail());
            existingUser.setSodienthoai(user.getSodienthoai());

            if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
                existingUser.setAvatar(user.getAvatar());
            }

            userRepository.save(existingUser);
            System.out.println("Cập nhật profile thành công: " + user.getTendangnhap());
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changePassword(String username, String newPassword) {
        try {
            User user = userRepository.findById(username).orElse(null);
            if (user == null) {
                System.err.println("Lỗi: Không tìm thấy người dùng!");
                return false;
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                System.err.println("Lỗi: Mật khẩu mới không được để trống!");
                return false;
            }

            user.setMatkhau(newPassword);
            userRepository.save(user);
            System.out.println("Đổi mật khẩu thành công: " + username);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi đổi mật khẩu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            if (!userRepository.existsById(id)) {
                System.err.println("Lỗi: Không tìm thấy người dùng với tên đăng nhập " + id);
                return false;
            }
            userRepository.deleteById(id);
            System.out.println("Xóa người dùng thành công: " + id);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa người dùng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (user == null || user.getTendangnhap() == null) {
            return null;
        }
        return userRepository.save(user);
    }
}