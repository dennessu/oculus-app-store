/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.authorization.db.repository
import com.junbo.authorization.spec.model.ApiDefinition
import com.junbo.common.cloudant.CloudantClient
import groovy.transform.CompileStatic

/**
 * Created by Zhanxin on 5/23/2014.
 */
@CompileStatic
class ApiDefinitionRepositoryCloudantImpl extends CloudantClient<ApiDefinition> implements ApiDefinitionRepository {
    @Override
    ApiDefinition getApi(String apiName) {
        return cloudantGetSync(apiName)
    }

    @Override
    ApiDefinition saveApi(ApiDefinition api) {
        return cloudantPostSync(api)
    }

    @Override
    ApiDefinition updateApi(ApiDefinition api, ApiDefinition oldApi) {
        return cloudantPutSync(api, oldApi)
    }
}
