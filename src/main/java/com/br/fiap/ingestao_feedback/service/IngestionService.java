package com.br.fiap.ingestao_feedback.service;

import com.br.fiap.ingestao_feedback.dto.FeedbackDTO;
import com.br.fiap.ingestao_feedback.model.Feedback;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionService {

    private final DynamoDbEnhancedClient dynamoDbClient;
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${DYNAMODB_TABLE_NAME}")
    private String tableName;

    @Value("${URGENCY_QUEUE_URL}")
    private String queueUrl;

    public Feedback processar(FeedbackDTO dto) {
        log.info("Recebendo feedback: {}", dto);

        // 1. Persistir (Síncrono)
        Feedback feedback = new Feedback(dto.descricao(), dto.nota());
        DynamoDbTable<Feedback> table = dynamoDbClient.table(tableName, TableSchema.fromBean(Feedback.class));
        table.putItem(feedback);

        // 2. Regra de Urgência (Assíncrono via SQS)
        if (dto.nota() < 5) {
            enviarParaFila(feedback);
        }

        return feedback;
    }

    private void enviarParaFila(Feedback feedback) {
        try {
            String json = objectMapper.writeValueAsString(feedback);
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(json)
                    .build());
            log.info("Enviado para fila de urgência: {}", feedback.getId());
        } catch (Exception e) {
            log.error("Erro ao enviar para SQS", e);
            // Em produção, considerar estratégia de retry ou fallback
        }
    }
}