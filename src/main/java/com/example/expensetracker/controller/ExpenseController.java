package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.service.ExpenseService;
import com.example.expensetracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;

    public ExpenseController(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    @GetMapping
    public String listExpenses(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Expense> expenses = expenseService.getExpensesForUser(user.getId());

        BigDecimal total = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("expenses", expenses);
        model.addAttribute("total", total);
        model.addAttribute("expense", new Expense());
        return "expenses";
    }

    @PostMapping
    public String addExpense(@AuthenticationPrincipal UserDetails userDetails,
                             @Valid @ModelAttribute("expense") Expense expense,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            User user = userService.findByUsername(userDetails.getUsername());
            List<Expense> expenses = expenseService.getExpensesForUser(user.getId());
            BigDecimal total = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("expenses", expenses);
            model.addAttribute("total", total);
            return "expenses";
        }

        User user = userService.findByUsername(userDetails.getUsername());
        expenseService.addExpense(expense, user);
        redirectAttributes.addFlashAttribute("success", "Expense added successfully");
        return "redirect:/expenses";
    }

    @PostMapping("/{id}/delete")
    public String deleteExpense(@AuthenticationPrincipal UserDetails userDetails,
                                @PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        expenseService.deleteExpense(id, user.getId());
        redirectAttributes.addFlashAttribute("success", "Expense deleted");
        return "redirect:/expenses";
    }
}
