/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.dao;

import com.junbo.order.db.entity.OrderPendingActionEntity;
import com.junbo.order.spec.model.enums.OrderPendingActionType;

import java.util.Date;
import java.util.List;

/**
 * Created by fzhang on 2015/2/2.
 */
public interface OrderPendingActionDao extends BaseDao<OrderPendingActionEntity> {

    List<OrderPendingActionEntity> list(int dataCenterId, int shardId, OrderPendingActionType actionType, Date startTime, Date endTime, int start, int count);

    List<OrderPendingActionEntity> getByOrderId(long orderId, OrderPendingActionType actionType);

}
