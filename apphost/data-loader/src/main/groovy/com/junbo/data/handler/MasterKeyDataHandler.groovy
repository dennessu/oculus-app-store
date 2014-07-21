package com.junbo.data.handler

import com.junbo.crypto.spec.model.MasterKey
import com.junbo.crypto.spec.resource.MasterKeyResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource

/**
 * Created by liangfu on 6/26/14.
 */
@CompileStatic
class MasterKeyDataHandler extends BaseDataHandler {

    private MasterKeyResource masterKeyResource

    @Required
    void setMasterKeyResource(MasterKeyResource masterKeyResource) {
        this.masterKeyResource = masterKeyResource
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Override
    void handle(String content) {
        try {
            masterKeyResource.create().get()
        } catch (Exception e) {
            logger.error("Error creating masterKey", e)
        }
    }
}
