/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao;

import com.junbo.order.db.entity.OrderEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by linyi on 14-2-7.
 */
public interface OrderDao extends BaseDao<OrderEntity> {
    List<OrderEntity> readByUserId(final Long userId);
    List<OrderEntity> readByTrackingUuid(final UUID trackingUuid);
}
