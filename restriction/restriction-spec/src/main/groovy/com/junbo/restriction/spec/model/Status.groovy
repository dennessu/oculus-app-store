/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.spec.model

import groovy.transform.CompileStatic

/**
 * Status.
 */
@CompileStatic
enum Status {
    PASSED,
    BANNED,
    BLOCKED
}