/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.PriceTierDao;
import com.junbo.catalog.db.entity.PriceTierEntity;
import com.junbo.catalog.db.mapper.PriceTierMapper;
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
        PriceTierEntity entity = PriceTierMapper.toDBEntity(priceTier);
        return priceTierDao.create(entity);
    }

    public PriceTier get(Long id) {
        PriceTierEntity dbEntity = priceTierDao.get(id);
        return PriceTierMapper.toModel(dbEntity);
    }

    public List<PriceTier> getPriceTiers(int start, int size) {
        List<PriceTierEntity> tierEntities = priceTierDao.getPriceTiers(start, size);
        List<PriceTier> priceTiers = new ArrayList<>();
        for (PriceTierEntity tierEntity : tierEntities) {
            priceTiers.add(PriceTierMapper.toModel(tierEntity));
        }

        return priceTiers;
    }

    public Long update(PriceTier priceTier) {
        PriceTierEntity dbEntity = priceTierDao.get(priceTier.getId());
        PriceTierMapper.fillDBEntity(priceTier, dbEntity);
        return priceTierDao.update(dbEntity);
    }

    public void delete(Long tierId) {
        PriceTierEntity dbEntity = priceTierDao.get(tierId);
        dbEntity.setDeleted(true);
        priceTierDao.update(dbEntity);
    }
}
