package com.example.budgetbuddy.controller;

import com.example.budgetbuddy.model.Envelope;
import com.example.budgetbuddy.model.Expense;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.service.EnvelopeService;
import com.example.budgetbuddy.service.ExpenseService;
import com.example.budgetbuddy.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class DashboardController {

    private final UserService userService;
    private final EnvelopeService envelopeService;
    private final ExpenseService expenseService;

    public DashboardController(UserService userService,
                               EnvelopeService envelopeService,
                               ExpenseService expenseService) {
        this.userService = userService;
        this.envelopeService = envelopeService;
        this.expenseService = expenseService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Envelope> envelopes = envelopeService.getEnvelopesForUser(user.getId());
        List<Expense> expenses = expenseService.getExpensesForUser(user.getId());

        BigDecimal totalBudget = envelopes.stream()
                .map(Envelope::getBudgetThreshold)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRemaining = envelopes.stream()
                .map(Envelope::getCurrentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("envelopes", envelopes);
        model.addAttribute("expenses", expenses);
        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("totalRemaining", totalRemaining);
        return "dashboard";
    }
}
