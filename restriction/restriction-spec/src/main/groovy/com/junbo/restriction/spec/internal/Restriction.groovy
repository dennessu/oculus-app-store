/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.spec.internal

import groovy.transform.CompileStatic

/**
 * Restriction.
 */
@CompileStatic
class Restriction {
    String country
    List<Rating> ratings
    String type
}
