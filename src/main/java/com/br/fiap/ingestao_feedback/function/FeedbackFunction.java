package com.br.fiap.ingestao_feedback.function;

import com.br.fiap.ingestao_feedback.dto.FeedbackDTO;
import com.br.fiap.ingestao_feedback.model.Feedback;
import com.br.fiap.ingestao_feedback.service.FeedbackService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.function.Function;

@Configuration
public class FeedbackFunction {

    private final FeedbackService feedbackService;

    public FeedbackFunction(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * Esta é a entrada da Lambda.
     * O Spring Cloud Function mapeia automaticamente o JSON de entrada para FeedbackDTO.
     */
    @Bean
    public Function<FeedbackDTO, Feedback> processarFeedback() {
        return (input) -> {
            return feedbackService.processarFeedback(input);
        };
    }
}