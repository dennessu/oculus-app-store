package com.junbo.data.handler

import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppErrorException
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.identity.spec.v1.resource.LocaleResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource
import org.springframework.util.StringUtils

/**
 * Created by haomin on 14-6-3.
 */
@CompileStatic
class LocaleDataHandler extends BaseDataHandler {
    private LocaleResource localeResource

    private boolean localeOverride

    @Required
    void setLocaleResource(LocaleResource localeResource) {
        this.localeResource = localeResource
    }

    @Required
    void setLocaleOverride(boolean localeOverride) {
        this.localeOverride = localeOverride
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        Map<String, Resource> localeCodeToResourceMap = new HashMap<>()
        Map<String, Locale> localeMap = new HashMap<>()

        resources.each { Resource resource ->
            String content = IOUtils.toString(resource.URI, "UTF-8")
            try {
                Locale locale = transcoder.decode(new TypeReference<Locale>() {}, content) as Locale
                localeMap.put(locale.localeCode, locale)
                localeCodeToResourceMap.put(locale.localeCode, resource)
            } catch (Exception e) {
                logger.error("Error parsing locale $content", e)
                exit()
            }
        }

        logger.info("resolve locale dependency")

        List<Resource> resolvedResource = new ArrayList<>()
        List<String> result = new ArrayList<>()
        localeMap.each { Map.Entry<String, Locale> entry ->
            String localeCode = entry.key

            List<String> temp = new ArrayList<>()
            resolveLocales(localeCode, localeMap, temp)

            result.addAll(temp.reverse())

            result.unique { String value ->
                return value
            }
        }

        logger.info("resolve resolves: " + result.join(','))

        result.each { String localeCode ->
            resolvedResource.add(localeCodeToResourceMap.get(localeCode))
        }

        return resolvedResource.toArray(new Resource[resolvedResource.size()])
    }

    private void resolveLocales(String localeCode, Map<String, Locale> localeMap, List<String> result) {
        if (!result.contains(localeCode)) {
            result.push(localeCode)
            Locale locale = localeMap.get(localeCode)

            if (locale == null) {
                logger.error("Cannot find locale $localeCode")
                exit()
            }

            if (locale.fallbackLocale != null && !StringUtils.isEmpty(locale.fallbackLocale.value)) {
                resolveLocales(locale.fallbackLocale.value, localeMap, result)
            }
        }
    }

    @Override
    void handle(String content) {
        Locale locale
        try {
            locale = transcoder.decode(new TypeReference<Locale>() {}, content) as Locale
        } catch (Exception e) {
            logger.error("Error parsing locale $content", e)
            exit()
        }

        Locale existing = null
        try {
            existing = localeResource.get(new LocaleId(locale.localeCode), new LocaleGetOptions()).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        if (existing != null) {
            if (localeOverride) {
                logger.debug("Update the existed locale $locale.localeName.")
                try {
                    locale.createdTime = existing.createdTime
                    locale.updatedTime = existing.updatedTime
                    locale.adminInfo = existing.adminInfo
                    locale.rev = existing.rev
                    locale.id = existing.getId()

                    localeResource.put(new LocaleId(locale.localeCode), locale).get()
                } catch (Exception e) {
                    logger.error("Error creating locale $locale.localeName", e)
                }
            } else {
                logger.debug("$locale.localeCode already exists, skipped!")
            }
        } else {
            logger.debug('Create new locale with this content')
            try {
                localeResource.create(locale).get()
            } catch (Exception e) {
                logger.error("Error creating locale $locale.localeName", e)
            }
        }
    }
}
