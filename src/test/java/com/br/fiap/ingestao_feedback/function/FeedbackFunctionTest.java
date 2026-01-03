package com.br.fiap.ingestao_feedback.function;

import com.br.fiap.ingestao_feedback.dto.FeedbackDTO;
import com.br.fiap.ingestao_feedback.model.Feedback;
import com.br.fiap.ingestao_feedback.service.IngestionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.Set;
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

        Function<FeedbackDTO, Message<Object>> f = function.processarFeedback();
        Message<Object> result = f.apply(dto);

        assertSame(expected, result.getPayload());
        assertEquals(result.getHeaders().get("statusCode"), HttpStatus.CREATED.value());
        verify(ingestion, times(1)).processar(dto);
    }

    @Test
    void processarFeedback_handlesConstraintViolationException() {
        IngestionService ingestion = mock(IngestionService.class);
        FeedbackFunction function = new FeedbackFunction(ingestion);

        FeedbackDTO dto = new FeedbackDTO("ok", 12);

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("A nota máxima é 10");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        when(ingestion.processar(dto)).thenThrow(exception);

        Function<FeedbackDTO, Message<Object>> f = function.processarFeedback();
        Message<Object> response = f.apply(dto);

        Object body = response.getPayload();
        assertInstanceOf(List.class, body);
        List<?> errors = (List<?>) body;
        assertEquals(1, errors.size());
        assertEquals("A nota máxima é 10", errors.get(0));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getHeaders().get("statusCode"));
    }
}
