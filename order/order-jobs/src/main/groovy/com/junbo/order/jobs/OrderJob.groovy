/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.jobs

import com.junbo.configuration.topo.DataCenters
import com.junbo.configuration.topo.model.DataCenter
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.PageParam
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

import javax.annotation.Resource
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by xmchen on 14-4-2.
 */
@CompileStatic
class OrderJob implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderJob)

    OrderRepositoryFacade orderRepository

    int orderProcessNumLimit

    int pageSizePerShard

    int numOfFuturesToTrack

    List<String> statusToProcess

    ThreadPoolTaskExecutor  threadPoolTaskExecutor

    OrderProcessor orderProcessor

    @Resource(name ='orderTransactionHelper')
    TransactionHelper transactionHelper

    void execute() {
        LOGGER.info('name=OrderProcessJobStart')
        def start = System.currentTimeMillis()
        def numProcessed = 0, numSuccess = new AtomicInteger(), numFail = new AtomicInteger()
        List<Future> futures = [] as LinkedList<Future>

        while (numProcessed < orderProcessNumLimit) {
            def orders = readOrdersForProcess()
            if (orders.isEmpty()) {
                break
            }

            orders.each { Order order ->
                def future = threadPoolTaskExecutor.submit(new Callable<Void>() {
                    @Override
                    Void call() throws Exception {
                        def result = orderProcessor.process(order)
                        if (result.success) {
                            numSuccess.andIncrement
                        } else {
                            numFail.andIncrement
                        }
                        return null
                    }
                } )
                appendFuture(futures, future)
            }

            numProcessed += orders.size()
        }

        // wait all task to finish
        futures.each { Future future ->
            future.get()
        }

        LOGGER.info('name=OrderProcessJobEnd, numOfOrder={}, numSuccess={}, numFail={}, latency={}ms',
            numProcessed, numSuccess, numFail, System.currentTimeMillis() - start)
        assert numProcessed == numSuccess.get() + numFail.get()
    }

    private List<Order> readOrdersForProcess() {
        List<Order> ordersAllShard = []
        DataCenter dataCenter = DataCenters.instance().getDataCenter(DataCenters.instance().currentDataCenterId())
        for (int shardId = 0; shardId < dataCenter.numberOfShard; ++shardId) {
            def orders = transactionHelper.executeInTransaction {
                orderRepository.getOrdersByStatus(DataCenters.instance().currentDataCenterId(), shardId, statusToProcess, true, new PageParam(
                        start: 0, count: pageSizePerShard
                ))
            }
            ordersAllShard.addAll(orders)
        }
        return shuffle(ordersAllShard)
    }

    private static List<Order> shuffle(List<Order> orders) {
        // shuffle the orders so it is not order by shard
        Map<String, Order> map = new HashMap<>()
        orders.each { Order order ->
            map[order.id.toString()] = order
        }
        return new ArrayList<Order>(map.values())
    }

    private void appendFuture(List<Future> futures, Future future) {
        futures.add(future)
        if (futures.size() >= numOfFuturesToTrack) {
            def numOfFutureToRemove = numOfFuturesToTrack / 2
            Iterator<Future> iterator = futures.iterator()
            for (int i = 0; i < numOfFutureToRemove; ++i) {
                iterator.next().get()
                iterator.remove()
            }
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        assert orderProcessor != null, 'orderProcessor should not be null'
    }
}
