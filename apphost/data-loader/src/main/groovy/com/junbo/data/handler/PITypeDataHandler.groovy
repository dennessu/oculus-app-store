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
import org.springframework.core.io.Resource

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
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Override
    void handle(String content) {
        PIType piType
        try {
            piType = transcoder.decode(new TypeReference<PIType>() {}, content) as PIType
        } catch (Exception e) {
            logger.error("Error parsing piType $content", e)
            exit()
        }

        Results<PIType> results = null
        try {
            results = piTypeResource.list(new PITypeListOptions(typeCode: piType.typeCode)).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        if (results != null && results.items != null && !results.items.empty) {
            PIType existing = results.items.get(0)
            logger.debug("Overwrite PIType $piType.typeCode with this content")
            piType.id = (PITypeId) existing.id
            piType.rev = existing.rev
            piType.createdTime = existing.createdTime
            piType.createdBy = existing.createdBy
            piType.updatedBy = existing.updatedBy
            piType.updatedTime = existing.updatedTime
            piType.adminInfo = existing.adminInfo
            try {
                piTypeResource.put((PITypeId) existing.id, piType).get()
            } catch (Exception e) {
                logger.error("Error updating piType $piType.typeCode", e)
            }
        } else {
            logger.debug('Create new piType with this content')
            try {
                piTypeResource.create(piType).get()
            } catch (Exception e) {
                logger.error("Error creating piType $piType.typeCode", e)
            }
        }
    }
}
