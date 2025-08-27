package com.deepak.publisher;

import com.deepak.events.CreditDecisionEvent;
import io.github.springwolf.bindings.kafka.annotations.KafkaAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreditRiskDecisionPublisher {

    public static final String TOPIC = "credit-decision-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CreditRiskDecisionPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @AsyncPublisher(operation = @AsyncOperation(channelName = "credit-decision-topic", description = "This topic will publish messages to loan service "
            +
            "about user loan application status APPROVED OR FAILED ! "))
    @KafkaAsyncOperationBinding
    public void publishCreditDecision(CreditDecisionEvent creditRiskCheckResult) {
        kafkaTemplate.send(TOPIC, creditRiskCheckResult);
        log.info("Credit risk service publish events {}", creditRiskCheckResult);
    }
}