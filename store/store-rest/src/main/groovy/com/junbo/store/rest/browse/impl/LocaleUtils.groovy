package com.junbo.store.rest.browse.impl

import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.store.rest.utils.ResourceContainer
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The LocaleUtils class.
 */
@Component('storeLocaleUtils')
@CompileStatic
class LocaleUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(LocaleUtils)

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    private int fallBackTimesLimit = 5

    public Object getLocaleProperties(Map properties, com.junbo.identity.spec.v1.model.Locale locale, String type, String id, String propertyName) {
        if (properties == null) {
            return null
        }

        Object result = properties.get(locale.getId().value)
        if (result != null) {
            return result
        }

        // get from fallback
        int fallBackTimes = 0
        while (locale.fallbackLocale != null && fallBackTimes < fallBackTimesLimit) {
            fallBackTimes++
            result = properties.get(locale.fallbackLocale.value)
            if (result != null) {
                break
            }
            // get the fallback locale
            locale = resourceContainer.localeResource.get(locale.fallbackLocale, new LocaleGetOptions()).get()
        }

        if (fallBackTimes >= fallBackTimesLimit) {
            LOGGER.warn('name=Store_Locale_Fallback_Limit_Reached, type={}, id={}, propertyName={}, limit={}', type, id, propertyName, fallBackTimesLimit)
        }
        return result
    }
}
