/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.dao;

import com.junbo.fulfilment.db.entity.FulfilmentActionEntity;

import java.util.List;

/**
 * FulfilmentActionDao.
 */
public interface FulfilmentActionDao extends BaseDao<FulfilmentActionEntity> {
    List<FulfilmentActionEntity> findByFulfilmentId(final Long fulfilmentId);
}
