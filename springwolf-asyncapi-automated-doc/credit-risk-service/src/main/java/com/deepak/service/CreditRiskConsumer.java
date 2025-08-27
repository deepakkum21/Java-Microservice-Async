package com.deepak.service;

import com.deepak.events.CreditDecisionEvent;
import com.deepak.events.LoanApplicationSubmitEvent;
import com.deepak.exception.InSufficientCreditScoreException;
import com.deepak.publisher.CreditRiskDecisionPublisher;
import com.deepak.util.CreditScoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreditRiskConsumer {

    private final CreditRiskDecisionPublisher creditRiskDecisionPublisher;

    public CreditRiskConsumer(CreditRiskDecisionPublisher creditRiskDecisionPublisher) {
        this.creditRiskDecisionPublisher = creditRiskDecisionPublisher;
    }

    @KafkaListener(topics = "loan-process-topic6")
    public void onLoanApplicationReceived(LoanApplicationSubmitEvent event) {
        CreditDecisionEvent creditRiskCheckResult = null;
        log.info("Received loan application event: {}", event);

        try {
            evaluateCreditRisk(event.getUserId());
            log.info("Credit risk check PASSED for userId: {}", event.getUserId());

            creditRiskCheckResult = buildCreditDecisionEvent(event, true, "Credit risk check PASSED");

            creditRiskDecisionPublisher.publishCreditDecision(creditRiskCheckResult);

        } catch (InSufficientCreditScoreException ex) {
            log.warn("Credit risk check FAILED for userId: {} | Reason: {}", event.getUserId(), ex.getMessage());

            creditRiskCheckResult = buildCreditDecisionEvent(event, false, ex.getMessage());
            creditRiskDecisionPublisher.publishCreditDecision(creditRiskCheckResult);

            log.warn("Credit risk service publish events {}", creditRiskCheckResult);
        }
    }

    private CreditDecisionEvent buildCreditDecisionEvent(LoanApplicationSubmitEvent event, boolean approved,
            String Credit_risk_check_PASSED) {
        // needs update
        return CreditDecisionEvent.builder()
                .loanId(event.getLoanId())
                .userId(event.getUserId())
                .approved(approved)
                .message(Credit_risk_check_PASSED)
                .build();
    }

    private void evaluateCreditRisk(int userId) {
        int creditScore = CreditScoreUtils
                .creditScoreResults()
                .getOrDefault(userId, 0);
        if (creditScore < 750) {
            throw new InSufficientCreditScoreException("Credit score is too low: " + creditScore);
        }
    }

}
