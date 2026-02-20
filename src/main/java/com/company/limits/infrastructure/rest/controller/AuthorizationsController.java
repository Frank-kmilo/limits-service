package com.company.limits.infrastructure.rest.controller;

import com.company.limits.application.usecase.AuthorizeOperationUseCase;
import com.company.limits.application.usecase.GetAuthorizationUseCase;
import com.company.limits.domain.model.Authorization;
import com.company.limits.infrastructure.rest.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@RestController
@RequestMapping("v1/authorizations")
@RequiredArgsConstructor
public class AuthorizationsController {

    private final AuthorizeOperationUseCase authorizeOperationUseCase;
    private final GetAuthorizationUseCase getAuthorizationUseCase;

    @Operation(summary = "Autorizar operación", description = "Valida límites y retorna APPROVED o REJECTED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "404", description = "Authorization no encontrado")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDto<AuthorizeOperationResponse>> authorize(
            @RequestBody AuthorizeOperationRequest request
    ) {
        Authorization authorization = authorizeOperationUseCase.execute(
                request.getCustomerId(),
                request.getClientOperationId(),
                request.getAmount(),
                request.getCurrency(),
                request.getCountry(),
                request.getOperationTimestamp()
        );

        AuthorizeOperationResponse response = new AuthorizeOperationResponse(
                authorization.getDecision().name(),
                authorization.getReason(),
                authorization.getAmount().getAmountInCents(),
                authorization.getProcessedAt()
        );

        ApiResponseDto<AuthorizeOperationResponse> apiResponse = new ApiResponseDto<>(
                "000",
                authorization.getReason(),
                response
        );

        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Consultar autorización", description = "Retorna el resultado de una autorización existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Authorization no encontrado")
    })
    @GetMapping("/{authorization_id}")
    public ResponseEntity<ApiResponseDto<AuthorizeOperationResponse>> getAuthorization(
            @PathVariable("authorization_id") UUID authorizationId
    ) {
        Authorization authorization = getAuthorizationUseCase.execute(authorizationId);

        AuthorizeOperationResponse response = new AuthorizeOperationResponse(
                authorization.getDecision().name(),
                authorization.getReason(),
                authorization.getAmount().getAmountInCents(),
                authorization.getProcessedAt()
        );

        ApiResponseDto<AuthorizeOperationResponse> apiResponse = new ApiResponseDto<>(
                "000",
                "Successful operation)",
                response
        );

        return ResponseEntity.ok(apiResponse);
    }
}