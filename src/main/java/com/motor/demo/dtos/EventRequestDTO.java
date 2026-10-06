package com.motor.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

public record EventRequestDTO(
        @NotBlank(message= "EventId é obrigatório !")
        String eventId,
        @NotBlank(message= "Type é obrigatório !")
        String type,
        @NotBlank(message= "Source é obrigatório !")
        String source,
        @NotNull(message= "ocurredAt é obrigatório !")
        OffsetDateTime occurredAt,
        @NotNull(message= "Payload é obrigatório !")
        JsonNode payload
) {}
