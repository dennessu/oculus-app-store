/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.core.service

import com.junbo.authorization.spec.model.ApiDefinition
import groovy.transform.CompileStatic

/**
 * ApiService.
 */
@CompileStatic
interface ApiService {
    ApiDefinition getApi(String apiName)
}
