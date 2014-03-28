/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service.impl

import com.junbo.oauth.core.service.ApiService
import com.junbo.oauth.spec.model.ApiDefinition
import groovy.transform.CompileStatic

/**
 * ApiServiceImpl.
 */
@CompileStatic
class ApiServiceImpl implements ApiService {
    @Override
    ApiDefinition getApi(String authorization, String apiName) {
        return null
    }

    @Override
    List<ApiDefinition> getAllApis(String authorization) {
        return []
    }

    @Override
    ApiDefinition createApi(String authorization, ApiDefinition apiDefinition) {
        return null
    }

    @Override
    ApiDefinition updateApi(String authorization, String apiName, ApiDefinition apiDefinition) {
        return null
    }

    @Override
    void deleteApi(String authorization, String apiName) {

    }
}
