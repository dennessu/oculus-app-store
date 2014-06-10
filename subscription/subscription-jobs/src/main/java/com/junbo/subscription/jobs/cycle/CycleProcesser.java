/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.jobs.cycle;

import com.junbo.common.id.SubscriptionId;
import com.junbo.subscription.db.repository.SubscriptionRepository;
//import com.junbo.subscription.spec.model.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

//import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Administrator on 14-5-28.
 */
public class CycleProcesser {
    private int concurrentCount;
    private int processCount;
    private PlatformTransactionManager ptManager;

    private static final SubscriptionId SUBSCRIPTION_END_ID = new SubscriptionId(-1L);
    private static final Logger LOGGER = LoggerFactory.getLogger(CycleProcesser.class);

    @Autowired
    SubscriptionRepository subscriptionRepository;


    @Required
    public void setConcurrentCount(int concurrentCount) {
        this.concurrentCount = concurrentCount;
    }

    @Required
    public void setProcessCount(int processCount) {
        this.processCount = processCount;
    }

    @Required
    public void setTransactionManager(PlatformTransactionManager ptManager) {
        this.ptManager = ptManager;
    }

    public void CycleCharge(){
        ExecutorService producer = Executors.newSingleThreadExecutor();
        ExecutorService consumers = Executors.newFixedThreadPool(concurrentCount);


        final LinkedBlockingQueue<SubscriptionId> subscriptionIds = new LinkedBlockingQueue<>();

        producer.execute(new Runnable() {
            @Override
            public void run() {
                fetchCycleSubscriptions(subscriptionIds);
                subscriptionIds.add(SUBSCRIPTION_END_ID);
            }
        });

        for (int i = 0; i < concurrentCount; i++) {
            consumers.execute(new CycleProcessor(subscriptionIds));
        }

        consumers.shutdown();

    }

    private void fetchCycleSubscriptions(final BlockingQueue<SubscriptionId> ids) {
        TransactionTemplate template = new TransactionTemplate(ptManager);
        template.setReadOnly(true);
        template.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                //List<SubscriptionId> subscriptionIds = subscriptionRepository.fetchToSettleBalanceIds(processCount);
                //ids.addAll(subscriptionIds);
            }
        });
    }
    public void CycleCharge(SubscriptionId id){

    }

    private class CycleProcessor implements Runnable {

        private final LinkedBlockingQueue<SubscriptionId> subscriptionIds;

        public CycleProcessor(LinkedBlockingQueue<SubscriptionId> ids) {
            this.subscriptionIds = ids;
        }

        @Override
        public void run() {
            LOGGER.info("Start thread to get cycle charge.");
            SubscriptionId subscriptionId;
            while (true) {
                try {
                    subscriptionId = subscriptionIds.take();

                    if (subscriptionId.equals(SUBSCRIPTION_END_ID)) {
                        break;
                    } else if (subscriptionId != null) {
                        final SubscriptionId id = subscriptionId;
                        CycleCharge(id);
                    }

                } catch (InterruptedException ex) {
                    LOGGER.warn("Balance Checking thread is interrupted.");
                }
            }

            subscriptionIds.add(SUBSCRIPTION_END_ID);
            LOGGER.info("Thread to get cycle charge finished");
        }
    }

}
