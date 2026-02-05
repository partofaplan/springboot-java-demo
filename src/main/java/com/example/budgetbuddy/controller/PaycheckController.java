package com.example.budgetbuddy.controller;

import com.example.budgetbuddy.dto.AllocationDTO;
import com.example.budgetbuddy.dto.PaycheckForm;
import com.example.budgetbuddy.model.Envelope;
import com.example.budgetbuddy.model.Paycheck;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.service.EnvelopeService;
import com.example.budgetbuddy.service.PaycheckService;
import com.example.budgetbuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/paychecks")
public class PaycheckController {

    private final PaycheckService paycheckService;
    private final EnvelopeService envelopeService;
    private final UserService userService;

    public PaycheckController(PaycheckService paycheckService,
                              EnvelopeService envelopeService,
                              UserService userService) {
        this.paycheckService = paycheckService;
        this.envelopeService = envelopeService;
        this.userService = userService;
    }

    @GetMapping
    public String listPaychecks(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Paycheck> paychecks = paycheckService.getPaychecksForUser(user.getId());
        model.addAttribute("paychecks", paychecks);
        return "paychecks";
    }

    @GetMapping("/new")
    public String newPaycheckForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Envelope> envelopes = envelopeService.getEnvelopesForUser(user.getId());

        PaycheckForm form = new PaycheckForm();
        List<AllocationDTO> allocations = envelopes.stream()
                .map(e -> new AllocationDTO(e.getId(), e.getName()))
                .toList();
        form.setAllocations(allocations);

        model.addAttribute("paycheckForm", form);
        return "paycheck-new";
    }

    @PostMapping
    public String addPaycheck(@AuthenticationPrincipal UserDetails userDetails,
                              @Valid @ModelAttribute("paycheckForm") PaycheckForm form,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        User user = userService.findByUsername(userDetails.getUsername());

        if (result.hasErrors()) {
            return "paycheck-new";
        }

        try {
            Paycheck paycheck = new Paycheck();
            paycheck.setDescription(form.getDescription());
            paycheck.setAmount(form.getAmount());
            paycheck.setDate(form.getDate());

            paycheckService.addPaycheck(paycheck, user, form.getAllocations());
            redirectAttributes.addFlashAttribute("success", "Paycheck added and funds allocated");
            return "redirect:/paychecks";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "paycheck-new";
        }
    }

    @PostMapping("/{id}/delete")
    public String deletePaycheck(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        paycheckService.deletePaycheck(id, user.getId());
        redirectAttributes.addFlashAttribute("success", "Paycheck deleted and allocations reversed");
        return "redirect:/paychecks";
    }
}
