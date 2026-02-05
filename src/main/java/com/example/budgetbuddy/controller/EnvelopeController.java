package com.example.budgetbuddy.controller;

import com.example.budgetbuddy.model.Envelope;
import com.example.budgetbuddy.model.Expense;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.service.EnvelopeService;
import com.example.budgetbuddy.service.ExpenseService;
import com.example.budgetbuddy.service.UserService;
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
@RequestMapping("/envelopes")
public class EnvelopeController {

    private final EnvelopeService envelopeService;
    private final ExpenseService expenseService;
    private final UserService userService;

    public EnvelopeController(EnvelopeService envelopeService,
                              ExpenseService expenseService,
                              UserService userService) {
        this.envelopeService = envelopeService;
        this.expenseService = expenseService;
        this.userService = userService;
    }

    @GetMapping
    public String listEnvelopes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Envelope> envelopes = envelopeService.getEnvelopesForUser(user.getId());
        model.addAttribute("envelopes", envelopes);
        model.addAttribute("envelope", new Envelope());
        return "envelopes";
    }

    @PostMapping
    public String createEnvelope(@AuthenticationPrincipal UserDetails userDetails,
                                 @Valid @ModelAttribute("envelope") Envelope envelope,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            User user = userService.findByUsername(userDetails.getUsername());
            model.addAttribute("envelopes", envelopeService.getEnvelopesForUser(user.getId()));
            return "envelopes";
        }
        User user = userService.findByUsername(userDetails.getUsername());
        envelopeService.createEnvelope(envelope, user);
        redirectAttributes.addFlashAttribute("success", "Envelope created");
        return "redirect:/envelopes";
    }

    @GetMapping("/{id}")
    public String envelopeDetail(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable Long id,
                                 Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        Envelope envelope = envelopeService.getEnvelopeForUser(id, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Envelope not found"));

        List<Expense> expenses = expenseService.getExpensesForEnvelope(id);

        BigDecimal totalSpent = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("envelope", envelope);
        model.addAttribute("expenses", expenses);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("expense", new Expense());
        return "envelope-detail";
    }

    @PostMapping("/{id}/add-expense")
    public String addExpense(@AuthenticationPrincipal UserDetails userDetails,
                             @PathVariable Long id,
                             @Valid @ModelAttribute("expense") Expense expense,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        User user = userService.findByUsername(userDetails.getUsername());

        if (result.hasErrors()) {
            Envelope envelope = envelopeService.getEnvelopeForUser(id, user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Envelope not found"));
            model.addAttribute("envelope", envelope);
            model.addAttribute("expenses", expenseService.getExpensesForEnvelope(id));
            model.addAttribute("totalSpent", BigDecimal.ZERO);
            return "envelope-detail";
        }

        expenseService.addExpense(expense, user, id);
        redirectAttributes.addFlashAttribute("success", "Expense added");
        return "redirect:/envelopes/" + id;
    }

    @PostMapping("/{envelopeId}/expenses/{expenseId}/delete")
    public String deleteExpense(@AuthenticationPrincipal UserDetails userDetails,
                                @PathVariable Long envelopeId,
                                @PathVariable Long expenseId,
                                RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        expenseService.deleteExpense(expenseId, user.getId());
        redirectAttributes.addFlashAttribute("success", "Expense deleted");
        return "redirect:/envelopes/" + envelopeId;
    }

    @PostMapping("/{id}/delete")
    public String deleteEnvelope(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        envelopeService.deleteEnvelope(id, user.getId());
        redirectAttributes.addFlashAttribute("success", "Envelope deleted");
        return "redirect:/envelopes";
    }
}
