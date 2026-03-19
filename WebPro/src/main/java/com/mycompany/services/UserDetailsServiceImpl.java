package com.mycompany.services;

import com.mycompany.models.User;
import com.mycompany.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = userRepository.findById(username)
                .orElseThrow(() -> 
                    new UsernameNotFoundException("Không tìm thấy người dùng: " + username)
                );

        // Vì class User đã implements UserDetails rồi → trả về luôn được
        return user;
    }
}