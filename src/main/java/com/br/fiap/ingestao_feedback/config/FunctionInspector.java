package com.br.fiap.ingestao_feedback.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class FunctionInspector implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(FunctionInspector.class);

    private final ApplicationContext ctx;

    public FunctionInspector(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            String[] functions = ctx.getBeanNamesForType(Function.class);
            String[] consumers = ctx.getBeanNamesForType(Consumer.class);
            String[] suppliers = ctx.getBeanNamesForType(Supplier.class);

            log.info("Registered Function beans: {}", Arrays.toString(functions));
            log.info("Registered Consumer beans: {}", Arrays.toString(consumers));
            log.info("Registered Supplier beans: {}", Arrays.toString(suppliers));
        } catch (Exception e) {
            log.warn("Unable to inspect function beans", e);
        }
    }
}
