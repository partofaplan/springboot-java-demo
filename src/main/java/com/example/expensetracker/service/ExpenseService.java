package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> getExpensesForUser(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId);
    }

    public Expense addExpense(Expense expense, User user) {
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public Optional<Expense> getExpenseForUser(Long expenseId, Long userId) {
        return expenseRepository.findByIdAndUserId(expenseId, userId);
    }

    public void deleteExpense(Long expenseId, Long userId) {
        expenseRepository.findByIdAndUserId(expenseId, userId)
                .ifPresent(expenseRepository::delete);
    }
}
