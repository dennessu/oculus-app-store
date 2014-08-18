/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.libs;

import com.junbo.test.common.exception.TestException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * @author Jason
 *         Time 3/11/14
 */
public final class RandomFactory {
    private static final String ALPHABET_AND_NUMERIC_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final Random RANDOM = new Random();

    private RandomFactory() {
    }

    public static String getRandomEmailAddress() {
        String email = "silkcloudtest+" + getRandomStringOfAlphabetOrNumeric(9) + String.format("@gmail.com");
        return email;
    }

    public static String getRandomStringOfAlphabetOrNumeric(int stringLength) {
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < stringLength; i++) {
            randomString.append(ALPHABET_AND_NUMERIC_CHARACTERS
                    .charAt(RANDOM.nextInt(ALPHABET_AND_NUMERIC_CHARACTERS.length())));

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
            randomString.append(allowedChars.charAt(RANDOM.nextInt(allowedChars.length())));
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
        return min + Math.abs(getRandomDouble() % num);
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
        return min + Math.abs(random.nextLong() % num);
    }

    public static boolean getRandomBoolean() {
        Random random = new Random();
        int rand = random.nextInt();
        return rand % 2 == 0;
    }

    public static String getRandomIp() {
        return RANDOM.nextInt(256) + "." + RANDOM.nextInt(256) + "." +
                RANDOM.nextInt(256) + "." + RANDOM.nextInt(256);
    }

    public static Calendar nextDate() {
        GregorianCalendar gc = new GregorianCalendar();
        int year = getRandomInteger(1970, 1995);
        gc.set(gc.YEAR, year);
        int dayOfYear = getRandomInteger(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
        gc.set(gc.DAY_OF_YEAR, dayOfYear);
        return gc;
    }

    public static String getRandomStringOfNumeric(int stringLength) {
        Random random = new Random();
        StringBuffer number = new StringBuffer();
        for (int i = 0; i < stringLength; i++) {
            number.append(random.nextInt(10));
        }
        return number.toString();
    }



}
