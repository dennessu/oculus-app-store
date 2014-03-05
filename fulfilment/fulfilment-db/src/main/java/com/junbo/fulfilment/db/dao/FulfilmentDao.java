/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.dao;

import com.junbo.fulfilment.db.entity.FulfilmentEntity;

import java.util.List;

/**
 * FulfilmentDao.
 */
public interface FulfilmentDao extends BaseDao<FulfilmentEntity> {
    List<FulfilmentEntity> findByRequestId(final Long requestId);
}
