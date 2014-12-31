/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.TosId
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.identity.spec.v1.resource.LocaleResource
import com.junbo.identity.spec.v1.resource.TosResource
import com.junbo.identity.spec.v1.resource.UserTosAgreementResource
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.oauth.common.cache.Cache
import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 * TosUtil.
 */
@CompileStatic
class TosUtil {

    public static final String HEADER_IP_GEO_LOCATION = "oculus-geoip-country-code"

    private Cache tosCache

    private TosResource tosResource

    private CountryResource countryResource

    private LocaleResource localeResource

    private String defaultLocale

    private UserTosAgreementResource userTosAgreementResource

    @Required
    void setTosCache(Cache tosCache) {
        this.tosCache = tosCache
    }

    @Required
    void setTosResource(TosResource tosResource) {
        this.tosResource = tosResource
    }

    @Required
    void setCountryResource(CountryResource countryResource) {
        this.countryResource = countryResource
    }

    @Required
    void setLocaleResource(LocaleResource localeResource) {
        this.localeResource = localeResource
    }

    @Required
    void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale
    }

    @Required
    void setUserTosAgreementResource(UserTosAgreementResource userTosAgreementResource) {
        this.userTosAgreementResource = userTosAgreementResource
    }

    Set<TosId> getRequiredTos(Client client, User user) {
        Set<TosId> unacceptedTosIds = []
        CountryId validCountry = getValidCountry(user)
        if (client.requiredTos != null && !client.requiredTos.isEmpty() && validCountry != null) {
            List<TosId> tosIds = getTosIds(client, validCountry, user)
            for (TosId tosId : tosIds) {
                UserTosAgreementListOptions options = new UserTosAgreementListOptions(
                        userId: user.getId(),
                        tosId: tosId
                )

                def results = userTosAgreementResource.list(options).get()
                if (results.items.isEmpty()) {
                    unacceptedTosIds.add(tosId)
                }
            }
        }

        return unacceptedTosIds
    }

    private List<TosId> getTosIds(final Client client, final CountryId country, final User user) {
        final List<TosId> tosId = []
        for (Client.RequiredTos requiredTos : client.requiredTos) {
            TosListOptions options = new TosListOptions(
                    type: requiredTos.type,
                    title: requiredTos.title,
                    countryId: country,
                    state: 'APPROVED'
            )
            def results = tosResource.list(options).get()

            if (!CollectionUtils.isEmpty(results.items)) {
                List<Tos> latestTosList = getLatestTosList(results.items)
                if (!CollectionUtils.isEmpty(latestTosList)) {
                    LocaleId localeId = getLocale(user, country)
                    Tos supportedTos = getSupportedTos(latestTosList, localeId)
                    if (supportedTos != null) {
                        tosId.add(supportedTos.getId())
                    }
                }
            }
        }

        return tosId
    }

    private List<Tos> getLatestTosList(List<Tos> tosList) {
        if (CollectionUtils.isEmpty(tosList)) {
            return new ArrayList<Tos>()
        }

        Double max = Double.parseDouble(tosList.get(0).version)
        for (Tos tos : tosList) {
            Double current = Double.parseDouble(tos.version)
            if (current > max) {
                max = current
            }
        }

        List<Tos> result = new ArrayList<>()
        for (Tos tos : tosList) {
            Double current = Double.parseDouble(tos.version)
            if (max == current) {
                result.add(tos)
            }
        }

        return result
    }

    LocaleId getLocale(User user, CountryId countryId) {
        if (user != null && user.preferredLocale != null) {
            return user.preferredLocale
        }

        if (JunboHttpContext.acceptableLanguage != null) {
            return new LocaleId(JunboHttpContext.acceptableLanguage)
        }

        if (countryId != null) {
            Country existingCountry = countryResource.get(countryId, new CountryGetOptions()).get()
            if (existingCountry.defaultLocale != null) {
                return existingCountry.defaultLocale
            }
        }

        return new LocaleId(defaultLocale)
    }

    private CountryId getValidCountry(User user) {
        if (user.countryOfResidence != null) {
            return user.countryOfResidence
        }

        if (JunboHttpContext.getRequestHeaders()!= null &&
                !CollectionUtils.isEmpty(JunboHttpContext.getRequestHeaders().get(HEADER_IP_GEO_LOCATION))) {
            String country = JunboHttpContext.getRequestHeaders().getFirst(HEADER_IP_GEO_LOCATION)
            return new CountryId(country)
        }

        return null
    }

    private Tos getSupportedTos(List<Tos> tosList, LocaleId localeId) {
        if (CollectionUtils.isEmpty(tosList) || localeId == null) {
            return null
        }

        Map<String, Boolean> circle = new HashMap<>()

        LocaleId current = localeId
        LocaleId fallback = null
        while (true) {
            com.junbo.identity.spec.v1.model.Locale locale = localeResource.get(current, new LocaleGetOptions()).get()
            Boolean visited = circle.get(locale.getId().toString())
            if (visited) {
                break
            }
            circle.put(locale.getId().toString(), true)
            for (Tos tos : tosList) {
                if (!CollectionUtils.isEmpty(tos.locales)) {
                    if (tos.locales.any { LocaleId tosLocaleId ->
                        return tosLocaleId == current
                    }) {
                        return tos
                    }
                }
            }

            fallback = locale.fallbackLocale
            if (current == fallback || fallback == null) {
                break
            }
            current = fallback
        }

        return null
    }
}
