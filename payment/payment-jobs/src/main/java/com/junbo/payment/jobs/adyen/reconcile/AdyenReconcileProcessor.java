/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.adyen.reconcile;

import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.db.repository.SettlementDetailRepository;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.internal.SettlementDetail;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.*;

/**
 * Reconcile Processor.
 */
public class AdyenReconcileProcessor {
    protected static final String SUCCESS_EVENT_RESPONSE = "{\"result\": \"OK\"}";
    private int concurrentCount;
    private int processCount;
    private int timeLimitMinutes;

    private PlatformTransactionManager transactionManager;
    private SettlementDetailRepository settlementDetailRepo;
    private PaymentTransactionService paymentTransactionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenReconcileProcessor.class);
    private static final SettlementDetail NO_MORE_PAYMENTS = new SettlementDetail(){
        {
            setMerchantReference("-1");
        }
    };

    @Required
    public void setConcurrentCount(int concurrentCount) {
        this.concurrentCount = concurrentCount;
    }

    @Required
    public void setProcessCount(int processCount) {
        this.processCount = processCount;
    }

    @Required
    public void setTimeLimitMinutes(int timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    @Required
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Required
    public void setSettlementDetailRepo(SettlementDetailRepository settlementDetailRepo) {
        this.settlementDetailRepo = settlementDetailRepo;
    }

    public void setPaymentTransactionService(PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionService = paymentTransactionService;
    }

    public void processPayments() {
        ExecutorService producer = Executors.newSingleThreadExecutor();
        ExecutorService consumers = Executors.newFixedThreadPool(concurrentCount);

        final LinkedBlockingQueue<SettlementDetail> settlementDetails = new LinkedBlockingQueue<>();

        producer.execute(new Runnable() {
            @Override
            public void run() {
                fetchToSettlePaymentIds(settlementDetails);
                settlementDetails.add(NO_MORE_PAYMENTS);
            }
        });

        for (int i = 0; i < concurrentCount; i++) {
            consumers.execute(new PaymentProcessor(settlementDetails));
        }

        consumers.shutdown();

        try {
            if (!consumers.awaitTermination(timeLimitMinutes, TimeUnit.MINUTES)) {
                consumers.shutdownNow();
                LOGGER.error("Settlement Job ran too long. Killed it...");
            }

        } catch (InterruptedException ex) {
            consumers.shutdownNow();
            LOGGER.error("Settlement Job was interrupted. Shutting down...", ex);
        }

    }

    private void processPayment(final SettlementDetail settlementDetail) {

        LOGGER.info("Sending check payment request for payment id: " + settlementDetail.getMerchantReference());
        PaymentTransaction payment = new PaymentTransaction();
        payment.setId(CommonUtil.decode(settlementDetail.getMerchantReference()));
        PaymentEvent event = new PaymentEvent();
        event.setPaymentId(payment.getId());
        event.setType(PaymentEventType.REPORT_EVENT.toString());
        event.setStatus(PaymentStatus.SETTLED.toString());
        event.setRequest(CommonUtil.toJson(settlementDetail, null));
        event.setResponse(SUCCESS_EVENT_RESPONSE);
        String closeStatus = "Closed";
        try{
            paymentTransactionService.reportPaymentEvent(event, null, null).get();
        }catch (Exception ex){
            LOGGER.error("Error in reconcile batch:" + settlementDetail.getModificationMerchantReference() + " due to: " + ex.toString());
            closeStatus = "ClosedWithError";
        }
        updateSettlementDetail(settlementDetail, closeStatus);
    }

    private void updateSettlementDetail(final SettlementDetail settlementDetail, final String closeStatus) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setReadOnly(true);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                settlementDetailRepo.closeSettlement(settlementDetail, closeStatus);
            }
        });
    }

    private void fetchToSettlePaymentIds(final BlockingQueue<SettlementDetail> ids) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setReadOnly(true);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                List<SettlementDetail> paymentIdList = settlementDetailRepo.fetchSettlePaymentIds(processCount);
                ids.addAll(paymentIdList);
            }
        });
    }

    private class PaymentProcessor implements Runnable {

        private final LinkedBlockingQueue<SettlementDetail> settlementDetails;

        public PaymentProcessor(LinkedBlockingQueue<SettlementDetail> settlementDetails) {
            this.settlementDetails = settlementDetails;
        }

        @Override
        public void run() {
            LOGGER.info("Start thread to check payment");
            SettlementDetail settlementDetail;
            while (true) {
                try {
                    settlementDetail = settlementDetails.take();

                    if (settlementDetail.getMerchantReference().equalsIgnoreCase(
                            NO_MORE_PAYMENTS.getMerchantReference())) {
                        break;
                    } else if (settlementDetail != null) {
                        final SettlementDetail id = settlementDetail;
                        processPayment(id);
                    }

                } catch (InterruptedException ex) {
                    LOGGER.warn("Payment Checking thread is interrupted.");
                }
            }

            settlementDetails.add(NO_MORE_PAYMENTS);
            LOGGER.info("Thread to check Payment finished");
        }
    }

}
