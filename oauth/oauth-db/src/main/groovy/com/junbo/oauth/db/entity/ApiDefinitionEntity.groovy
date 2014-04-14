/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.entity

import com.junbo.oauth.spec.model.MatrixRow
import groovy.transform.CompileStatic

/**
 * ApiDefinitionEntity.
 */
@CompileStatic
class ApiDefinitionEntity extends BaseEntity {

    Map<String, List<MatrixRow>> scopes
}
