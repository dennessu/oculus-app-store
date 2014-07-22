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
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

/**
 * Price tier resource implementation.
 */
public class PriceTierResourceImpl implements PriceTierResource {
    @Autowired
    private PriceTierService priceTierService;

    @Override
    public Promise<PriceTier> getPriceTier(String tierId) {
        return Promise.pure(priceTierService.getPriceTier(tierId));
    }

    @Override
    public Promise<Results<PriceTier>> getPriceTiers(@BeanParam PriceTiersGetOptions options) {
        List<PriceTier> attributes = priceTierService.getPriceTiers(options);
        Results<PriceTier> results = new Results<>();
        results.setItems(attributes);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        return Promise.pure(results);
    }

    private String buildNextUrl(PriceTiersGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getPriceTierIds())) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("price-tiers");
        builder.queryParam("count", options.getValidSize());
        if (!StringUtils.isEmpty(options.getNextCursor())) {
            builder.queryParam("cursor", options.getNextCursor());
        } else {
            builder.queryParam("cursor", options.nextStart());
        }

        return builder.toTemplate();
    }

    @Override
    public Promise<PriceTier> createPriceTier(PriceTier priceTier) {
        return Promise.pure(priceTierService.create(priceTier));
    }

    @Override
    public Promise<PriceTier> update(String tierId, PriceTier priceTier) {
        return Promise.pure(priceTierService.update(tierId, priceTier));
    }

    @Override
    public Promise<Response> delete(String tierId) {
        priceTierService.delete(tierId);
        return Promise.pure(Response.status(204).build());
    }
}
