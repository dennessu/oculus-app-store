/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.mock

/**
 * Created by fzhang on 14-3-4.
 */
class BaseMock {

    private static long nextLong = System.currentTimeMillis()

    protected static long generateLong() {
        return nextLong++
    }
}
