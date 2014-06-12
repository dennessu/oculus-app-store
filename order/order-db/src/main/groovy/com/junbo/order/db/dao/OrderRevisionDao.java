/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao;

import com.junbo.order.db.entity.OrderRevisionEntity;

import java.util.List;

/**
 * Created by chriszhu on 6/11/14.
 */
public interface OrderRevisionDao extends BaseDao<OrderRevisionEntity> {

    List<OrderRevisionEntity> readByOrderId(final Long orderId);
}
