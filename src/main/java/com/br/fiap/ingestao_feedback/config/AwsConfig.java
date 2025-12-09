package com.br.fiap.ingestao_feedback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Configuration
public class AwsConfig {

    @Bean
    public DynamoDbClient dynamoDbClient() {
        // DefaultCredentialsProvider busca automaticamente:
        // 1. Variáveis de ambiente (Lambda)
        // 2. ~/.aws/credentials (Local)
        // 3. IAM Role (Lambda/EC2)
        return DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.SA_EAST_1) // Ajuste conforme sua região
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }
}
