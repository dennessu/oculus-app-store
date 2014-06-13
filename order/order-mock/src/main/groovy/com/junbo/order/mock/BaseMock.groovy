/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.mock

import groovy.transform.CompileStatic

/**
 * Created by fzhang on 14-3-4.
 */
@CompileStatic
class BaseMock {

    private static long nextLong = System.currentTimeMillis()

    static final String GRANT_ENTITLEMENT = 'GRANT_ENTITLEMENT'
    static final String DELIVER_PHYSICAL_GOODS = 'DELIVER_PHYSICAL_GOODS'
    static final String CREDIT_WALLET = 'CREDIT_WALLET'

    protected static long generateLong() {
        return nextLong++
    }

    protected static String generateString() {
        return String.valueOf(nextLong++)
    }
}
