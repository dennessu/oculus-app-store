package com.junbo.data.handler

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.PITypeId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.identity.spec.v1.resource.PITypeResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-6-3.
 */
@CompileStatic
class PITypeDataHandler extends BaseDataHandler {
    private PITypeResource piTypeResource

    @Required
    void setPiTypeResource(PITypeResource piTypeResource) {
        this.piTypeResource = piTypeResource
    }

    @Override
    void handle(String content) {
        PIType piType

        try {
            piType = transcoder.decode(new TypeReference<PIType>() {}, content) as PIType
        } catch (Exception e) {
            logger.warn('Error parsing pitype, skip this content:' + content, e)
            return
        }

        Results<PIType> results = null
        try {
            results = piTypeResource.list(new PITypeListOptions(typeCode: piType.typeCode)).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        if (results != null && results.items != null && !results.items.empty) {
            PIType existing = results.items.get(0)
            if (alwaysOverwrite) {
                logger.debug("Overwrite PIType $piType.typeCode with this content")
                piType.id = (PITypeId)existing.id
                piType.rev = existing.rev
                piTypeResource.put((PITypeId)existing.id, piType)
            } else {
                logger.debug("$piType.typeCode already exists, skipped!")
            }
        } else {
            logger.debug('Create new piType with this content')
            piTypeResource.create(piType)
        }
    }
}
