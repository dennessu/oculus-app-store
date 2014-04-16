/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.view;

import com.junbo.order.db.entity.SubledgerItemEntity;
import com.junbo.sharding.view.EntityView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LinYi on 14-4-15.
 */
public class SubledgerItemStatusView implements EntityView<Long, SubledgerItemEntity, String> {
    @Override
    public String getName() {
        return "subledger_item_status";
    }

    @Override
    public Class<Long> getIdType() {
        return Long.class;
    }

    @Override
    public Class<SubledgerItemEntity> getEntityType() {
        return SubledgerItemEntity.class;
    }

    @Override
    public Class<String> getKeyType() {
        return String.class;
    }

    @Override
    public boolean handlesEntity(SubledgerItemEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        return entity.getStatus() != null;
    }

    @Override
    public List<String> mapEntity(SubledgerItemEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }
        List<String> result = new ArrayList<>();
        result.add(entity.getStatus().toString());
        return result;
    }
}
