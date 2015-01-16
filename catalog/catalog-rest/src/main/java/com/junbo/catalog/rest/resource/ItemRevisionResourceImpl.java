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
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.item.*;
import com.junbo.catalog.spec.resource.ItemRevisionResource;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
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
        checkRights(options);

        List<ItemRevision> revisions = itemService.getRevisions(options);
        for (ItemRevision revision : revisions) {
            revision.setLocaleAccuracy(LocaleAccuracy.HIGH.name());
        }

        if (!StringUtils.isEmpty(options.getLocale())) {
            final Map<String, String> localeRelations = localeFacade.getLocaleRelations();
            for (final ItemRevision revision : revisions) {
                revision.setLocaleAccuracy(getLocaleAccuracy(revision.getLocales().get(options.getLocale())));
                revision.setLocales(new HashMap<String, ItemRevisionLocaleProperties>() {{
                    put(options.getLocale(), getLocaleProperties(revision, options.getLocale(), localeRelations));
                }});
            }
        }

        Results<ItemRevision> results = new Results<>();
        results.setItems(revisions);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        results.setTotal(options.getTotal());
        return Promise.pure(results);
    }

    private void checkRights(final ItemRevisionsGetOptions options) {
        boolean isDeveloper = isDeveloper();
        if (isAdmin()) {
            return;
        } else if (!isDeveloper || options.getDeveloperId() == null) {
            // If the status is not provided, use default APPROVED filter
            if (options.getStatus() == null) {
                options.setStatus(Status.APPROVED.name());
            } else if (!Status.APPROVED.is(options.getStatus())) {
                // If a developer try to get non-APPROVED item revision, the developerId is required
                // (falling into this branch means the options.getDeveloperId() is null)
                if (isDeveloper) {
                    throw AppCommonErrors.INSTANCE.fieldRequired("developerId").exception();
                    // if a non-developer try to get non-APPROVED item revision, throw forbidden exception.
                } else {
                    throw AppCommonErrors.INSTANCE.forbiddenWithMessage("User is not allowed to" +
                            " get item revisions that have not been approved").exception();
                }
            }
            // This is a developer and the developerId is provided
            // Do the authorization check.
        } else {
            AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(options.getDeveloperId());
            RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<ItemRevision>>() {
                @Override
                public Promise<ItemRevision> apply() {

                    // The user should have draft.read right to view the DRAFT/REJECTED item revisions,
                    // which means the user is one member of the item revision's organization.
                    // Usually a developer A wants to search developer B's item revisions will reach this code block.
                    if (!AuthorizeContext.hasRights("draft.read")) {
                        // If the user doesn't provide status, use default APPROVED status filter.
                        if (StringUtils.isEmpty(options.getStatus())) {
                            options.setStatus(Status.APPROVED.name());
                        }
                        // If the status is provided (DRAFT/REJECTED) the developer A wants to search
                        // developer B's DRAFT item revision, throw FORBIDDEN exception.
                        if (!Status.APPROVED.is(options.getStatus())) {
                            throw AppCommonErrors.INSTANCE.forbiddenWithMessage("User is not allowed to" +
                                    " get item revisions that have not been approved").exception();
                        }
                    }

                    return Promise.pure(null);
                }
            });
        }
    }

    private String buildNextUrl(ItemRevisionsGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getItemIds()) || !CollectionUtils.isEmpty(options.getRevisionIds())) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("item-revisions");
        if (options.getStatus() != null) {
            builder.queryParam("status", options.getStatus().toUpperCase());
        }
        if (options.getDeveloperId() != null) {
            builder.queryParam("developerId", IdFormatter.encodeId(options.getDeveloperId()));
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
    public Promise<ItemRevision> getItemRevision(final String revisionId, final ItemRevisionGetOptions options) {
        final ItemRevision itemRevision = itemService.getRevision(revisionId);
        itemRevision.setLocaleAccuracy(LocaleAccuracy.HIGH.name());
        if (!StringUtils.isEmpty(options.getLocale())) {
            final Map<String, String> localeRelations = localeFacade.getLocaleRelations();
            itemRevision.setLocaleAccuracy(getLocaleAccuracy(itemRevision.getLocales().get(options.getLocale())));
            itemRevision.setLocales(new HashMap<String, ItemRevisionLocaleProperties>(){{
                put(options.getLocale(), getLocaleProperties(itemRevision, options.getLocale(), localeRelations));
            }});
        }

        boolean isDeveloper = isDeveloper();
        if (!Status.APPROVED.is(itemRevision.getStatus()) && !isDeveloper) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("item-revision", revisionId).exception();
        } else if (Status.APPROVED.is(itemRevision.getStatus())) {
            return Promise.pure(itemRevision);
        } else {
            AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(itemRevision.getItemId());
            return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<ItemRevision>>() {
                @Override
                public Promise<ItemRevision> apply() {

                    if (!AuthorizeContext.hasRights("draft.read")) {
                        throw AppCommonErrors.INSTANCE.resourceNotFound("item-revision", revisionId).exception();
                    }

                    return Promise.pure(itemRevision);
                }
            });
        }
    }

    @Override
    public Promise<ItemRevision> createItemRevision(final ItemRevision itemRevision) {
        if (itemRevision.getItemId()==null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("item").exception();
        }
        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(itemRevision.getItemId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<ItemRevision>>() {
            @Override
            public Promise<ItemRevision> apply() {

                if (!AuthorizeContext.hasRights("create")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                return Promise.pure(itemService.createRevision(itemRevision));
            }
        });
    }

    @Override
    public Promise<ItemRevision> updateItemRevision(final String revisionId, final ItemRevision itemRevision) {
        if (itemRevision.getItemId()==null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("item").exception();
        }
        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(itemRevision.getItemId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<ItemRevision>>() {
            @Override
            public Promise<ItemRevision> apply() {

                if (!AuthorizeContext.hasRights("update")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                if (Status.APPROVED.is(itemRevision.getStatus()) && !AuthorizeContext.hasRights("approve")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                if (Status.REJECTED.is(itemRevision.getStatus()) && !AuthorizeContext.hasRights("reject")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                return Promise.pure(itemService.updateRevision(revisionId, itemRevision));
            }
        });
    }

    @Override
    public Promise<Response> delete(final String revisionId) {
        ItemRevision itemRevision = itemService.getRevision(revisionId);
        if (itemRevision == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("item-revision", revisionId).exception();
        }

        AuthorizeCallback<Item> callback = itemAuthorizeCallbackFactory.create(itemRevision.getItemId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Response>>() {
            @Override
            public Promise<Response> apply() {

                if (!AuthorizeContext.hasRights("delete")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                itemService.deleteRevision(revisionId);
                return Promise.pure(Response.status(204).build());
            }
        });
    }

    private ItemRevisionLocaleProperties getLocaleProperties(ItemRevision revision, String locale, Map<String, String> localeRelations) {
        if (revision == null || locale == null) {
            return new ItemRevisionLocaleProperties();
        }

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

    private boolean isAdmin() {
        return AuthorizeContext.hasAnyScope(new String[] {"catalog.admin", "catalog.service", "readonly.service"});
    }

    private boolean isDeveloper() {
        return AuthorizeContext.hasAnyScope(new String[] {"catalog.developer", "catalog.admin", "catalog.service", "catalog.update"});
    }
}
