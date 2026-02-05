package com.example.budgetbuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "envelope")
public class Envelope {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Envelope name is required")
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Budget threshold is required")
    @DecimalMin(value = "0.00", message = "Budget must be zero or positive")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal budgetThreshold;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "envelope", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date DESC")
    private List<Expense> expenses = new ArrayList<>();

    public Envelope() {}

    public double getPercentageUsed() {
        if (budgetThreshold == null || budgetThreshold.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        BigDecimal spent = budgetThreshold.subtract(currentAmount);
        return spent.divide(budgetThreshold, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();
    }

    public BigDecimal getAmountSpent() {
        if (budgetThreshold == null || currentAmount == null) return BigDecimal.ZERO;
        return budgetThreshold.subtract(currentAmount).max(BigDecimal.ZERO);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getBudgetThreshold() { return budgetThreshold; }
    public void setBudgetThreshold(BigDecimal budgetThreshold) { this.budgetThreshold = budgetThreshold; }

    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }
}
