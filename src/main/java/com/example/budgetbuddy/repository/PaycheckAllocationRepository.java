package com.example.budgetbuddy.repository;

import com.example.budgetbuddy.model.PaycheckAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaycheckAllocationRepository extends JpaRepository<PaycheckAllocation, Long> {
    List<PaycheckAllocation> findByPaycheckId(Long paycheckId);
}
