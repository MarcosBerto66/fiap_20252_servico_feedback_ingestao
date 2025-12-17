package com.br.fiap.ingestao_feedback.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EnvInspector implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(EnvInspector.class);

    private final Environment env;

    public EnvInspector(Environment env) {
        this.env = env;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            String envVar = System.getenv("SPRING_CLOUD_FUNCTION_DEFINITION");
            String prop = env.getProperty("spring.cloud.function.definition");
            log.info("ENV SPRING_CLOUD_FUNCTION_DEFINITION='{}' | prop spring.cloud.function.definition='{}'", envVar, prop);

            // also list relevant envs for debugging
            log.debug("Selected environment variables: {}", Map.of(
                    "SPRING_PROFILES_ACTIVE", System.getenv("SPRING_PROFILES_ACTIVE"),
                    "AWS_REGION", System.getenv("AWS_REGION"),
                    "DYNAMODB_TABLE_NAME", System.getenv("DYNAMODB_TABLE_NAME")
            ));
        } catch (Exception e) {
            log.warn("Failed to read environment for debugging", e);
        }
    }
}
