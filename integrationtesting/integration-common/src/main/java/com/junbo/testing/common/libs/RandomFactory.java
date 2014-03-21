/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.libs;

import com.junbo.testing.common.exception.TestException;

import java.util.Random;

/**
 * @author Jason
 *         Time 3/11/14
 */
public final class RandomFactory {
    private static final String alphabetAndNumericCharacters =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final Random random = new Random();
    private static final String TEST_NAMESPACE_NAME = "user";
    private static final Random RANDOMINT = new Random();

    private RandomFactory() {
    }

    /*
    public static String getRandomEmailAddress() {
        int rand1 = RANDOMINT.nextInt(100000);
        int rand2 = RANDOMINT.nextInt(100000);
        String email = rand1 + TEST_NAMESPACE_NAME + GregorianCalendar.getInstance().getTimeInMillis()
                + rand2 + "@ecommerce.com";
        return email;
    }
    */

    public static String getRandomEmailAddress() {
        String email = getRandomStringOfAlphabetOrNumeric(20) + String.format("@wan-san.com");
        return email;
    }

    public static String getRandomStringOfAlphabetOrNumeric(int stringLength) {
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < stringLength; i++) {
            randomString.append(alphabetAndNumericCharacters
                    .charAt(random.nextInt(alphabetAndNumericCharacters.length())));

        }
        return randomString.toString();
    }

    public static String getRandomStringOfAlphabet(int stringLength) {
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < stringLength; i++) {
            randomString.append((char) (int) (getRandomInteger(65, 90)));
        }
        return randomString.toString();
    }

    public static Integer getRandomInteger(int max) {
        return getRandomInteger(0, max);
    }

    public static Integer getRandomInteger(int min, int max) {
        if (min > max) {
            throw new TestException(String.format("minValue: %s is greater than maxValue: %s", min, max));
        }
        Random random = new Random();
        int rand = random.nextInt(max - min);
        return rand + min;
    }

    public static String getRandomStringOfCharacters(int stringLength, String allowedChars) {
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < stringLength; i++) {
            randomString.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }
        return randomString.toString();
    }

    public static Double getRandomDouble() {
        Random random = new Random();
        return random.nextDouble();
    }

    public static Double getRandomDouble(double min, double max) {
        if (min > max) {
            throw new TestException(String.format("minValue: %s is greater than maxValue: %s", min, max));
        }
        Random random = new Random();
        double num = max - min;
        return min + getRandomDouble() * num;
    }

    public static Long getRandomLong(long max) {
        return getRandomLong(0, max);
    }

    public static Long getRandomLong(long min, long max) {
        if (min > max) {
            throw new TestException(String.format("minValue: %s is greater than maxValue: %s", min, max));
        }
        Random random = new Random();
        long num = max - min;
        return min + random.nextLong() * num;
    }

    public static boolean getRandomBoolean() {
        Random random = new Random();
        int rand = random.nextInt();
        return rand % 2 == 0;
    }

    public static String getRandomIp() {
        return random.nextInt(256) + "." + random.nextInt(256) + "." +
                random.nextInt(256) + "." + random.nextInt(256);
    }

}
