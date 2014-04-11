/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao;

import com.junbo.order.db.entity.OrderItemPreorderInfoEntity;

import java.util.List;

/**
 * Created by linyi on 14-2-7.
 */
public interface OrderItemPreorderInfoDao extends BaseDao<OrderItemPreorderInfoEntity> {
    List<OrderItemPreorderInfoEntity> readByOrderItemId(final Long orderItemId);
}
