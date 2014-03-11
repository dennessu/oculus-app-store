/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.impl;

/**
 * Java doc for TimeGeneratorImpl.
 */
public class TimeGeneratorImpl implements TimeGenerator {

    @Override
    public int currentTimeSec(int timeSecOffset) {
        return (int) ((System.currentTimeMillis() / 1000 - timeSecOffset) & Integer.MAX_VALUE);
    }
}
