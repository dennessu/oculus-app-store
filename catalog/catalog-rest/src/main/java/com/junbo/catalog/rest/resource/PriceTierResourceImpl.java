/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.PriceTierService;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.catalog.spec.model.pricetier.PriceTiersGetOptions;
import com.junbo.catalog.spec.resource.PriceTierResource;
import com.junbo.common.id.PriceTierId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Price tier resource implementation.
 */
public class PriceTierResourceImpl implements PriceTierResource {
    @Autowired
    private PriceTierService priceTierService;

    @Override
    public Promise<PriceTier> getPriceTier(PriceTierId tierId) {
        return Promise.pure(priceTierService.getPriceTier(tierId.getValue()));
    }

    @Override
    public Promise<Results<PriceTier>> getPriceTiers(@BeanParam PriceTiersGetOptions options) {
        List<PriceTier> attributes = priceTierService.getPriceTiers(options);
        Results<PriceTier> resultList = new Results<>();
        resultList.setItems(attributes);
        return Promise.pure(resultList);
    }

    @Override
    public Promise<PriceTier> createPriceTier(PriceTier priceTier) {
        return Promise.pure(priceTierService.create(priceTier));
    }

    @Override
    public Promise<PriceTier> update(PriceTierId tierId, PriceTier priceTier) {
        return Promise.pure(priceTierService.update(tierId.getValue(), priceTier));
    }

    @Override
    public Promise<Response> delete(PriceTierId tierId) {
        priceTierService.delete(tierId.getValue());
        return Promise.pure(Response.status(204).build());
    }
}
