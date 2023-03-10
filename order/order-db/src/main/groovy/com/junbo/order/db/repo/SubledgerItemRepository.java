/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo;

import com.junbo.common.id.ItemId;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.id.SubledgerItemId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.order.spec.model.PageParam;
import com.junbo.order.spec.model.SubledgerItem;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by fzhang on 4/2/2014.
 */
public interface SubledgerItemRepository extends BaseRepository<SubledgerItem, SubledgerItemId> {

    @ReadMethod
    Promise<List<SubledgerItem>> getSubledgerItems(Integer dataCenterId, Object shardKey, String status, ItemId itemId, Date endTime, PageParam pageParam);

    @ReadMethod
    Promise<List<SubledgerItem>> getSubledgerItemsByTime(int dataCenterId, int shardId, Date startTime, Date endTime, PageParam pageParam);

    @ReadMethod
    Promise<List<SubledgerItem>> getByOrderItemId(OrderItemId orderItemId);

    @ReadMethod
    Promise<List<ItemId>> getDistinctItemIds(Integer dataCenterId, Object shardKey, String status, PageParam pageParam);
}
