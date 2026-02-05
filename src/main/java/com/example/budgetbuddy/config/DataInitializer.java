package com.example.budgetbuddy.config;

import com.example.budgetbuddy.model.Envelope;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.EnvelopeRepository;
import com.example.budgetbuddy.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      EnvelopeRepository envelopeRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("demo")) {
                User demo = new User("demo", passwordEncoder.encode("demo"));
                demo = userRepository.save(demo);

                String[][] envelopes = {
                        {"Groceries", "500.00"},
                        {"Rent", "1500.00"},
                        {"Transport", "200.00"},
                        {"Utilities", "150.00"},
                        {"Entertainment", "100.00"},
                };
                for (String[] env : envelopes) {
                    Envelope envelope = new Envelope();
                    envelope.setName(env[0]);
                    envelope.setBudgetThreshold(new BigDecimal(env[1]));
                    envelope.setCurrentAmount(BigDecimal.ZERO);
                    envelope.setUser(demo);
                    envelopeRepository.save(envelope);
                }
            }
        };
    }
}
