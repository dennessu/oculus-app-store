package com.junbo.data.handler

import com.junbo.crypto.spec.resource.MasterKeyResource
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
            boolean newKey = false
            if (content == "newkey") {
                newKey = true
            }
            masterKeyResource.create(newKey).get()
        } catch (Exception e) {
            logger.error("Error creating masterKey", e)
        }
    }
}
