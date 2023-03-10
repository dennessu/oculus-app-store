/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.PromotionEntity;
import com.junbo.catalog.spec.model.promotion.Promotion;

/**
 * Promotion mapper between model and DB entity.
 */
public class PromotionMapper {
    private PromotionMapper(){}

    public static PromotionEntity toDBEntity(Promotion model) {
        if (model == null) {
            return null;
        }
        PromotionEntity entity = new PromotionEntity();
        fillDBEntity(model, entity);
        return entity;
    }

    public static void fillDBEntity(Promotion model, PromotionEntity entity) {
        entity.setPromotionId(model.getPromotionId());
        entity.setOwnerId(model.getOwnerId());
        entity.setType(model.getType());
        entity.setCurrentRevisionId(model.getCurrentRevisionId());
        entity.setPayload(Utils.toJsonWithType(model));
        entity.setRev(model.getResourceAge());
    }

    public static Promotion toModel(PromotionEntity entity) {
        if (entity == null) {
            return null;
        }
        Promotion model = Utils.fromJson(entity.getPayload(), Promotion.class);
        model.setPromotionId(entity.getPromotionId());
        model.setCurrentRevisionId(entity.getCurrentRevisionId());
        model.setOwnerId(entity.getOwnerId());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedTime(entity.getUpdatedTime());
        model.setResourceAge(entity.getRev());
        return model;
    }
}
