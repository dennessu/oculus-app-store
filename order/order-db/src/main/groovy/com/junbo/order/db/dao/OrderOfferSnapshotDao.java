/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao;

import com.junbo.order.db.entity.OrderOfferSnapshotEntity;

import java.util.List;

/**
 * Created by LinYi on 2014/8/28.
 */
public interface OrderOfferSnapshotDao extends BaseDao<OrderOfferSnapshotEntity> {
    List<OrderOfferSnapshotEntity> readByOrderId(final Long orderId);
}
