/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.oculus

import groovy.transform.CompileStatic

/**
 * Java doc for interface OculusGlobalCounter.
 */
@CompileStatic
interface OculusGlobalCounter {

    int getAndIncrease(int dataCenterId, int shardId, int idType)
}
