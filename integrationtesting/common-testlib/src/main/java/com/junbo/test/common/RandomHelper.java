/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author dw
 */
public class RandomHelper {

    private RandomHelper() {

    }

    public static String randomAlphabetic(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

}
