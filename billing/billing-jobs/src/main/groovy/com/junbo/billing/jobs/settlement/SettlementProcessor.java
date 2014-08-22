/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.jobs.settlement;

import com.junbo.billing.db.repo.facade.BalanceRepositoryFacade;
import com.junbo.billing.jobs.clientproxy.BillingFacade;
import com.junbo.billing.spec.model.Balance;
import com.junbo.common.id.BalanceId;
import com.junbo.configuration.topo.DataCenters;
import com.junbo.langur.core.promise.Promise;
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
 * Created by xmchen on 14-5-6.
 */
@CompileStatic
public class SettlementProcessor {
    private int concurrentCount;
    private int processCount;
    private int timeLimitMinutes;

    private PlatformTransactionManager transactionManager;

    @Autowired
    private BalanceRepositoryFacade balanceRepositoryFacade;

    @Autowired
    private BillingFacade billingFacade;

    private static final Logger LOGGER = LoggerFactory.getLogger(SettlementProcessor.class);
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
        if (DataCenters.instance().currentDataCenterId() != 0) {
            LOGGER.info("The SettlementJob only runs in first datacenter.");
            return;
        }

        ExecutorService producer = Executors.newSingleThreadExecutor();
        ExecutorService consumers = Executors.newFixedThreadPool(concurrentCount);

        final LinkedBlockingQueue<BalanceId> balanceIds = new LinkedBlockingQueue<>();

        producer.execute(new Runnable() {
            @Override
            public void run() {
                fetchToSettleBalanceIds(balanceIds);
                balanceIds.add(NO_MORE_BALANCES);
            }
        });

        for (int i = 0; i < concurrentCount; i++) {
            consumers.execute(new BalanceProcessor(balanceIds));
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

    private void fetchToSettleBalanceIds(final BlockingQueue<BalanceId> ids) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setReadOnly(true);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                List<BalanceId> balanceIdList = balanceRepositoryFacade.fetchToSettleBalanceIds(processCount);
                ids.addAll(balanceIdList);
            }
        });
    }

    private void processBalance(BalanceId balanceId) {
        LOGGER.info("Sending check balance request for balance id: " + balanceId);
        Balance balance = new Balance();
        balance.setId(balanceId);

        billingFacade.checkBalance(balance).recover(new Promise.Func<Throwable, Promise<Balance>>() {
            @Override
            public Promise<Balance> apply(Throwable throwable) {
                LOGGER.error("Error in checking balance", throwable);
                return Promise.pure(null);
            }
        }).then(new Promise.Func<Balance, Promise<Balance>>() {
            @Override
            public Promise<Balance> apply(Balance balance) {
                if(balance == null) {
                    return Promise.pure(null);
                }
                LOGGER.info("The checked balance status is " + balance.getStatus() + " for balance id: " +
                        balance.getId().getValue());
                return Promise.pure(balance);
            }
        });
    }

    private class BalanceProcessor implements Runnable {

        private final LinkedBlockingQueue<BalanceId> balanceIds;

        public BalanceProcessor(LinkedBlockingQueue<BalanceId> ids) {
            this.balanceIds = ids;
        }

        @Override
        public void run() {
            LOGGER.info("Start thread to check balance");
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
                    LOGGER.warn("Balance Checking thread is interrupted.");
                }
            }

            balanceIds.add(NO_MORE_BALANCES);
            LOGGER.info("Thread to check balance finished");
        }
    }

}
