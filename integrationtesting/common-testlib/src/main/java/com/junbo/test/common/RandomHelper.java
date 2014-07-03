/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author dw
 */
public class RandomHelper {

    private RandomHelper() {

    }

    public static String randomAlphabetic(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static String randomNumeric(int count) {
        return RandomStringUtils.randomNumeric(count);
    }

    public static Long randomLong() {
        return new Random().nextLong();
    }

    public static int randomInt() {
        return new Random().nextInt();
    }

    public static Boolean randomBoolean() {
        List<Object> array = new ArrayList<>();
        array.add(true);
        array.add(false);
        return Boolean.parseBoolean(randomValueFromList(randomInt(), array).toString());
    }

    public static Object randomValueFromList(int rand, List<Object> values) {
        int result = Math.abs(rand) % values.size();
        return values.get(result);
    }

    public static String randomIP() {
        return randomNumeric(2) + "." + randomNumeric(2) + "." + randomNumeric(2) + "." + randomNumeric(2);
    }
}
