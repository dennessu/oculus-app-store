/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.cloudant

import com.junbo.common.id.OrderId
import com.junbo.common.id.OrderPendingActionId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.OrderPendingActionRepository
import com.junbo.order.spec.model.OrderPendingAction
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.enums.OrderPendingActionType
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
/**
 * Created by acer on 2015/2/2.
 */
class OrderPendingActionRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<OrderPendingAction, OrderPendingActionId> implements OrderPendingActionRepository {

    @Override
    Promise<List<OrderPendingAction>> getOrderPendingActionsByOrderId(OrderId orderId, OrderPendingActionType actionType) {
        throw new UnsupportedOperationException("not implemented in cloudant");
    }

    @Override
    Promise<List<OrderPendingAction>> listOrderPendingActionsCreateTimeAsc(int dataCenterId, int shardId, OrderPendingActionType actionType,
                                                              Date startTime, Date endTime, PageParam pageParam) {
        throw new UnsupportedOperationException("not implemented in cloudant");
    }
}
