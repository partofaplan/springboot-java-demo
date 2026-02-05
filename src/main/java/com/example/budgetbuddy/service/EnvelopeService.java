package com.example.budgetbuddy.service;

import com.example.budgetbuddy.model.Envelope;
import com.example.budgetbuddy.model.User;
import com.example.budgetbuddy.repository.EnvelopeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EnvelopeService {

    private final EnvelopeRepository envelopeRepository;

    public EnvelopeService(EnvelopeRepository envelopeRepository) {
        this.envelopeRepository = envelopeRepository;
    }

    public List<Envelope> getEnvelopesForUser(Long userId) {
        return envelopeRepository.findByUserIdOrderByNameAsc(userId);
    }

    public Optional<Envelope> getEnvelopeForUser(Long envelopeId, Long userId) {
        return envelopeRepository.findByIdAndUserId(envelopeId, userId);
    }

    public Envelope createEnvelope(Envelope envelope, User user) {
        envelope.setUser(user);
        envelope.setCurrentAmount(BigDecimal.ZERO);
        return envelopeRepository.save(envelope);
    }

    @Transactional
    public void deleteEnvelope(Long envelopeId, Long userId) {
        envelopeRepository.findByIdAndUserId(envelopeId, userId)
                .ifPresent(envelopeRepository::delete);
    }

    @Transactional
    public void adjustAmount(Long envelopeId, BigDecimal adjustment) {
        envelopeRepository.findById(envelopeId).ifPresent(envelope -> {
            envelope.setCurrentAmount(envelope.getCurrentAmount().add(adjustment));
            envelopeRepository.save(envelope);
        });
    }
}
