package com.junbo.data.handler

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppErrorException
import com.junbo.common.model.Results
import com.junbo.data.model.TosData
import com.junbo.data.model.TosLocalePropertyData
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.model.TosLocaleProperty
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.identity.spec.v1.resource.TosResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource

/**
 * Created by liangfu on 8/28/2014.
 */
@CompileStatic
class TosDataHandler extends BaseDataHandler {
    private TosResource tosResource

    @Required
    void setTosResource(TosResource tosResource) {
        this.tosResource = tosResource
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Override
    void handle(String content) {
        TosData tosData
        try {
            tosData = transcoder.decode(new TypeReference<TosData>() {}, content) as TosData
        } catch (Exception e) {
            logger.error("Error parsing tos $content", e)
            exit()
        }

        Results<Tos> existing = null
        try {
            existing = tosResource.list(new TosListOptions(type: tosData.type)).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        // Don't create two files with the same title and type, or it will be override
        if (existing != null && !CollectionUtils.isEmpty(existing.items)) {
            logger.debug('tos exists, skip')
        } else {
            logger.debug('Create new tos')
            try {
                Tos tos = new Tos(
                        content: tosData.content,
                        type: tosData.type,
                        version: tosData.version,
                        state: 'DRAFT',
                        minorversion: tosData.minorversion
                )
                if (!CollectionUtils.isEmpty(tosData.countries)) {
                    List<CountryId> countryIdList = new ArrayList<>()
                    tosData.countries.each { String countryId ->
                        countryIdList.add(new CountryId(countryId))
                    }
                    tos.countries = countryIdList
                }
                if (!CollectionUtils.isEmpty(tosData.coveredLocales)) {
                    List<LocaleId> localeIdList = new ArrayList<>()
                    tosData.coveredLocales.each { String localeId ->
                        localeIdList.add(new LocaleId(localeId))
                    }
                    tos.coveredLocales = localeIdList
                }
                if (tosData.locales != null && !tosData.locales.isEmpty()) {
                    Map<String, TosLocaleProperty> localePropertyMap = new HashMap<>()
                    tosData.locales.each { Map.Entry<String, TosLocalePropertyData> entry ->
                        TosLocaleProperty tosLocaleProperty = new TosLocaleProperty()
                        tosLocaleProperty.setTitle(entry.getValue().title)
                        localePropertyMap.put(entry.key, tosLocaleProperty)
                    }
                    tos.locales = localePropertyMap
                }
                tos = tosResource.create(tos).get()
                tos.state = 'APPROVED'
                tos = tosResource.put(tos.getId(), tos).get()
            } catch (Exception e) {
                logger.error("Error creating tos $tosData.type", e)
            }
        }
    }
}
