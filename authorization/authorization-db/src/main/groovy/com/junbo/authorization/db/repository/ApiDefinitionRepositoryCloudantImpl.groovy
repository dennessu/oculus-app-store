/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.authorization.db.repository

import com.junbo.authorization.spec.model.ApiDefinition
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews

/**
 * Created by Zhanxin on 5/23/2014.
 */
class ApiDefinitionRepositoryCloudantImpl extends CloudantClient<ApiDefinition> implements ApiDefinitionRepository {
    @Override
    ApiDefinition getApi(String apiName) {
        return cloudantGet(apiName).get()
    }

    @Override
    ApiDefinition saveApi(ApiDefinition api) {
        return cloudantPost(api).get()
    }

    @Override
    ApiDefinition updateApi(ApiDefinition api) {
        return cloudantPut(api).get()
    }

    @Override
    void deleteApi(ApiDefinition api) {
        cloudantDelete(api.apiName).get()
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }
}
