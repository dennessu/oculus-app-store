/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.PromotionDraftEntity;

import java.util.List;

/**
 * Promotion draft DAO definition.
 */
public interface PromotionDraftDao extends VersionedDao<PromotionDraftEntity> {
    List<PromotionDraftEntity> getEffectivePromotions(int start, int size, String status);
}
