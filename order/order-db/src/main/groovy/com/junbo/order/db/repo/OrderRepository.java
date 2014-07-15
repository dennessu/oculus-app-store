/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo;

import com.junbo.common.id.OrderId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderQueryParam;
import com.junbo.order.spec.model.PageParam;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;

/**
 * Created by chriszhu on 2/18/14.
 */
public interface OrderRepository extends BaseRepository<Order, OrderId> {

    @ReadMethod
    Promise<List<Order>> getByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam);

    @ReadMethod
    Promise<List<Order>> getByStatus(Integer dataCenterId, Object shardKey, List<String> statusList,
                                     boolean updatedByAscending, PageParam pageParam);
}
