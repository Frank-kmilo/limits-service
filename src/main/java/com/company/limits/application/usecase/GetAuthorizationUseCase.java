package com.company.limits.application.usecase;

import com.company.limits.domain.model.Authorization;
import com.company.limits.domain.port.out.AuthorizationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAuthorizationUseCase {

    private final AuthorizationRepositoryPort authorizationRepository;

    public Authorization execute(UUID authorizationId) {
        return authorizationRepository.findById(authorizationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Authorization not found for id: " + authorizationId
                ));
    }
}