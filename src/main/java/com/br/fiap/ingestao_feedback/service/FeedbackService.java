package com.br.fiap.ingestao_feedback.service;

import com.br.fiap.ingestao_feedback.dto.FeedbackDTO;
import com.br.fiap.ingestao_feedback.model.Feedback;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final DynamoDbEnhancedClient dynamoDbClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${DYNAMODB_TABLE_NAME:FeedbackTable}")
    private String tableName;

    @Value("${TOPIC_NAME:feedback-urgente}")
    private String topicName;

    public Feedback processarFeedback(FeedbackDTO dto) {
        log.info("Processando feedback: {}", dto);

        // 1. Converter e Salvar no DynamoDB
        DynamoDbTable<Feedback> table = dynamoDbClient.table(tableName, TableSchema.fromBean(Feedback.class));
        Feedback feedback = new Feedback(dto.descricao(), dto.nota());

        try {
            table.putItem(feedback);
            log.info("Feedback salvo com ID: {}", feedback.getId());
        } catch (Exception e) {
            log.error("Erro ao salvar no DynamoDB", e);
            throw new RuntimeException("Erro ao persistir dados");
        }

        // 2. Regra de Negócio: Verificar Criticidade
        if (dto.nota() < 5) {
            log.warn("Feedback Crítico detectado (Nota: {}). Enviando para Kafka...", dto.nota());
            enviarParaKafka(feedback);
        }

        return feedback;
    }

    private void enviarParaKafka(Feedback feedback) {
        try {
            String mensagem = objectMapper.writeValueAsString(feedback);
            kafkaTemplate.send(topicName, feedback.getId(), mensagem);
            log.info("Mensagem enviada para o tópico {}", topicName);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem para Kafka", e);
            // Não relançamos erro aqui para não falhar a requisição HTTP,
            // mas em prod idealmente usaríamos um Dead Letter Queue (DLQ).
        }
    }
}