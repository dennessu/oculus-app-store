/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.clientproxy.LocaleFacade;
import com.junbo.catalog.core.OfferAttributeService;
import com.junbo.catalog.spec.enums.LocaleAccuracy;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttributeGetOptions;
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.catalog.spec.resource.OfferAttributeResource;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Attribute resource implementation.
 */
public class OfferAttributeResourceImpl extends ResourceSupport implements OfferAttributeResource {
    @Autowired
    private OfferAttributeService attributeService;
    @Autowired
    private LocaleFacade localeFacade;

    @Override
    public Promise<OfferAttribute> getAttribute(String attributeId, final OfferAttributeGetOptions options) {
        final OfferAttribute attribute = attributeService.getAttribute(attributeId);
        attribute.setLocaleAccuracy(LocaleAccuracy.HIGH.name());
        if (!StringUtils.isEmpty(options.getLocale())) {
            Map<String, String> localeRelations = localeFacade.getLocaleRelations();
            filterLocale(attribute, options.getLocale(), localeRelations);
        }
        return Promise.pure(attribute);
    }

    @Override
    public Promise<Results<OfferAttribute>> getAttributes(@BeanParam OfferAttributesGetOptions options) {
        List<OfferAttribute> attributes = attributeService.getAttributes(options);
        for (OfferAttribute attribute : attributes) {
            attribute.setLocaleAccuracy(LocaleAccuracy.HIGH.name());
        }
        if (!StringUtils.isEmpty(options.getLocale())) {
            Map<String, String> localeRelations = localeFacade.getLocaleRelations();
            for (OfferAttribute attribute : attributes) {
                filterLocale(attribute, options.getLocale(), localeRelations);
            }
        }
        Results<OfferAttribute> results = new Results<>();
        results.setItems(attributes);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        results.setTotal(options.getTotal());
        return Promise.pure(results);
    }

    private void filterLocale(final OfferAttribute attribute, final String locale, final Map<String, String> localeRelations) {
        if (!StringUtils.isEmpty(locale)) {
            attribute.setLocaleAccuracy(calLocaleAccuracy(attribute.getLocales().get(locale)));
            attribute.setLocales(new HashMap<String, SimpleLocaleProperties>() {{
                put(locale, getLocaleProperties(attribute.getLocales(), locale, localeRelations));
            }});
        }
    }

    private String buildNextUrl(OfferAttributesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getAttributeIds())) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("offer-attributes");
        if (options.getAttributeType() != null) {
            builder.queryParam("type", options.getAttributeType());
        }
        builder.queryParam("count", options.getValidSize());
        if (!StringUtils.isEmpty(options.getNextCursor())) {
            builder.queryParam("cursor", options.getNextCursor());
        } else {
            builder.queryParam("cursor", options.nextStart());
        }

        return builder.toTemplate();
    }

    @Override
    public Promise<OfferAttribute> createAttribute(OfferAttribute attribute) {
        return Promise.pure(attributeService.create(attribute));
    }

    @Override
    public Promise<OfferAttribute> update(String attributeId, OfferAttribute attribute) {
        return Promise.pure(attributeService.update(attributeId, attribute));
    }

    @Override
    public Promise<Response> delete(String attributeId) {
        attributeService.deleteAttribute(attributeId);
        return Promise.pure(Response.status(204).build());
    }
}
