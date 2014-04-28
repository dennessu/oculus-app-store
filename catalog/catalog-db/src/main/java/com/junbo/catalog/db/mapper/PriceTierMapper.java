/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.entity.PriceTierEntity;
import com.junbo.catalog.spec.model.pricetier.PriceTier;

/**
 * Price tier mapper between model and DB entity.
 */
public class PriceTierMapper {
    private PriceTierMapper(){}

    public static PriceTierEntity toDBEntity(PriceTier priceTier) {
        if (priceTier == null) {
            return null;
        }
        PriceTierEntity dbEntity = new PriceTierEntity();
        fillDBEntity(priceTier, dbEntity);
        return dbEntity;
    }

    public static void fillDBEntity(PriceTier priceTier, PriceTierEntity dbEntity) {
        dbEntity.setId(priceTier.getId());
        dbEntity.setPayload(Utils.toJson(priceTier));
        dbEntity.setRev(priceTier.getRev()==null ? null : Integer.valueOf(priceTier.getRev()));
    }

    public static PriceTier toModel(PriceTierEntity dbEntity) {
        if (dbEntity == null) {
            return null;
        }
        PriceTier priceTier = Utils.fromJson(dbEntity.getPayload(), PriceTier.class);
        priceTier.setId(dbEntity.getId());
        priceTier.setCreatedBy(dbEntity.getCreatedBy());
        priceTier.setCreatedTime(dbEntity.getCreatedTime());
        priceTier.setUpdatedBy(dbEntity.getUpdatedBy());
        priceTier.setUpdatedTime(dbEntity.getUpdatedTime());
        priceTier.setRev(dbEntity.getRev().toString());
        return priceTier;
    }
}
