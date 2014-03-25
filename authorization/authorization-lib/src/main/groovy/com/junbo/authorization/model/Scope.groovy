/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.model

import groovy.transform.CompileStatic

/**
 * Scope.
 */
@CompileStatic
class Scope {
    Map<Role, Set<String>> claims
}
