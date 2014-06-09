/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;

import com.junbo.catalog.db.entity.OfferEntity;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.id.OrganizationId;

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
        entity.setPublished(model.getPublished()==null?false:model.getPublished());
        entity.setOwnerId(model.getOwnerId() == null ? null : model.getOwnerId().getValue());
        entity.setCurrentRevisionId(model.getCurrentRevisionId());
        //entity.setCategories(Utils.toJson(model.getCategories()));
        entity.setEnvironment(model.getEnvironment());
        entity.setCategories(model.getCategories());
        entity.setCreatedBy(String.valueOf(model.getOwnerId()));
        entity.setUpdatedBy(String.valueOf(model.getOwnerId()));
        entity.setRev(model.getResourceAge());
    }

    public static Offer toModel(OfferEntity entity) {
        if (entity == null) {
            return null;
        }
        Offer model = new Offer();
        model.setOfferId(entity.getOfferId());
        model.setPublished(entity.isPublished());
        model.setCurrentRevisionId(entity.getCurrentRevisionId());
        model.setOwnerId(new OrganizationId(entity.getOwnerId()));
        model.setEnvironment(entity.getEnvironment());
        //model.setCategories(Utils.fromJson(entity.getCategories(), List.class));
        model.setCategories(entity.getCategories());
        model.setCreatedTime(entity.getCreatedTime());
        model.setUpdatedTime(entity.getUpdatedTime());
        model.setResourceAge(entity.getRev());
        return model;
    }
}
