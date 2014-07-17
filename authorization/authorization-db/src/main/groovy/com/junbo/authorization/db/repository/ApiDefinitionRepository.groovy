/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.db.repository

import com.junbo.authorization.spec.model.ApiDefinition
import groovy.transform.CompileStatic

/**
 * ApiDefinitionRepository.
 */
@CompileStatic
interface ApiDefinitionRepository {
    ApiDefinition getApi(String apiName)

    ApiDefinition saveApi(ApiDefinition api)

    ApiDefinition updateApi(ApiDefinition api, ApiDefinition oldApi)

    void deleteApi(ApiDefinition api)
}
