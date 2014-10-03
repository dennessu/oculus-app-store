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
import com.junbo.catalog.auth.OfferAuthorizeCallbackFactory;
import com.junbo.catalog.clientproxy.LocaleFacade;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.spec.enums.LocaleAccuracy;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
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
 * Offer revision resource implementation.
 */
public class OfferRevisionResourceImpl implements OfferRevisionResource {
    @Autowired
    private OfferService offerService;

    @Autowired
    private OfferAuthorizeCallbackFactory offerAuthorizeCallbackFactory;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private LocaleFacade localeFacade;

    @Override
    public Promise<Results<OfferRevision>> getOfferRevisions(final OfferRevisionsGetOptions options) {
        checkRights(options);

        List<OfferRevision> revisions = offerService.getRevisions(options);
        for (final OfferRevision revision : revisions) {
            revision.setLocaleAccuracy(LocaleAccuracy.HIGH.name());
            if (!StringUtils.isEmpty(options.getLocale())) {
                revision.setLocaleAccuracy(getLocaleAccuracy(revision.getLocales().get(options.getLocale())));
                revision.setLocales(new HashMap<String, OfferRevisionLocaleProperties>() {{
                    put(options.getLocale(), getLocaleProperties(revision, options.getLocale()));
                }});
            }
        }
        Results<OfferRevision> results = new Results<>();
        results.setItems(revisions);
        results.setTotal(options.getTotal());
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        return Promise.pure(results);
    }

    private void checkRights(final OfferRevisionsGetOptions options) {
        boolean isDeveloper = isDeveloper();
        if (isAdmin()) {
            return;
        } else if (!isDeveloper || options.getPublisherId() == null) {
            // If the status is not provided, use default APPROVED filter
            if (options.getStatus() == null) {
                options.setStatus(Status.APPROVED.name());
            } else if (!Status.APPROVED.is(options.getStatus())) {
                // If a developer try to get non-APPROVED offer revision, the publisherId is required
                // (falling into this branch means the options.getPublisherId() is null)
                if (isDeveloper) {
                    throw AppCommonErrors.INSTANCE.fieldRequired("publisherId").exception();
                    // if a non-developer try to get non-APPROVED offer revision, throw forbidden exception.
                } else {
                    throw AppCommonErrors.INSTANCE.forbiddenWithMessage("User is not allowed to" +
                            " get offer revisions that have not been approved").exception();
                }
            }
            // This is a developer and the publisherId is provided
            // Do the authorization check.
        } else  {
            AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(options.getPublisherId());
            RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<OfferRevision>>() {
                @Override
                public Promise<OfferRevision> apply() {

                    // The user should have draft.read right to view the DRAFT/REJECTED offer revisions,
                    // which means the user is one member of the offer revision's organization.
                    // Usually a publisher A wants to search publisher B's offer revisions will reach this code block.
                    if (!AuthorizeContext.hasRights("draft.read")) {
                        // If the user doesn't provide status, use default APPROVED status filter.
                        if (StringUtils.isEmpty(options.getStatus())) {
                            options.setStatus(Status.APPROVED.name());
                        }
                        // If the status is provided (DRAFT/REJECTED) the publisher A wants to search
                        // publisher B's DRAFT offer revision, throw FORBIDDEN exception.
                        if (!Status.APPROVED.is(options.getStatus())) {
                            throw AppCommonErrors.INSTANCE.forbiddenWithMessage("User is not allowed to" +
                                    " get offer revisions that have not been approved").exception();
                        }
                    }

                    return Promise.pure(null);
                }
            });
        }
    }

    private String buildNextUrl(OfferRevisionsGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getOfferIds()) || !CollectionUtils.isEmpty(options.getRevisionIds())) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("offer-revisions");
        if (options.getStatus() != null) {
            builder.queryParam("status", options.getStatus().toUpperCase());
        }
        builder.queryParam("count", options.getValidSize());
        if (options.getPublisherId() != null) {
            builder.queryParam("publisherId", IdFormatter.encodeId(options.getPublisherId()));
        }
        if (!StringUtils.isEmpty(options.getNextCursor())) {
            builder.queryParam("cursor", options.getNextCursor());
        } else {
            builder.queryParam("cursor", options.nextStart());
        }

        return builder.toTemplate();
    }

    @Override
    public Promise<OfferRevision> getOfferRevision(final String revisionId, final OfferRevisionGetOptions options) {
        final OfferRevision revision = offerService.getRevision(revisionId);
        revision.setLocaleAccuracy(LocaleAccuracy.HIGH.name());
        if (!StringUtils.isEmpty(options.getLocale())) {
            revision.setLocaleAccuracy(getLocaleAccuracy(revision.getLocales().get(options.getLocale())));
            revision.setLocales(new HashMap<String, OfferRevisionLocaleProperties>(){{
                put(options.getLocale(), getLocaleProperties(revision, options.getLocale()));
            }});
        }

        boolean isDeveloper = isDeveloper();
        if (!Status.APPROVED.is(revision.getStatus()) && !Status.OBSOLETE.is(revision.getStatus()) && !isDeveloper) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("offer-revision", revisionId).exception();
        } else if (Status.APPROVED.is(revision.getStatus()) || Status.OBSOLETE.is(revision.getStatus())) {
            return Promise.pure(revision);
        } else {
            AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(revision.getOfferId());
            return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<OfferRevision>>() {
                @Override
                public Promise<OfferRevision> apply() {

                    if (!AuthorizeContext.hasRights("draft.read")) {
                        throw AppCommonErrors.INSTANCE.resourceNotFound("offer-revision", revisionId).exception();
                    }

                    return Promise.pure(revision);
                }
            });
        }
    }

    @Override
    public Promise<OfferRevision> createOfferRevision(final OfferRevision offerRevision) {
        if (offerRevision.getOfferId()==null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("offer").exception();
        }
        AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(offerRevision.getOfferId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<OfferRevision>>() {
            @Override
            public Promise<OfferRevision> apply() {

                if (!AuthorizeContext.hasRights("create")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                return Promise.pure(offerService.createRevision(offerRevision));
            }
        });
    }

    @Override
    public Promise<OfferRevision> updateOfferRevision(final String revisionId, final OfferRevision offerRevision) {
        if (offerRevision.getOfferId()==null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("offer").exception();
        }
        AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(offerRevision.getOfferId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<OfferRevision>>() {
            @Override
            public Promise<OfferRevision> apply() {

                if (!AuthorizeContext.hasRights("update")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                if (Status.APPROVED.is(offerRevision.getStatus()) && !AuthorizeContext.hasRights("approve")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                if (Status.REJECTED.is(offerRevision.getStatus()) && !AuthorizeContext.hasRights("reject")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                if (Status.OBSOLETE.is(offerRevision.getStatus()) && !AuthorizeContext.hasRights("obsolete")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                return Promise.pure(offerService.updateRevision(revisionId, offerRevision));
            }
        });
    }

    @Override
    public Promise<Response> delete(final String revisionId) {
        final OfferRevision offerRevision = offerService.getRevision(revisionId);
        if (offerRevision == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("offer-revision", revisionId).exception();
        }

        AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(offerRevision.getOfferId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Response>>() {
            @Override
            public Promise<Response> apply() {

                if (!AuthorizeContext.hasRights("delete")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                offerService.deleteRevision(revisionId);
                return Promise.pure(Response.status(204).build());
            }
        });
    }

    private OfferRevisionLocaleProperties getLocaleProperties(OfferRevision revision, String locale) {
        if (revision == null || locale == null) {
            return new OfferRevisionLocaleProperties();
        }
        Map<String, String> localeRelations = localeFacade.getLocaleRelations();
        OfferRevisionLocaleProperties result = revision.getLocales().get(locale);
        if (result == null) {
            result = new OfferRevisionLocaleProperties();
        }
        String fallbackLocale = locale;
        while (!checkOfferRevisionLocales(result)) {
            if (localeRelations.get(fallbackLocale) == null) {
                break;
            }
            fallbackLocale = localeRelations.get(fallbackLocale);
            OfferRevisionLocaleProperties fallbackLocaleProperties = revision.getLocales().get(fallbackLocale);
            if (fallbackLocaleProperties != null) {
                addFallbackProperties(result, fallbackLocaleProperties);
            }
        }
        return result;
    }

    // TODO: don't use reflection in future
    private void addFallbackProperties(OfferRevisionLocaleProperties properties,
                                       OfferRevisionLocaleProperties fallbackProperties) {
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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // TODO: don't use reflection in future
    private boolean checkOfferRevisionLocales(OfferRevisionLocaleProperties properties) {
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
    private String getLocaleAccuracy(OfferRevisionLocaleProperties properties) {
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
        return AuthorizeContext.hasAnyScope(new String[] {"catalog.admin", "catalog.service"});
    }

    private boolean isDeveloper() {
        return AuthorizeContext.hasAnyScope(new String[] {"catalog.developer", "catalog.admin", "catalog.service"});
    }
}
