/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.PromotionRevisionEntity;
import com.junbo.catalog.spec.model.promotion.PromotionRevisionsGetOptions;

import java.util.List;

/**
 * Promotion revision DAO definition.
 */
public interface PromotionRevisionDao extends BaseDao<PromotionRevisionEntity> {
    List<PromotionRevisionEntity> getEffectiveRevisions(PromotionRevisionsGetOptions options);
}
