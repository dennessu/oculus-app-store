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
import com.junbo.identity.spec.v1.resource.CountryResource
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
                Tos latest = results.items.get(0)
                for (Tos tos : results.items) {
                    if (compareVersion(latest.version, tos.version) < 0) {
                        latest = tos
                    }
                }

                if (isTosInSupportList(latest, user, country)) {
                    tosId.add(latest.getId())
                }
            }
        }

        return tosId
    }

    Boolean isTosInSupportList(Tos tos, User user, CountryId countryId) {
        if (CollectionUtils.isEmpty(tos.locales)) {
            return false
        }

        if (user.preferredLocale != null) {
            if (tos.locales.contains(user.preferredLocale)) {
                return true
            }
        }

        if (JunboHttpContext.acceptableLanguage != null) {
            if (tos.locales.any { LocaleId localeId ->
                return localeId.toString().equals(JunboHttpContext.acceptableLanguage)
            }) {
                return true
            }
        }

        Country existingCountry = countryResource.get(countryId, new CountryGetOptions()).get()
        if (tos.locales.contains(existingCountry.defaultLocale)) {
            return true
        }

        return tos.locales.any { LocaleId localeId ->
            return localeId.toString().equals(defaultLocale)
        }
    }

    static int compareVersion(String version1, String version2) {
        double v1 = Double.parseDouble(version1)
        double v2 = Double.parseDouble(version2)
        return Double.compare(v1, v2)
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
}
