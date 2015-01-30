/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.TosId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.identity.spec.v1.resource.LocaleResource
import com.junbo.identity.spec.v1.resource.TosResource
import com.junbo.identity.spec.v1.resource.UserTosAgreementResource
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.oauth.common.cache.Cache
import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils
/**
 * TosUtil.
 */
@CompileStatic
class TosUtil {

    public static final String HEADER_IP_GEO_LOCATION = "oculus-geoip-country-code"

    public static final int PAGE_SIZE = 100

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
            List<List<Tos>> tosList = getTosList(client, validCountry, user)
            LocaleId localeId = getLocale(user, validCountry)
            Set<TosId> acceptedTosIds = getAcceptedTosIds(user.getId())
            tosList.each { List<Tos> toses ->
                if (!toses.any {Tos tos -> acceptedTosIds.contains(tos.getId())}) {
                    unacceptedTosIds << selectTosForChallenge(toses, localeId).getId()
                }
            }
        }

        return unacceptedTosIds
    }

    private Set<TosId> getAcceptedTosIds(UserId userId) {
        Set<TosId> acceptedTosIds = []
        int offset = 0;
        while (true) {
            List<UserTosAgreement> results = userTosAgreementResource.list(new UserTosAgreementListOptions(userId: userId, offset: offset)).get().items;
            results.each { UserTosAgreement userTosAgreement ->
                acceptedTosIds.add(userTosAgreement.tosId);
            }
            if (results.size() < PAGE_SIZE)  {
                break
            }
            offset += PAGE_SIZE
        }
        return acceptedTosIds
    }

    private List<List<Tos>> getTosList(final Client client, final CountryId country, final User user) {
        final List<List<Tos>> tosList = []
        for (Client.RequiredTos requiredTos : client.requiredTos) {
            TosListOptions options = new TosListOptions(
                    type: requiredTos.type,
                    countryId: country,
                    state: 'APPROVED'
            )
            def results = tosResource.list(options).get()

            if (!CollectionUtils.isEmpty(results.items)) {
                List<Tos> latestTosList = getLatestTosList(results.items)
                if (!CollectionUtils.isEmpty(latestTosList)) {
                    tosList.add(latestTosList)
                }
            }
        }

        return tosList
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

    private Tos selectTosForChallenge(List<Tos> tosList, LocaleId localeId) {
        Assert.notEmpty(tosList)
        tosList = new ArrayList<Tos>(tosList).sort { Tos t -> return t.minorversion }.reverse()
        Tos tos = tosList.find {Tos t -> t.coveredLocales != null && t.coveredLocales.contains(localeId)}
        return tos == null ? tosList.first() : tos
    }
}
