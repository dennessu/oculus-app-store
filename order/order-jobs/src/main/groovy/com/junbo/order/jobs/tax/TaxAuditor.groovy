/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.jobs.tax

import com.junbo.configuration.topo.DataCenters
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.jobs.OrderProcessor
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.PageParam
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.util.concurrent.atomic.AtomicInteger

/**
 * Backend job to audit tax.
 */
@CompileStatic
@Component('orderTaxAuditor')
class TaxAuditor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxAuditor)

    OrderRepositoryFacade orderRepository

    int orderProcessNumLimit

    int pageSizePerShard

    int numOfFuturesToTrack

    List<String> statusToProcess

    ThreadPoolTaskExecutor threadPoolTaskExecutor

    @Resource(name = 'orderAuditTaxProcessor')
    OrderProcessor orderProcessor

    @Resource(name ='orderTransactionHelper')
    TransactionHelper transactionHelper

    void processOrder(String orderIdListStr) {
        // export to JMX for debugging
        def orderIdList = orderIdListStr.split(',').collect { String idStr ->
            Long.valueOf(idStr.trim())
        }
        orderIdList.each { Long orderId ->
            transactionHelper.executeInTransaction {
                Order order = orderRepository.getOrder(orderId)
                orderProcessor.process(order)
            }
        }
    }

    void execute() {
        LOGGER.info('name=OrderAuditTaxJobStart')
        def start = System.currentTimeMillis()
        def numProcessed = 0, numSuccess = new AtomicInteger(), numFail = new AtomicInteger()

        while (numProcessed < orderProcessNumLimit) {
            def orders = readOrdersForProcess()
            if (orders.isEmpty()) {
                break
            }

            orders.each { Order order ->
                transactionHelper.executeInTransaction {
                    def result = orderProcessor.process(order)
                    if (result.success) {
                        numSuccess.andIncrement
                    } else {
                        numFail.andIncrement
                    }
                }
            }

            numProcessed += orders.size()
        }
        LOGGER.info('name=OrderAuditTaxJobEnd, numOfOrder={}, numSuccess={}, numFail={}, latency={}ms',
                numProcessed, numSuccess, numFail, System.currentTimeMillis() - start)
        assert numProcessed == numSuccess.get() + numFail.get()
    }

    private List<Order> readOrdersForProcess() {
        List<Order> ordersAllShard = []
        allShards.each { Integer shardKey ->
            def orders = transactionHelper.executeInTransaction {
                orderRepository.getOrdersByTaxStatus(DataCenters.instance().currentDataCenterId(), shardKey,
                        statusToProcess, false, true, new PageParam(
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
}