/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.PriceTierService;
import com.junbo.catalog.db.repo.PriceTierRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.catalog.spec.model.pricetier.PriceTiersGetOptions;
import com.junbo.common.id.PriceTierId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Price tier service implementation.
 */
public class PriceTierServiceImpl implements PriceTierService {
    @Autowired
    private PriceTierRepository priceTierRepo;

    @Override
    public PriceTier getPriceTier(Long tierId) {
        return priceTierRepo.get(tierId);
    }

    @Override
    public List<PriceTier> getPriceTiers(PriceTiersGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getPriceTierIds())) {
            List<PriceTier> priceTiers = new ArrayList<>();

            for (PriceTierId tierId : options.getPriceTierIds()) {
                PriceTier priceTier = priceTierRepo.get(tierId.getValue());

                if (priceTier != null) {
                    priceTiers.add(priceTier);
                }
            }
            return priceTiers;
        } else {
            options.ensurePagingValid();
            return priceTierRepo.getPriceTiers(options.getStart(), options.getSize());
        }
    }

    @Override
    public PriceTier create(PriceTier priceTier) {
        if (!StringUtils.isEmpty(priceTier.getRev())) {
            throw AppErrors.INSTANCE.validation("rev must be null at creation.").exception();
        }
        Long attributeId = priceTierRepo.create(priceTier);
        return priceTierRepo.get(attributeId);
    }

    @Override
    public PriceTier update(Long tierId, PriceTier priceTier) {
        priceTierRepo.update(priceTier);
        return priceTierRepo.get(tierId);
    }

    @Override
    public void delete(Long tierId) {
        priceTierRepo.delete(tierId);
    }
}
