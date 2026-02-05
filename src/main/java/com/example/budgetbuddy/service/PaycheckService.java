package com.example.budgetbuddy.service;

import com.example.budgetbuddy.dto.AllocationDTO;
import com.example.budgetbuddy.model.*;
import com.example.budgetbuddy.repository.EnvelopeRepository;
import com.example.budgetbuddy.repository.PaycheckAllocationRepository;
import com.example.budgetbuddy.repository.PaycheckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaycheckService {

    private final PaycheckRepository paycheckRepository;
    private final PaycheckAllocationRepository allocationRepository;
    private final EnvelopeRepository envelopeRepository;

    public PaycheckService(PaycheckRepository paycheckRepository,
                           PaycheckAllocationRepository allocationRepository,
                           EnvelopeRepository envelopeRepository) {
        this.paycheckRepository = paycheckRepository;
        this.allocationRepository = allocationRepository;
        this.envelopeRepository = envelopeRepository;
    }

    public List<Paycheck> getPaychecksForUser(Long userId) {
        return paycheckRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Transactional
    public Paycheck addPaycheck(Paycheck paycheck, User user, List<AllocationDTO> allocations) {
        BigDecimal totalAllocated = allocations.stream()
                .map(AllocationDTO::getAmount)
                .filter(a -> a != null && a.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAllocated.compareTo(paycheck.getAmount()) > 0) {
            throw new IllegalArgumentException("Allocations exceed paycheck amount");
        }

        paycheck.setUser(user);
        paycheck = paycheckRepository.save(paycheck);

        for (AllocationDTO dto : allocations) {
            if (dto.getAmount() != null && dto.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                Envelope envelope = envelopeRepository.findById(dto.getEnvelopeId())
                        .orElseThrow(() -> new IllegalArgumentException("Envelope not found"));

                envelope.setCurrentAmount(envelope.getCurrentAmount().add(dto.getAmount()));
                envelopeRepository.save(envelope);

                PaycheckAllocation allocation = new PaycheckAllocation();
                allocation.setPaycheck(paycheck);
                allocation.setEnvelope(envelope);
                allocation.setAmount(dto.getAmount());
                allocationRepository.save(allocation);
            }
        }

        return paycheck;
    }

    @Transactional
    public void deletePaycheck(Long paycheckId, Long userId) {
        paycheckRepository.findByIdAndUserId(paycheckId, userId).ifPresent(paycheck -> {
            for (PaycheckAllocation allocation : paycheck.getAllocations()) {
                Envelope envelope = allocation.getEnvelope();
                envelope.setCurrentAmount(envelope.getCurrentAmount().subtract(allocation.getAmount()));
                envelopeRepository.save(envelope);
            }
            paycheckRepository.delete(paycheck);
        });
    }
}
