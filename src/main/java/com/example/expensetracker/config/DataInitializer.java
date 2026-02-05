package com.example.expensetracker.config;

import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("demo")) {
                User demo = new User("demo", passwordEncoder.encode("demo"));
                userRepository.save(demo);
            }
        };
    }
}
