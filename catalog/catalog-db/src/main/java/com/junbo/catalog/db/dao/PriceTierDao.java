/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.PriceTierEntity;

import java.util.List;

/**
 * Price tier DAO definition.
 */
public interface PriceTierDao extends BaseDao<PriceTierEntity> {
    List<PriceTierEntity> getPriceTiers(int start, int size);
}
