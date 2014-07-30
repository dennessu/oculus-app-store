/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.rest

import groovy.transform.CompileStatic

/**
 * ResourceScopeValidator.
 */
@CompileStatic
interface ResourceScopeValidator {
    void validateScope(String apiName)
}
