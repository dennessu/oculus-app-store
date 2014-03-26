/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.dao.PriceTierDao;
import com.junbo.catalog.db.entity.PriceTierEntity;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Price tier repository.
 */
public class PriceTierRepository {
    @Autowired
    private PriceTierDao priceTierDao;

    public Long create(PriceTier priceTier) {
        PriceTierEntity entity = new PriceTierEntity();
        entity.setName(priceTier.getName());
        entity.setPayload(Utils.toJson(priceTier));
        return priceTierDao.create(entity);
    }

    public PriceTier get(Long id) {
        PriceTierEntity entity = priceTierDao.get(id);
        PriceTier priceTier = Utils.fromJson(entity.getPayload(), PriceTier.class);
        priceTier.setId(entity.getId());
        priceTier.setName(entity.getName());
        return priceTier;
    }

    public List<PriceTier> getPriceTiers(int start, int size) {
        List<PriceTierEntity> tierEntities = priceTierDao.getPriceTiers(start, size);
        List<PriceTier> priceTiers = new ArrayList<>();
        for (PriceTierEntity tierEntity : tierEntities) {
            PriceTier priceTier = Utils.fromJson(tierEntity.getPayload(), PriceTier.class);
            priceTier.setId(tierEntity.getId());
            priceTier.setName(tierEntity.getName());

            priceTiers.add(priceTier);
        }

        return priceTiers;
    }
}
