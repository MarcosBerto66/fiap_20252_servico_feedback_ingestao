package com.br.fiap.ingestao_feedback.function;

import com.br.fiap.ingestao_feedback.dto.FeedbackDTO;
import com.br.fiap.ingestao_feedback.model.Feedback;
import com.br.fiap.ingestao_feedback.service.IngestionService;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedbackFunctionTest {

    @Test
    void processarFeedback_delegatesToIngestionService() {
        IngestionService ingestion = mock(IngestionService.class);
        FeedbackFunction function = new FeedbackFunction(ingestion);

        FeedbackDTO dto = new FeedbackDTO("ok", 6);
        Feedback expected = new Feedback("ok", 6);
        when(ingestion.processar(dto)).thenReturn(expected);

        Function<FeedbackDTO, Feedback> f = function.processarFeedback();
        Feedback result = f.apply(dto);

        assertSame(expected, result);
        verify(ingestion, times(1)).processar(dto);
    }
}
