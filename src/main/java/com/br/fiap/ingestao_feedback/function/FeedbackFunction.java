package com.br.fiap.ingestao_feedback.function;

import com.br.fiap.ingestao_feedback.dto.FeedbackDTO;
import com.br.fiap.ingestao_feedback.model.Feedback;
import com.br.fiap.ingestao_feedback.service.IngestionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class FeedbackFunction {

    // MS 1: Ingestão (Recebe FeedbackDTO -> Retorna Feedback salvo)
    @Bean("processarFeedback")
    public Function<FeedbackDTO, Feedback> processarFeedback(IngestionService service) {
        System.out.println(">>> [DEBUG] O BEAN 'processarFeedback' FOI INICIALIZADO! <<<");
        return service::processar;
    }
}