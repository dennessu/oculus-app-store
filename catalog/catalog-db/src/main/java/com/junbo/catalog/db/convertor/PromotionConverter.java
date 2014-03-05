/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.convertor;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.PromotionDraftEntity;
import com.junbo.catalog.db.entity.PromotionEntity;
import com.junbo.catalog.spec.model.promotion.Promotion;

/**
 * Promotion converter between model and DB entity.
 */
public class PromotionConverter {
    private PromotionConverter(){}

    public static PromotionDraftEntity toDraftEntity(Promotion model) {
        PromotionDraftEntity entity = new PromotionDraftEntity();
        entity.setName(model.getName());
        entity.setType(model.getType());
        entity.setRevision(model.getRevision());
        entity.setStatus(model.getStatus());
        entity.setStartDate(model.getStartDate());
        entity.setEndDate(model.getEndDate());
        entity.setPayload(Utils.toJsonWithType(model));
        return entity;
    }

    public static Promotion toModel(PromotionDraftEntity entity) {
        Promotion model = Utils.fromJson(entity.getPayload(), Promotion.class);
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setRevision(entity.getRevision());
        model.setStatus(entity.getStatus());
        model.setStartDate(entity.getStartDate());
        model.setEndDate(entity.getEndDate());
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedBy(entity.getUpdatedBy());
        model.setUpdatedTime(entity.getUpdatedTime());
        return model;
    }

    public static PromotionEntity toEntity(Promotion model) {
        PromotionEntity entity = new PromotionEntity();
        entity.setPromotionId(model.getId());
        entity.setName(model.getName());
        entity.setType(model.getType());
        entity.setStatus(model.getStatus());
        entity.setRevision(model.getRevision());
        entity.setStartDate(model.getStartDate());
        entity.setEndDate(model.getEndDate());
        entity.setPayload(Utils.toJsonWithType(model));
        return entity;
    }


    public static Promotion toModel(PromotionEntity entity) {
        Promotion model = Utils.fromJson(entity.getPayload(), Promotion.class);
        model.setId(entity.getPromotionId());
        model.setName(entity.getName());
        model.setStatus(entity.getStatus());
        model.setStartDate(entity.getStartDate());
        model.setEndDate(entity.getEndDate());
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedBy(entity.getUpdatedBy());
        model.setUpdatedTime(entity.getUpdatedTime());
        return model;
    }
}
