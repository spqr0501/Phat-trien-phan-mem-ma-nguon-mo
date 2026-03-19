package com.mycompany.services;

import com.mycompany.models.User;
import java.util.List;

public interface UserService {
    List<User> getAll();

    User findById(String id);

    User findByUsername(String username);

    User findByEmail(String email);

    boolean create(User user);

    boolean update(User user);

    boolean updateProfile(User user);

    boolean changePassword(String username, String newPassword);

    boolean delete(String id);

    User save(User user);

    User updateUser(User user);
}