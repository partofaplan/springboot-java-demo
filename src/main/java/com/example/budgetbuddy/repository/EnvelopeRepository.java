package com.example.budgetbuddy.repository;

import com.example.budgetbuddy.model.Envelope;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnvelopeRepository extends JpaRepository<Envelope, Long> {
    List<Envelope> findByUserIdOrderByNameAsc(Long userId);
    Optional<Envelope> findByIdAndUserId(Long id, Long userId);
}
