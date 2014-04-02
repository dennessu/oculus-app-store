/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo

import com.junbo.oauth.spec.model.ApiDefinition
import groovy.transform.CompileStatic

/**
 * ApiDefinitionRepository.
 */
@CompileStatic
interface ApiDefinitionRepository {
    ApiDefinition getApi(String apiName)

    List<ApiDefinition> getAllApis()

    ApiDefinition saveApi(ApiDefinition api)

    ApiDefinition updateApi(ApiDefinition api)

    void deleteApi(ApiDefinition api)
}
