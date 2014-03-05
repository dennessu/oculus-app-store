/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.model

import groovy.transform.CompileStatic

import javax.validation.constraints.Null

/**
 * Base Model.
 */
@CompileStatic
class Model {
    @Null
    String createdBy
    @Null
    Date createdTime
    @Null
    String modifiedBy
    @Null
    Date modifiedTime
}
