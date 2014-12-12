package com.junbo.data.handler

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppErrorException
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.model.CommunicationLocale
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions
import com.junbo.identity.spec.v1.resource.CommunicationResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 10/16/14.
 */
@CompileStatic
class CommunicationDataHandler extends BaseDataHandler {
    private CommunicationResource communicationResource

    @Required
    void setCommunicationResource(CommunicationResource communicationResource) {
        this.communicationResource = communicationResource
    }

    @Override
    void handle(String content) {
        Communication communication
        try {
            communication = transcoder.decode(new TypeReference<Communication>() {}, content) as Communication
        } catch (Exception e) {
            logger.error("Error parsing communication $content", e)
            exit()
        }

        List<Communication> existing = null
        try {
            existing = communicationResource.list(new CommunicationListOptions()).get().items
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        Boolean exists = false
        String communicationTitle = getCommunicationTitle(communication)
        if (!CollectionUtils.isEmpty(existing)) {
            for (Communication existingCommunication : existing) {
                String existingTitle = getCommunicationTitle(existingCommunication)

                if (existingTitle.equalsIgnoreCase(communicationTitle)) {
                    exists = true
                    break
                }
            }
        }

        if (!exists) {
            logger.debug('Create new communication with this content')
            try {
                communicationResource.create(communication).get()
            } catch (Exception e) {
                logger.error("Error creating communication " + getCommunicationTitle(communication), e)
            }
        } else {
            logger.debug('communication exists, skip')
        }
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    private static String getCommunicationTitle(Communication communication) {
        if (communication == null || communication.locales == null) {
            return ''
        }

        JsonNode jsonNode = communication.locales.get('en_US')
        if (jsonNode == null) {
            return ''
        }

        CommunicationLocale communicationLocale = (CommunicationLocale) JsonHelper.jsonNodeToObj(jsonNode, CommunicationLocale)
        return communicationLocale.name
    }
}
