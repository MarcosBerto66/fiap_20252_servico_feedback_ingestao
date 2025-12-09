package com.br.fiap.ingestao_feedback.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public record FeedbackDTO(
        @NotBlank(message = "A descrição não pode estar vazia")
        String descricao,

        @NotNull(message = "A nota é obrigatória")
        @Min(value = 0, message = "A nota mínima é 0")
        @Max(value = 10, message = "A nota máxima é 10")
        Integer nota
) implements Serializable {}