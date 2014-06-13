/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.db.repo.PriceTierRepository;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantViews;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();

        CloudantViews.CloudantView view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {emit(doc._id, doc._id)}");
        view.setResultClass(String.class);
        viewMap.put("by_tierId", view);

        setViews(viewMap);
    }};

    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews;
    }
}
