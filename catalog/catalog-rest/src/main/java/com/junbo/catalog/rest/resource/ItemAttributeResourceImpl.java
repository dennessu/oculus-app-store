/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.clientproxy.LocaleFacade;
import com.junbo.catalog.core.ItemAttributeService;
import com.junbo.catalog.spec.enums.LocaleAccuracy;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttributeGetOptions;
import com.junbo.catalog.spec.model.attribute.ItemAttributesGetOptions;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.catalog.spec.resource.ItemAttributeResource;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Attribute resource implementation.
 */
public class ItemAttributeResourceImpl extends ResourceSupport implements ItemAttributeResource {
    @Autowired
    private ItemAttributeService attributeService;
    @Autowired
    private LocaleFacade localeFacade;

    @Override
    public Promise<ItemAttribute> getAttribute(String attributeId, final ItemAttributeGetOptions options) {
        final ItemAttribute attribute = attributeService.getAttribute(attributeId);
        attribute.setLocaleAccuracy(LocaleAccuracy.HIGH.name());
        if (!StringUtils.isEmpty(options.getLocale())) {
            Map<String, String> localeRelations = localeFacade.getLocaleRelations();
            filterLocale(attribute, options.getLocale(), localeRelations);
        }
        return Promise.pure(attribute);
    }

    @Override
    public Promise<Results<ItemAttribute>> getAttributes(final ItemAttributesGetOptions options) {
        List<ItemAttribute> attributes = attributeService.getAttributes(options);
        for (ItemAttribute attribute : attributes) {
            attribute.setLocaleAccuracy(LocaleAccuracy.HIGH.name());
        }
        if (!StringUtils.isEmpty(options.getLocale())) {
            Map<String, String> localeRelations = localeFacade.getLocaleRelations();
            for (final ItemAttribute attribute : attributes) {
                filterLocale(attribute, options.getLocale(), localeRelations);
            }
        }
        Results<ItemAttribute> results = new Results<>();
        results.setItems(attributes);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        results.setTotal(options.getTotal());
        return Promise.pure(results);
    }

    private void filterLocale(final ItemAttribute attribute, final String locale, final Map<String, String> localeRelations) {
        if (!StringUtils.isEmpty(locale)) {
            attribute.setLocaleAccuracy(calLocaleAccuracy(attribute.getLocales().get(locale)));
            attribute.setLocales(new HashMap<String, SimpleLocaleProperties>() {{
                put(locale, getLocaleProperties(attribute.getLocales(), locale, localeRelations));
            }});
        }
    }

    private String buildNextUrl(ItemAttributesGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getAttributeIds())) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("item-attributes");
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
    public Promise<ItemAttribute> createAttribute(ItemAttribute attribute) {
        return Promise.pure(attributeService.create(attribute));
    }

    @Override
    public Promise<ItemAttribute> update(String attributeId, ItemAttribute attribute) {
        return Promise.pure(attributeService.update(attributeId, attribute));
    }

    @Override
    public Promise<Response> delete(String attributeId) {
        attributeService.deleteAttribute(attributeId);
        return Promise.pure(Response.status(204).build());
    }
}
