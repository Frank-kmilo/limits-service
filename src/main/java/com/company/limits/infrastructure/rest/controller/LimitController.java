package com.company.limits.infrastructure.rest.controller;


import com.company.limits.application.usecase.CreateLimitUseCase;
import com.company.limits.domain.model.LimitConfiguration;
import com.company.limits.infrastructure.rest.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("v1/limits")
@RequiredArgsConstructor
public class LimitController {

    private final CreateLimitUseCase createLimitUseCase;
    @PostMapping
    @Operation(summary = "Crear configuración de límite", description = "Crea límites diarios, mensuales y por transacción para un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "000", description = "Configuración creada exitosamente",
                    content = @Content(schema = @Schema(implementation = CreateLimitResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    public ResponseEntity<ApiResponseDto<CreateLimitResponse>>  createLimit(
            @RequestBody CreateLimitRequest request
    ) {

        LimitConfiguration configuration =
                createLimitUseCase.execute(
                        request.getCustomerId(),
                        request.getCurrencyCode(),
                        request.getCountryCode(),
                        request.getDailyLimitAmount(),
                        request.getMonthlyLimitAmount(),
                        request.getTransactionLimitAmount()
                );

        CreateLimitResponse response =
                new CreateLimitResponse(
                        configuration.getId().toString(),
                        "CREATED",
                        configuration.getCurrencyCode().getValue(),
                        configuration.getCountryCode().getValue(),
                        configuration.getDailyLimit().getAmountInCents(),
                        configuration.getMonthlyLimit().getAmountInCents(),
                        configuration.getTransactionLimit().getAmountInCents()
                );


        ApiResponseDto<CreateLimitResponse> apiResponse = new ApiResponseDto<>(
                "000",
                "Limit configuration created",
                response
        );

        return ResponseEntity.ok(apiResponse);
    }
}