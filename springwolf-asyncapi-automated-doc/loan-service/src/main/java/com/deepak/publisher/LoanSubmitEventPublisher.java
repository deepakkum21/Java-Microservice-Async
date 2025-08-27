package com.deepak.publisher;

import com.deepak.events.LoanApplicationSubmitEvent;
import io.github.springwolf.bindings.kafka.annotations.KafkaAsyncOperationBinding;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
public class LoanSubmitEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${loan.processing.topic-name}")
    private String loanProcessingTopic;

    public LoanSubmitEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        System.out.println("LoanSubmitEventPublisher initialized...");
    }

    @AsyncPublisher(operation = @AsyncOperation(channelName = "loan-process-topic6", description = "This topic will publish messages to Credit-risk service "
            +
            "about user submitted loan application events"))
    @KafkaAsyncOperationBinding
    public void publishLoanSubmitKafkaEvent(LoanApplicationSubmitEvent event) {

        kafkaTemplate.send(loanProcessingTopic, event);
        log.info("Loan application event published to Kafka | topic: {}",
                loanProcessingTopic);
    }
}