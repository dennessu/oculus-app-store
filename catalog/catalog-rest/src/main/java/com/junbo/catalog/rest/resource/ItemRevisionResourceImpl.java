/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.authorization.RightsScope;
import com.junbo.catalog.auth.ItemAuthorizeCallbackFactory;
import com.junbo.catalog.clientproxy.LocaleFacade;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.spec.enums.LocaleAccuracy;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.item.*;
import com.junbo.catalog.spec.resource.ItemRevisionResource;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Item revision resource implementation.
 */
public class ItemRevisionResourceImpl implements ItemRevisionResource {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemAuthorizeCallbackFactory itemAuthorizeCallbackFactory;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private LocaleFacade localeFacade;

    @Override
    public Promise<Results<ItemRevision>> getItemRevisions(final ItemRevisionsGetOptions options) {
        List<ItemRevision> revisions = itemService.getRevisions(options);
        if (!StringUtils.isEmpty(options.getLocale())) {
            for (final ItemRevision revision : revisions) {
                revision.setLocaleAccuracy(getLocaleAccuracy(revision.getLocales().get(options.getLocale())));
                revision.setLocales(new HashMap<String, ItemRevisionLocaleProperties>() {{
                        put(options.getLocale(), getLocaleProperties(revision, options.getLocale()));
                }});
            }
        }
        Results<ItemRevision> results = new Results<>();
        results.setItems(revisions);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        return Promise.pure(results);
    }

    private String buildNextUrl(ItemRevisionsGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getItemIds()) || !CollectionUtils.isEmpty(options.getRevisionIds())) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("item-revisions");
        if (options.getStatus() != null) {
            builder.queryParam("status", options.getStatus().toUpperCase());
        }
        builder.queryParam("size", options.getValidSize());
        if (!StringUtils.isEmpty(options.getNextBookmark())) {
            builder.queryParam("bookmark", options.getNextBookmark());
        } else {
            builder.queryParam("start", options.nextStart());
        }

        return builder.toTemplate();
    }

    @Override
    public Promise<ItemRevision> getItemRevision(String revisionId, final ItemRevisionGetOptions options) {
        final ItemRevision itemRevision = itemService.getRevision(revisionId);
        if (!StringUtils.isEmpty(options.getLocale())) {
            itemRevision.setLocaleAccuracy(getLocaleAccuracy(itemRevision.getLocales().get(options.getLocale())));
            itemRevision.setLocales(new HashMap<String, ItemRevisionLocaleProperties>(){{
                put(options.getLocale(), getLocaleProperties(itemRevision, options.getLocale()));
            }});
        }
        return Promise.pure(itemRevision);
    }

    @Override
    public Promise<ItemRevision> createItemRevision(final ItemRevision itemRevision) {
        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(itemRevision.getItemId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<ItemRevision>>() {
            @Override
            public Promise<ItemRevision> apply() {

                if (!AuthorizeContext.hasRights("create")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                return Promise.pure(itemService.createRevision(itemRevision));
            }
        });
    }

    @Override
    public Promise<ItemRevision> updateItemRevision(final String revisionId, final ItemRevision itemRevision) {
        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(itemRevision.getItemId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<ItemRevision>>() {
            @Override
            public Promise<ItemRevision> apply() {

                if (!AuthorizeContext.hasRights("update")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                return Promise.pure(itemService.updateRevision(revisionId, itemRevision));
            }
        });
    }

    @Override
    public Promise<Response> delete(final String revisionId) {
        ItemRevision itemRevision = itemService.getRevision(revisionId);
        if (itemRevision == null) {
            throw AppErrors.INSTANCE.notFound("item-revision", revisionId).exception();
        }

        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(itemRevision.getItemId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Response>>() {
            @Override
            public Promise<Response> apply() {

                if (!AuthorizeContext.hasRights("delete")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                itemService.deleteRevision(revisionId);
                return Promise.pure(Response.status(204).build());
            }
        });
    }

    private ItemRevisionLocaleProperties getLocaleProperties(ItemRevision revision, String locale) {
        if (revision == null || locale == null) {
            return new ItemRevisionLocaleProperties();
        }
        Map<String, String> localeRelations = localeFacade.getLocaleRelations();
        ItemRevisionLocaleProperties result = revision.getLocales().get(locale);
        if (result == null) {
            result = new ItemRevisionLocaleProperties();
        }
        String fallbackLocale = locale;
        while (!checkItemRevisionLocales(result)) {
            if (localeRelations.get(fallbackLocale) == null) {
                break;
            }
            fallbackLocale = localeRelations.get(fallbackLocale);
            ItemRevisionLocaleProperties fallbackLocaleProperties = revision.getLocales().get(fallbackLocale);
            if (fallbackLocaleProperties != null) {
                addFallbackProperties(result, fallbackLocaleProperties);
            }
        }
        return result;
    }

    // TODO: don't use reflection in future
    private void addFallbackProperties(ItemRevisionLocaleProperties properties,
                                       ItemRevisionLocaleProperties fallbackProperties) {
        try {
            Map<String, Object> fields = PropertyUtils.describe(properties);
            for(String fieldName : fields.keySet()) {
                if (PropertyUtils.getProperty(properties, fieldName) == null) {
                    PropertyUtils.setProperty(properties, fieldName,
                            PropertyUtils.getProperty(fallbackProperties, fieldName));
                }
            }
        } catch (Exception e) {
            //
        }
    }

    // TODO: don't use reflection in future
    private boolean checkItemRevisionLocales(ItemRevisionLocaleProperties properties) {
        try {
            Map<String, Object> fields = PropertyUtils.describe(properties);
            for(String fieldName : fields.keySet()) {
                if (PropertyUtils.getProperty(properties, fieldName) == null) {
                    return false;
                }
            }
        } catch (Exception e) {
            //
        }
        return true;
    }

    // TODO: don't use reflection in future
    private String getLocaleAccuracy(ItemRevisionLocaleProperties properties) {
        if (properties == null) {
            return LocaleAccuracy.LOW.name();
        }
        boolean containsNull = false, containsNonNull = false;
        try {
            Map<String, Object> fields = PropertyUtils.describe(properties);
            for(String fieldName : fields.keySet()) {
                if (PropertyUtils.getProperty(properties, fieldName) == null) {
                    containsNull = true;
                } else {
                    containsNonNull = true;
                }
            }
        } catch (Exception e) {
            //
        }

        if (containsNull && containsNonNull) {
            return LocaleAccuracy.MEDIUM.name();
        } else if (containsNull) {
            return LocaleAccuracy.LOW.name();
        } else {
            return LocaleAccuracy.HIGH.name();
        }
    }
}
