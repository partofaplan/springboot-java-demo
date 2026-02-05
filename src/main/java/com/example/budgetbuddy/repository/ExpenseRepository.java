package com.example.budgetbuddy.repository;

import com.example.budgetbuddy.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserIdOrderByDateDesc(Long userId);
    List<Expense> findByEnvelopeIdOrderByDateDesc(Long envelopeId);
    Optional<Expense> findByIdAndUserId(Long id, Long userId);
}
