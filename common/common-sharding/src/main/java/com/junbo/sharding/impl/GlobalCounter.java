/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.impl;

/**
 * Java doc for interface GlobalCounter.
 */
public interface GlobalCounter {

    int getAndIncrease(int shardId, int timeSec, int optionMode);
}
