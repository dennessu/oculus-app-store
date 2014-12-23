package com.junbo.emulator.casey.rest

import com.junbo.common.enumid.LocaleId
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The LocaleUtils class.
 */
@Component('caseyEmulatorLocaleUtils')
@CompileStatic
class LocaleUtils {

    @Resource(name = 'caseyResourceContainer')
    private ResourceContainer resourceContainer

    public Object getLocaleProperties(Map properties, com.junbo.identity.spec.v1.model.Locale locale) {
        if (properties == null) {
            return null
        }

        Object result = properties.get(locale.getId().value)
        if (result != null) {
            return result
        }

        // get from fallback
        Set<LocaleId> checkedLocale = [] as Set
        while (locale.fallbackLocale != null && !checkedLocale.contains(locale.fallbackLocale)) {
            checkedLocale << locale.fallbackLocale
            result = properties.get(locale.fallbackLocale.value)
            if (result != null) {
                break
            }
            // get the fallback locale
            locale = resourceContainer.localeResource.get(locale.fallbackLocale, new LocaleGetOptions()).get()
        }
        return result
    }
}
