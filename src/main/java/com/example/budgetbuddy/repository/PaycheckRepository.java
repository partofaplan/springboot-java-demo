package com.example.budgetbuddy.repository;

import com.example.budgetbuddy.model.Paycheck;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaycheckRepository extends JpaRepository<Paycheck, Long> {
    List<Paycheck> findByUserIdOrderByDateDesc(Long userId);
    Optional<Paycheck> findByIdAndUserId(Long id, Long userId);
}
