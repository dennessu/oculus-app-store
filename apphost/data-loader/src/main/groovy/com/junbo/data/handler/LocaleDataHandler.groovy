package com.junbo.data.handler

import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppErrorException
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.identity.spec.v1.resource.LocaleResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-6-3.
 */
@CompileStatic
class LocaleDataHandler extends BaseDataHandler {
    private LocaleResource localeResource

    @Required
    void setLocaleResource(LocaleResource localeResource) {
        this.localeResource = localeResource
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

        logger.info("loading locale $locale.localeCode")

        Locale existing = null
        try {
            existing = localeResource.get(new LocaleId(locale.localeCode), new LocaleGetOptions()).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        if (existing != null) {
            if (alwaysOverwrite) {
                logger.debug("Overwrite Locale $locale.localeName with this content")
                locale.id = (LocaleId) existing.id
                locale.rev = existing.rev
                try {
                    localeResource.patch(locale.id as LocaleId, locale).get()
                } catch (Exception e) {
                    logger.error("Error updating locale $locale.localeName", e)
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
