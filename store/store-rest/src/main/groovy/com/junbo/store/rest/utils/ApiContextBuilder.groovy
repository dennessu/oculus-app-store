package com.junbo.store.rest.utils
import com.fasterxml.jackson.core.type.TypeReference
import com.junbo.authorization.AuthorizeContext
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.clientproxy.error.ErrorCodes
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Platform
import com.junbo.store.spec.model.StoreApiHeader
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.util.regex.Pattern
/**
 * The ApiContextBuilder class.
 */
@CompileStatic
@Component('storeContextBuilder')
class ApiContextBuilder implements InitializingBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(ApiContextBuilder)

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Resource(name = 'storeIdentityUtils')
    private IdentityUtils identityUtils

    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    private String defaultCountry = 'US'

    private String defaultLocale = 'en_US'

    private String localeWildCard = '*'

    @Value('${store.locale.mapping}')
    private String localeMappingString;

    @Value('${store.locale.mapping.enabled}')
    private boolean localeMappingEnabled;

    private Map<String, String> localeMapping = [:] as Map;


    private final Pattern androidIdPattern = Pattern.compile('[a-fA-F\\d]{1,16}')

    Promise<ApiContext> buildApiContext() {
        ApiContext result = new ApiContext()
        result.contextData = new HashMap<>()
        result.platform = Platform.ANDROID
        result.userAgent = getHeader(StoreApiHeader.USER_AGENT)
        result.androidId = getHeader(StoreApiHeader.ANDROID_ID)
        result.deviceId = getHeader(StoreApiHeader.DEVICE_ID)
        result.user = (AuthorizeContext.currentUserId?.value == null || AuthorizeContext.currentUserId?.value == 0) ? null : AuthorizeContext.currentUserId
        String countryCode = getHeader(StoreApiHeader.IP_COUNTRY)

        result.platformVersion = getHeader(StoreApiHeader.ANDROID_VERSION)
        result.ipAddress = getHeader(StoreApiHeader.USER_IP)
        fillClientNameAndVersion(result.userAgent, result)

        if (!StringUtils.isEmpty(result.androidId) && !androidIdPattern.matcher(result.androidId).matches()) {
            LOGGER.warn('name=Invalid_AndroidId_Pattern, androidId={}', result.androidId)
        }
        Promise.pure().then { // get country.
            if (StringUtils.isEmpty(countryCode)) {
                countryCode = defaultCountry
            }
            resourceContainer.countryResource.get(new CountryId(countryCode), new CountryGetOptions()).recover { Throwable ex ->
                LOGGER.error('name=Store_Invalid_CountryCode, countryCode={}, default={}', countryCode, defaultCountry, ex)
                resourceContainer.countryResource.get(new CountryId(defaultCountry),  new CountryGetOptions())
            }.then { Country country ->
                result.country = country
                return Promise.pure()
            }
        }.then { // get locale
            getLocale().then { com.junbo.identity.spec.v1.model.Locale e ->
                result.locale = e
                return Promise.pure()
            }
        }.then {
            LOGGER.info("name=Store_Build_Context, androidId={}, countryCode={}, actualCountryCode={}", result.androidId, countryCode, result.getCountry().getId().value)
            return Promise.pure(result)
        }
    }
    
    public String doLocaleMapping(String localeId) {
        if (!localeMappingEnabled || localeId == null) {
            return localeId;
        }
        return localeMapping.containsKey(localeId) ? localeMapping[localeId] : localeId
    }

    private static String getHeader(StoreApiHeader header) {
        return JunboHttpContext.requestHeaders.getFirst(header.value)
    }

    private static void fillClientNameAndVersion(String userAgent, ApiContext apiContext) {
        String[] splits = userAgent.trim().split('\\s')
        if (splits.length > 0) {
            splits = splits[0].trim().split('/')

            if (splits.length > 0) {
                apiContext.clientName = splits[0]
            }

            if (splits.length > 1) {
                apiContext.clientVersion = splits[1]
            }
        }
    }

    private Promise<com.junbo.identity.spec.v1.model.Locale> getLocale() {
        String localeId = doLocaleMapping(JunboHttpContext.acceptableLanguage);

        resourceContainer.localeResource.get(new LocaleId(localeId), new LocaleGetOptions()).recover { Throwable ex ->
            if (appErrorUtils.isAppError(ex, ErrorCodes.Identity.LocaleNotFound)) {
                LOGGER.warn('name=Store_Locale_Not_Found, locale={}, fallback to default', localeId)
                String[] locales = localeId.toString().split('_')
                if (locales.size() != 2) {
                    return resourceContainer.localeResource.get(new LocaleId(defaultLocale), new LocaleGetOptions())
                }
                CountryId countryId = new CountryId(locales[1])
                return resourceContainer.countryResource.get(countryId, new CountryGetOptions()).recover { Throwable t ->
                    if (appErrorUtils.isAppError(t, ErrorCodes.Identity.CountryNotFound)) {
                        LOGGER.warn('name=Store_Locale_Country_Not_Found, country={}, fallback to default', countryId)
                        return Promise.pure(null)
                    }
                    throw t
                }.then { Country country ->
                    if (country == null || country.defaultLocale == null) {
                        return resourceContainer.localeResource.get(new LocaleId(defaultLocale), new LocaleGetOptions())
                    }

                    // If the default locale isn't exist, fall back to default
                    return resourceContainer.localeResource.get(country.defaultLocale, new LocaleGetOptions()).recover { Throwable ex2 ->
                        if (appErrorUtils.isAppError(ex2, ErrorCodes.Identity.LocaleNotFound)) {
                            LOGGER.warn('name=Store_Locale_Not_Found_Again, locale={}, fallback to default', country.defaultLocale)
                            return resourceContainer.localeResource.get(new LocaleId(defaultLocale), new LocaleGetOptions())
                        }
                        throw ex2
                    }
                }
            }
            throw ex
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        if (!StringUtils.isBlank(localeMappingString)) {
            localeMapping = (Map<String, String>) ObjectMapperProvider.instanceNotStrict().readValue(localeMappingString, new TypeReference<Map<String, String>>() {})
        }
    }
}
