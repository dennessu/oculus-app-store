/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.facade;

import com.junbo.common.id.OrderId;
import com.junbo.order.spec.model.*;
import com.junbo.order.spec.model.enums.OrderItemRevisionType;
import com.junbo.order.spec.model.enums.OrderPendingActionType;

import java.util.Date;
import java.util.List;

/**
 * Created by chriszhu on 2/18/14.
 */
public interface OrderRepositoryFacade {

    Order createOrder(Order order);

    Order getOrder(Long orderId);

    List<Order> getOrdersByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam);

    List<Order> getOrdersByStatus(Integer dataCenterId, Object shardKey, List<String> statusList,
                                  boolean updatedByAscending, PageParam pageParam);

    List<Order> getOrdersByTaxStatus(Integer dataCenterId, Object shardKey, List<String> statusList, boolean isAudited,
                                      boolean updatedByAscending, PageParam pageParam);

    OrderEvent createOrderEvent(OrderEvent event);

    FulfillmentHistory createFulfillmentHistory(FulfillmentHistory history);

    BillingHistory createBillingHistory(Long orderId, BillingHistory history);

    BillingHistory updateBillingHistory(BillingHistory history);

    List<OrderItem> getOrderItems(Long orderId);

    OrderItem getOrderItem(Long orderItemId);

    List<Discount> getDiscounts(Long orderId);

    Order updateOrder(Order order, Boolean updateOnlyOrder,
                      Boolean saveRevision, OrderItemRevisionType revisionType) ;

    List<OrderEvent> getOrderEvents(Long orderId, PageParam pageParam);

    List<PreorderInfo> getPreorderInfo(Long orderItemId);

    List<BillingHistory> getBillingHistories(Long orderId);

    List<FulfillmentHistory> getFulfillmentHistories(Long orderItemId);

    OfferSnapshot createOfferSnapshot(OfferSnapshot offerSnapshot);

    List<OfferSnapshot> getSnapshot(Long orderId);

    OrderPendingAction createOrderPendingAction(OrderPendingAction action);

    OrderPendingAction updateOrderPendingAction(OrderPendingAction action);

    List<OrderPendingAction> getOrderPendingActionsByOrderId(OrderId orderId, OrderPendingActionType actionType);

    List<OrderPendingAction> listOrderPendingActionsCreateTimeAsc(Integer dataCenterId, Integer shardKey,
                                                                  OrderPendingActionType actionType, Date startTime,
                                                                  Date endTime, PageParam pageParam);

}
