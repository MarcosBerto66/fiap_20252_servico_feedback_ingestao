package com.br.fiap.ingestao_feedback;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@SpringBootTest
class IngestaoFeedbackApplicationTests {

    // Mockamos os clientes AWS para que o teste não tente conectar na nuvem real
    // e não falhe por falta de credenciais no ambiente de teste.

    @MockBean
    private DynamoDbClient dynamoDbClient;

    @MockBean
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @MockBean
    private SqsClient sqsClient;

    @Test
    void contextLoads() {
        // Se o contexto subir sem erros (graças aos mocks), o teste passa.
    }

}