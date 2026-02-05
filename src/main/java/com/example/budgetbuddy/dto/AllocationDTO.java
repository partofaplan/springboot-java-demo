package com.example.budgetbuddy.dto;

import java.math.BigDecimal;

public class AllocationDTO {

    private Long envelopeId;
    private String envelopeName;
    private BigDecimal amount;

    public AllocationDTO() {}

    public AllocationDTO(Long envelopeId, String envelopeName) {
        this.envelopeId = envelopeId;
        this.envelopeName = envelopeName;
        this.amount = BigDecimal.ZERO;
    }

    public Long getEnvelopeId() { return envelopeId; }
    public void setEnvelopeId(Long envelopeId) { this.envelopeId = envelopeId; }

    public String getEnvelopeName() { return envelopeName; }
    public void setEnvelopeName(String envelopeName) { this.envelopeName = envelopeName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
