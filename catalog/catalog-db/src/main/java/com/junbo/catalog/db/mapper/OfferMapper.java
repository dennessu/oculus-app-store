/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.OfferEntity;
import com.junbo.catalog.spec.model.offer.Offer;

import java.util.List;


/**
 * Offer mapper between model and DB entity.
 */
public class OfferMapper {
    private OfferMapper(){}

    public static OfferEntity toDBEntity(Offer model) {
        if (model == null) {
            return null;
        }
        OfferEntity entity = new OfferEntity();
        fillDBEntity(model, entity);
        return entity;
    }

    public static void fillDBEntity(Offer model, OfferEntity entity) {
        entity.setOfferId(model.getOfferId());
        entity.setOfferName(model.getName());
        entity.setCurated(model.getCurated()==null?false:model.getCurated());
        entity.setOwnerId(model.getOwnerId());
        entity.setCurrentRevisionId(model.getCurrentRevisionId());
        entity.setCategories(Utils.toJson(model.getCategories()));
    }

    public static Offer toModel(OfferEntity entity) {
        if (entity == null) {
            return null;
        }
        Offer model = new Offer();
        model.setOfferId(entity.getOfferId());
        model.setName(entity.getOfferName());
        model.setCurated(entity.isCurated());
        model.setCurrentRevisionId(entity.getCurrentRevisionId());
        model.setOwnerId(entity.getOwnerId());
        model.setCategories(Utils.fromJson(entity.getCategories(), List.class));
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedBy(entity.getUpdatedBy());
        model.setUpdatedTime(entity.getUpdatedTime());
        return model;
    }
}
