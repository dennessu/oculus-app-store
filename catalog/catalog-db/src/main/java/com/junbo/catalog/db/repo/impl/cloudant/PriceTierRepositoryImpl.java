/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.PriceTierRepository;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.catalog.spec.model.pricetier.PriceTiersGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantQueryResult;

import java.util.List;

/**
 * Price tier repository.
 */
public class PriceTierRepositoryImpl extends CloudantClient<PriceTier> implements PriceTierRepository {

    public PriceTier create(PriceTier priceTier) {
        return cloudantPostSync(priceTier);
    }

    public PriceTier get(String tierId) {
        return cloudantGetSync(tierId);
    }

    @Override
    public List<PriceTier> getPriceTiers(PriceTiersGetOptions options) {
        CloudantQueryResult queryResult = queryViewSync("by_tierId", null, options.getValidSize(), options.getValidStart(), true, true);
        options.setTotal(queryResult.getTotalRows());
        return Utils.getDocs(queryResult.getRows());
    }

    public PriceTier update(PriceTier priceTier, PriceTier oldPriceTier) {
        return cloudantPutSync(priceTier, oldPriceTier);
    }

    public void delete(String tierId) {
        cloudantDeleteSync(tierId);
    }

}
