/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.jobs

import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.resource.proxy.OrderEventResourceClientProxy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by xmchen on 14-4-2.
 */
class OrderJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderJob)


    OrderEventResourceClientProxy orderEventResourceClientProxy

    OrderRepository orderRepository

    int orderProcessNumLimitPerTurn

    int pageSizePerShard

    List<String> statusToProcess

    protected void execute() {
        LOGGER.info("Starting order job")

        def numProcessed = 0
        while (numProcessed < orderProcessNumLimitPerTurn) {

            def orders = readOrders()
            orders.each {

            }
            orderProcessNumLimitPerTurn + orders.size()
        }

        LOGGER.info("Order job finished")
    }

    private List<Order> readOrders() {
        List<Order> ordersAllShard = []
        allShards.each { Long shardKey ->
            def orders = orderRepository.getOrdersByStatus(shardKey, statusToProcess, true, new PageParam(
                    start: 0, count: pageSizePerShard
            ))
            ordersAllShard.addAll(orders)
        }
        return shuffle(ordersAllShard)
    }

    private List<Order> shuffle(List<Order> orders) {
        // shuffle the orders so it is not order by shard
        Map<String, Order> map = new HashMap<>()
        orders.each { Order order ->
            map[order.id.toString()] = order
        }
        return new ArrayList<Order>(map.values())
    }

    private Long[] getAllShards() {
        return null
    }
}
