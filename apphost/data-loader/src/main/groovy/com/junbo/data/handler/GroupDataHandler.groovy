package com.junbo.data.handler

import com.junbo.identity.spec.v1.resource.GroupResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource

/**
 * Created by haomin on 14-7-11.
 */
@CompileStatic
class GroupDataHandler extends BaseDataHandler {
    private GroupResource groupResource

    @Required
    void setGroupResource(GroupResource groupResource) {
        this.groupResource = groupResource
    }

    @Override
    void handle(String content) {

    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }
}
