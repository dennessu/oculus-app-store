package com.junbo.store.rest.utils

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Platform
import com.junbo.store.spec.model.StoreApiHeader
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The ApiContextBuilder class.
 */
@CompileStatic
@Component('storeContextBuilder')
class ApiContextBuilder {

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeIdentityUtils')
    private IdentityUtils identityUtils

    Promise<ApiContext> buildApiContext() {
        ApiContext result = new ApiContext()
        result.contextData = new HashMap<>()
        result.platform = Platform.ANDROID
        result.userAgent = getHeader(StoreApiHeader.USER_AGENT)
        result.androidId = getHeader(StoreApiHeader.ANDROID_ID)
        result.user = (AuthorizeContext.currentUserId?.value == null || AuthorizeContext.currentUserId?.value == 0) ? null : AuthorizeContext.currentUserId

        Promise.pure().then { // get country. todo use ip geo or from sewer
            resourceContainer.countryResource.get(new CountryId('US'), new CountryGetOptions()).then { Country country ->
                result.country = country
                return Promise.pure()
            }
        }.then { // get locale
            getLocale().then { com.junbo.identity.spec.v1.model.Locale e ->
                result.locale = e
                return Promise.pure()
            }
        }.then { // get currency
            resourceContainer.currencyResource.get(result.country.defaultCurrency, new CurrencyGetOptions()).then { com.junbo.identity.spec.v1.model.Currency currency ->
                result.currency = currency
                return Promise.pure()
            }
        }.then {
            return Promise.pure(result)
        }
    }

    private static String getHeader(StoreApiHeader header) {
        return JunboHttpContext.requestHeaders.getFirst(header.value)
    }

    private Promise<com.junbo.identity.spec.v1.model.Locale> getLocale() {
        if (CollectionUtils.isEmpty(JunboHttpContext.acceptableLanguages)) {
            throw AppCommonErrors.INSTANCE.headerInvalid(StoreApiHeader.ACCEPT_LANGUAGE.value).exception()
        }

        Locale requestLocale = JunboHttpContext.acceptableLanguages.get(0)
        String localeId = requestLocale.language
        if (!StringUtils.isEmpty(requestLocale.country)) {
            localeId += "_${requestLocale.country}"
        }
        resourceContainer.localeResource.get(new LocaleId(localeId), new LocaleGetOptions()).then { com.junbo.identity.spec.v1.model.Locale e ->
            return Promise.pure(e)
        }
    }
}