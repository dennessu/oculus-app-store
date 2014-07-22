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
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.util.IdUtil;
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
        List<OfferRevision> revisions = offerService.getRevisions(options);
        if (!StringUtils.isEmpty(options.getLocale())) {
            for (final OfferRevision revision : revisions) {
                revision.setLocaleAccuracy(getLocaleAccuracy(revision.getLocales().get(options.getLocale())));
                revision.setLocales(new HashMap<String, OfferRevisionLocaleProperties>() {{
                    put(options.getLocale(), getLocaleProperties(revision, options.getLocale()));
                }});
            }
        }
        Results<OfferRevision> results = new Results<>();
        results.setItems(revisions);
        return Promise.pure(results);
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
        if (!StringUtils.isEmpty(options.getNextCursor())) {
            builder.queryParam("cursor", options.getNextCursor());
        } else {
            builder.queryParam("cursor", options.nextStart());
        }

        return builder.toTemplate();
    }

    @Override
    public Promise<OfferRevision> getOfferRevision(String revisionId, final OfferRevisionGetOptions options) {
        final OfferRevision revision = offerService.getRevision(revisionId);
        if (!StringUtils.isEmpty(options.getLocale())) {
            revision.setLocaleAccuracy(getLocaleAccuracy(revision.getLocales().get(options.getLocale())));
            revision.setLocales(new HashMap<String, OfferRevisionLocaleProperties>(){{
                put(options.getLocale(), getLocaleProperties(revision, options.getLocale()));
            }});
        }
        return Promise.pure(revision);
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
}
