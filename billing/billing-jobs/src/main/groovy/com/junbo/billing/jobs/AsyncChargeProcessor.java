/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.jobs;

import com.junbo.billing.db.repository.BalanceRepository;
import com.junbo.common.id.BalanceId;
import groovy.transform.CompileStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by xmchen on 14-4-8.
 */
@CompileStatic
public class AsyncChargeProcessor {
    private int concurrentCount;
    private int processCount;
    private int timeLimitMinutes;

    private PlatformTransactionManager transactionManager;

    @Autowired
    private BalanceRepository balanceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncChargeProcessor.class);
    private static final BalanceId NO_MORE_BALANCES = new BalanceId(-1L);

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

    public void processBalances() {
        ExecutorService producer = Executors.newSingleThreadExecutor();
        ExecutorService consumers = Executors.newFixedThreadPool(concurrentCount);

        final LinkedBlockingQueue<BalanceId> balanceIds = new LinkedBlockingQueue<>();

        producer.execute(new Runnable() {
            @Override
            public void run() {
                fetchAsyncBalanceIds(balanceIds);
                balanceIds.add(NO_MORE_BALANCES);
            }
        });

        for (int i = 0; i < concurrentCount; i++) {
            consumers.execute(new BalanceProcessor(balanceIds));
        }

        consumers.isShutdown();

        try {
            if (!consumers.awaitTermination(timeLimitMinutes, TimeUnit.MINUTES)) {
                consumers.shutdownNow();
                LOGGER.error("Async Charge Job ran too long. Killed it...");
            }

        } catch (InterruptedException ex) {
            consumers.shutdownNow();
            LOGGER.error("Async Charge Job was interrupted. Shutting down...", ex);
        }

    }

    private void fetchAsyncBalanceIds(final BlockingQueue<BalanceId> ids) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setReadOnly(true);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                List<BalanceId> balanceIdList = balanceRepository.fetchAsyncChargeBalanceIds(processCount);
                ids.addAll(balanceIdList);
            }

        });
        LOGGER.info("Get async charge balances size: " + ids.size());
    }

    private void processBalance(BalanceId balanceId) {
        LOGGER.info("Sending async charge process request for balance id: " + balanceId);
    }

    private class BalanceProcessor implements Runnable {

        private final LinkedBlockingQueue<BalanceId> balanceIds;

        public BalanceProcessor(LinkedBlockingQueue<BalanceId> ids) {
            this.balanceIds = ids;
        }

        @Override
        public void run() {
            BalanceId balanceId;
            while (true) {
                try {
                    balanceId = balanceIds.take();

                    if (balanceId.equals(NO_MORE_BALANCES)) {
                        break;
                    } else if (balanceId != null) {
                        final BalanceId id = balanceId;
                        processBalance(id);
                    }

                } catch (InterruptedException ex) {
                    LOGGER.warn("Balance Processing thread is interrupted.");
                }

            }

            balanceIds.add(NO_MORE_BALANCES);
        }
    }

}
