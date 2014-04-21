/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.jobs

import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.PageParam
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

import javax.annotation.Resource
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
/**
 * Created by xmchen on 14-4-2.
 */
@CompileStatic
class OrderJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderJob)

    OrderRepository orderRepository

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
        allShards.each { Integer shardKey ->
            def orders = transactionHelper.executeInTransaction {
                orderRepository.getOrdersByStatus(shardKey, statusToProcess, true, new PageParam(
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

    private List<Integer> getAllShards() {
        return [0, 1]
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
}
