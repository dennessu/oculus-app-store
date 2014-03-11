/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.impl;

/**
 * Java doc for TimeGenerator.
 */
public interface TimeGenerator {

    int currentTimeSec(int timeSecOffset);
}
