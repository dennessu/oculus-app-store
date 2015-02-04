/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo;

import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrderPendingActionId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.OrderPendingAction;
import com.junbo.order.spec.model.PageParam;
import com.junbo.order.spec.model.enums.OrderPendingActionType;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by fzhang on 2/18/14.
 */
public interface OrderPendingActionRepository extends BaseRepository<OrderPendingAction, OrderPendingActionId>  {

    @ReadMethod
    Promise<List<OrderPendingAction>> getOrderPendingActionsByOrderId(OrderId orderId, OrderPendingActionType actionType);

    @ReadMethod
    Promise<List<OrderPendingAction>> listOrderPendingActionsCreateTimeAsc(int dataCenterId, int shardId, OrderPendingActionType actionType,
                                                                           Date startTime, Date endTime, PageParam pageParam);
}
