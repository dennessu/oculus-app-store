/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.db.repo.PriceTierRepository;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.common.cloudant.CloudantClient;

import java.util.List;

/**
 * Price tier repository.
 */
public class PriceTierRepositoryImpl extends CloudantClient<PriceTier> implements PriceTierRepository {

    public PriceTier create(PriceTier priceTier) {
        return super.cloudantPost(priceTier).get();
    }

    public PriceTier get(String tierId) {
        return super.cloudantGet(tierId).get();
    }

    public List<PriceTier> getPriceTiers(int start, int size) {
        return super.queryView("by_tierId", null, size, start, true).get();
    }

    public PriceTier update(PriceTier priceTier) {
        return super.cloudantPut(priceTier).get();
    }

    public void delete(String tierId) {
        super.cloudantDelete(tierId).get();
    }

}
