/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao;

import com.junbo.order.db.entity.OrderEntity;
import com.junbo.order.spec.model.enums.OrderStatus;

import java.util.List;

/**
 * Created by linyi on 14-2-7.
 */
public interface OrderDao extends BaseDao<OrderEntity> {
    List<OrderEntity> readByUserId(final Long userId,  Boolean tentative, Integer start, Integer count);

    List<OrderEntity> readByStatus(Integer dataCenterId, Integer shardId,  List<OrderStatus> statusList, boolean updatedByAscending,
                                   Integer start, Integer count);
}
