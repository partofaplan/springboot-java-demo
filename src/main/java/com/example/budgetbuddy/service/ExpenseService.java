package com.example.budgetbuddy.service;

import com.example.budgetbuddy.model.Envelope;
import com.example.budgetbuddy.model.Expense;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.EnvelopeRepository;
import com.example.budgetbuddy.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final EnvelopeRepository envelopeRepository;

    public ExpenseService(ExpenseRepository expenseRepository, EnvelopeRepository envelopeRepository) {
        this.expenseRepository = expenseRepository;
        this.envelopeRepository = envelopeRepository;
    }

    public List<Expense> getExpensesForUser(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId);
    }

    public List<Expense> getExpensesForEnvelope(Long envelopeId) {
        return expenseRepository.findByEnvelopeIdOrderByDateDesc(envelopeId);
    }

    @Transactional
    public Expense addExpense(Expense expense, User user, Long envelopeId) {
        Envelope envelope = envelopeRepository.findByIdAndUserId(envelopeId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Envelope not found"));

        expense.setUser(user);
        expense.setEnvelope(envelope);

        envelope.setCurrentAmount(envelope.getCurrentAmount().subtract(expense.getAmount()));
        envelopeRepository.save(envelope);

        return expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(Long expenseId, Long userId) {
        expenseRepository.findByIdAndUserId(expenseId, userId).ifPresent(expense -> {
            Envelope envelope = expense.getEnvelope();
            envelope.setCurrentAmount(envelope.getCurrentAmount().add(expense.getAmount()));
            envelopeRepository.save(envelope);
            expenseRepository.delete(expense);
        });
    }
}
