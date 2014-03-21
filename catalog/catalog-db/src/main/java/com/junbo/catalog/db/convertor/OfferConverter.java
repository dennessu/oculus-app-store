/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.convertor;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.OfferDraftEntity;
import com.junbo.catalog.db.entity.OfferEntity;
import com.junbo.catalog.spec.model.offer.Offer;

/**
 * Offer converter between model and DB entity.
 */
public class OfferConverter {
    private OfferConverter(){}

    public static OfferDraftEntity toDraftEntity(Offer model) {
        if (model == null) {
            return null;
        }
        OfferDraftEntity entity = new OfferDraftEntity();
        fillDraftEntity(model, entity);
        return entity;
    }

    public static void fillDraftEntity(Offer model, OfferDraftEntity entity) {
        entity.setName(model.getName());
        entity.setStatus(model.getStatus());
        entity.setOwnerId(model.getOwnerId());
        entity.setPayload(Utils.toJson(model));
    }

    public static Offer toModel(OfferDraftEntity entity) {
        if (entity == null) {
            return null;
        }
        Offer model = Utils.fromJson(entity.getPayload(), Offer.class);
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setStatus(entity.getStatus());
        model.setOwnerId(entity.getOwnerId());
        model.setTimestamp(entity.getTimestamp());
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedBy(entity.getUpdatedBy());
        model.setUpdatedTime(entity.getUpdatedTime());
        return model;
    }

    public static OfferEntity toEntity(Offer model) {
        if (model == null) {
            return null;
        }
        OfferEntity entity = new OfferEntity();
        entity.setOfferId(model.getId());
        entity.setName(model.getName());
        entity.setStatus(model.getStatus());
        entity.setOwnerId(model.getOwnerId());
        entity.setPayload(Utils.toJson(model));
        return entity;
    }


    public static Offer toModel(OfferEntity entity) {
        if (entity == null) {
            return null;
        }
        Offer model = Utils.fromJson(entity.getPayload(), Offer.class);
        model.setId(entity.getOfferId());
        model.setName(entity.getName());
        model.setStatus(entity.getStatus());
        model.setOwnerId(entity.getOwnerId());
        model.setTimestamp(entity.getTimestamp());
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedBy(entity.getUpdatedBy());
        model.setUpdatedTime(entity.getUpdatedTime());
        return model;
    }
}
