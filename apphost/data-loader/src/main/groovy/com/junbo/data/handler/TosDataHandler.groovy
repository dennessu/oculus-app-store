package com.junbo.data.handler

import com.junbo.common.enumid.CountryId
import com.junbo.common.error.AppErrorException
import com.junbo.common.model.Results
import com.junbo.data.model.TosData
import com.junbo.identity.spec.v1.model.Tos
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
            existing = tosResource.list(new TosListOptions( title: tosData.title, type: tosData.type)).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        // Don't create two files with the same title and type, or it will be override
        if (existing != null && !CollectionUtils.isEmpty(existing.items)) {
            if (!alwaysOverwrite) {
                return;
            } else {
                existing.items.each { Tos tos ->
                    if (tos.state != 'APPROVED') {
                        tos.state = 'APPROVED'
                        try {
                            return tosResource.put(tos.getId(), tos).get()
                        } catch (Exception e) {
                            logger.error("Error updating tos $tos.title", e)
                        }
                    } else {
                        tos.title = tosData.title
                        tos.type = tosData.type
                        tos.version = tosData.version
                        tos.state = 'APPROVED'
                        if (!CollectionUtils.isEmpty(tosData.countries)) {
                            List<CountryId> countryIdList = new ArrayList<>()
                            tosData.countries.each { String countryId ->
                                countryIdList.add(new CountryId(countryId))
                            }
                            tos.countries = countryIdList
                        }
                        return tosResource.put(tos.getId(), tos).get()
                    }
                }
            }
        } else {
            logger.debug('Create new tos')
            try {
                Tos tos = new Tos(
                        title: tosData.title,
                        content: tosData.content,
                        type: tosData.type,
                        version: tosData.version,
                        state: 'DRAFT'
                )
                if(!CollectionUtils.isEmpty(tosData.countries)) {
                    List<CountryId> countryIdList = new ArrayList<>()
                    tosData.countries.each { String countryId ->
                        countryIdList.add(new CountryId(countryId))
                    }
                    tos.countries = countryIdList
                }
                tos = tosResource.create(tos).get()
                tos.state = 'APPROVED'
                tos = tosResource.put(tos.getId(), tos).get()
            } catch (Exception e) {
                logger.error("Error creating tos $tosData.title", e)
            }
        }
    }
}
