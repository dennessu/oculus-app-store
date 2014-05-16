/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service

import com.junbo.oauth.spec.model.ApiDefinition
import groovy.transform.CompileStatic

/**
 * ApiService.
 */
@CompileStatic
interface ApiService {
    ApiDefinition getApi(String apiName)

    List<ApiDefinition> getAllApis()

    ApiDefinition saveApi(ApiDefinition apiDefinition)

    ApiDefinition updateApi(String apiName, ApiDefinition apiDefinition)

    void deleteApi(String apiName)
}
