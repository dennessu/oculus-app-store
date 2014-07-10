/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao;

import com.junbo.order.db.entity.SubledgerItemEntity;
import com.junbo.order.spec.model.enums.SubledgerItemStatus;

import java.util.List;

/**
 * Created by linyi on 14-2-7.
 */
public interface SubledgerItemDao extends BaseDao<SubledgerItemEntity> {
    List<SubledgerItemEntity> getByStatus(Integer dataCenterId, Integer shardId, SubledgerItemStatus status, int start, int count);

    List<SubledgerItemEntity> getByOrderItemId(Long orderItemId);
}
