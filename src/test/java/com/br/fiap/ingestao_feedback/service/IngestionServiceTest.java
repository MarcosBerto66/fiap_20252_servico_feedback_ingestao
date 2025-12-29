package com.br.fiap.ingestao_feedback.service;

import com.br.fiap.ingestao_feedback.dto.FeedbackDTO;
import com.br.fiap.ingestao_feedback.model.Feedback;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestionServiceTest {

    @Mock
    DynamoDbEnhancedClient dynamo;

    @Mock
    DynamoDbTable<Feedback> table;

    @Mock
    SqsClient sqsClient;

    @Mock
    ObjectMapper objectMapper;

    IngestionService service;

    @BeforeEach
    void setUp() {
        service = new IngestionService(dynamo, sqsClient, objectMapper);
        ReflectionTestUtils.setField(service, "tableName", "table");
        ReflectionTestUtils.setField(service, "queueUrl", "queue");
        when(dynamo.table(eq("table"), any(TableSchema.class))).thenReturn((DynamoDbTable) table);
    }

    @Test
    void processar_persistsAndDoesNotSendToQueue_whenNotaIsHigh() {
        FeedbackDTO dto = new FeedbackDTO("Tudo certo", 8);

        Feedback result = service.processar(dto);

        assertNotNull(result.getId());
        assertEquals("Tudo certo", result.getDescricao());
        assertEquals(8, result.getNota());

        verify(table, times(1)).putItem(any(Feedback.class));
        verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void processar_persistsAndSendsToQueue_whenNotaIsLow() throws Exception {
        FeedbackDTO dto = new FeedbackDTO("Problema grave", 3);

        when(objectMapper.writeValueAsString(any(Feedback.class))).thenReturn("{\"dummy\":true}");

        Feedback result = service.processar(dto);

        assertNotNull(result.getId());
        assertEquals("Problema grave", result.getDescricao());
        assertEquals(3, result.getNota());

        verify(table, times(1)).putItem(any(Feedback.class));

        ArgumentCaptor<SendMessageRequest> captor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(sqsClient, times(1)).sendMessage(captor.capture());
        SendMessageRequest sent = captor.getValue();
        assertEquals("queue", sent.queueUrl());
        assertTrue(sent.messageBody().contains("dummy"));
    }
}
