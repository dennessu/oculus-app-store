/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.OfferRevisionEntity;
import com.junbo.catalog.spec.model.offer.OfferRevision;

/**
 * Offer revision mapper between model and DB entity.
 */
public class OfferRevisionMapper {
    private OfferRevisionMapper(){}

    public static OfferRevisionEntity toDBEntity(OfferRevision model) {
        if (model == null) {
            return null;
        }
        OfferRevisionEntity entity = new OfferRevisionEntity();
        fillDBEntity(model, entity);
        return entity;
    }

    public static void fillDBEntity(OfferRevision model, OfferRevisionEntity entity) {
        entity.setRevisionId(model.getRevisionId());
        entity.setOfferId(model.getOfferId());
        entity.setStatus(model.getStatus());
        entity.setOwnerId(model.getOwnerId());
        entity.setPayload(Utils.toJson(model));
    }

    public static OfferRevision toModel(OfferRevisionEntity entity) {
        if (entity == null) {
            return null;
        }
        OfferRevision model = Utils.fromJson(entity.getPayload(), OfferRevision.class);
        model.setOfferId(entity.getOfferId());
        model.setStatus(entity.getStatus());
        model.setOwnerId(entity.getOwnerId());
        model.setRevisionId(entity.getRevisionId());

        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedBy(entity.getUpdatedBy());
        model.setUpdatedTime(entity.getUpdatedTime());
        return model;
    }
}
