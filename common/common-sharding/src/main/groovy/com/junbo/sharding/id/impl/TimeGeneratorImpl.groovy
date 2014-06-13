/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.impl

import groovy.transform.CompileStatic

/**
 * Java doc for TimeGeneratorImpl.
 */
@CompileStatic
class TimeGeneratorImpl implements TimeGenerator {

    @Override
    int currentTimeSec(int timeSecOffset) {
        return (int) (((int)(System.currentTimeMillis() / 1000) - timeSecOffset) & Integer.MAX_VALUE)
    }
}
