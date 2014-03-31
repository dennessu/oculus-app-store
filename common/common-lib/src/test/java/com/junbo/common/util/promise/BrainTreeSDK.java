/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util.promise;

/**
 * BrainTreeSDK.
 */
public class BrainTreeSDK {
    public static void authorize() {

    }

    public static Long charge(double amount, String currency) {
        if (amount < 0) {
            throw new RuntimeException("too small!");
        }

        return 123L;
    }
}
