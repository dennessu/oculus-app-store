/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.oculus;

/**
 * Java doc for interface OculusGlobalCounter.
 */
public interface OculusGlobalCounter {

    int getAndIncrease(int shardId, int idType);
}
