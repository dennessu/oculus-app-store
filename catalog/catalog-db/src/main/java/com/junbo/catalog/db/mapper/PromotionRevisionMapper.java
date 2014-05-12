/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.PromotionRevisionEntity;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;

/**
 * Promotion revision mapper between model and DB entity.
 */
public class PromotionRevisionMapper {
    private PromotionRevisionMapper(){}

    public static PromotionRevisionEntity toDBEntity(PromotionRevision model) {
        if (model == null) {
            return null;
        }
        PromotionRevisionEntity entity = new PromotionRevisionEntity();
        fillDBEntity(model, entity);
        return entity;
    }

    public static void fillDBEntity(PromotionRevision model, PromotionRevisionEntity entity) {
        entity.setRevisionId(model.getRevisionId());
        entity.setPromotionId(model.getPromotionId());
        entity.setType(model.getType());
        entity.setStatus(model.getStatus());
        entity.setOwnerId(model.getOwnerId());
        entity.setStartDate(model.getStartDate());
        entity.setEndDate(model.getEndDate());
        entity.setPayload(Utils.toJsonWithType(model));
        entity.setRev(model.getRev()==null ? null : Integer.valueOf(model.getRev()));
    }

    public static PromotionRevision toModel(PromotionRevisionEntity entity) {
        if (entity == null) {
            return null;
        }
        PromotionRevision model = Utils.fromJson(entity.getPayload(), PromotionRevision.class);
        model.setRevisionId(entity.getRevisionId());
        model.setPromotionId(entity.getPromotionId());
        model.setStatus(entity.getStatus());
        model.setStartDate(entity.getStartDate());
        model.setEndDate(entity.getEndDate());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedTime(entity.getUpdatedTime());
        model.setRev(entity.getRev().toString());
        return model;
    }
}
