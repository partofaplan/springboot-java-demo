package com.example.budgetbuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "paycheck_allocation")
public class PaycheckAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paycheck_id", nullable = false)
    private Paycheck paycheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "envelope_id", nullable = false)
    private Envelope envelope;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    public PaycheckAllocation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Paycheck getPaycheck() { return paycheck; }
    public void setPaycheck(Paycheck paycheck) { this.paycheck = paycheck; }

    public Envelope getEnvelope() { return envelope; }
    public void setEnvelope(Envelope envelope) { this.envelope = envelope; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
