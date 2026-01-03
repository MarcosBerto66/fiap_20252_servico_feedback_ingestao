package com.br.fiap.ingestao_feedback.function;

import com.br.fiap.ingestao_feedback.dto.FeedbackDTO;
import com.br.fiap.ingestao_feedback.model.Feedback;
import com.br.fiap.ingestao_feedback.service.IngestionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;
import java.util.function.Function;

@Configuration
public class FeedbackFunction {

    private final IngestionService ingestionService;

    public FeedbackFunction(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    // MS 1: Ingestão (Recebe FeedbackDTO -> Retorna Feedback salvo)
    @Bean("processarFeedback")
    public Function<FeedbackDTO, Message<Object>> processarFeedback() {
        System.out.println(">>> [DEBUG] O BEAN 'processarFeedback' FOI INICIALIZADO! <<<");

        return dto -> {
            try {
                Feedback feedback = ingestionService.processar(dto);
                return MessageBuilder.withPayload((Object) feedback)
                        .setHeader("statusCode", HttpStatus.CREATED.value())
                        .build();

            } catch (ConstraintViolationException ex) {
                List<String> errors = ex.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .toList();

                return MessageBuilder.withPayload((Object) errors)
                        .setHeader("statusCode", HttpStatus.BAD_REQUEST.value())
                        .build();
            }
        };
    }
}
