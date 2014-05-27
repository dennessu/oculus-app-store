/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.crypto;

/**
 * Created by liangfu on 5/26/14.
 */
public class HexHelper {
    private HexHelper() {

    }

    public static String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (int index = 0; index < bytes.length; index++) {
            sb.append(byteToChar((byte) (bytes[index] & 0x0F)));
            sb.append(byteToChar((byte) ((bytes[index] & 0xF0) >> 4)));
        }

        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] bytes = new byte[s.length() / 2];
        for (int index = 0; index < s.length(); ) {
            byte tempLow = extractByte(s.charAt(index));
            byte tempHigh = extractByte(s.charAt(index + 1));

            byte value = (byte) (tempHigh << 4 | tempLow);
            bytes[index/2] = value;
            index += 2;
        }

        return bytes;
    }

    public static byte extractByte(char c) {
        if (c >= '0' && c <= '9') {
            return (byte) ((c - '0') & 0x0F);
        }

        if (c >= 'A' && c <= 'F') {
            return (byte) (((c - 'A') & 0x0F) + 0x0A);
        }

        throw new IllegalArgumentException("Incorrect hex character");
    }

    public static char byteToChar(byte b) {
        if (b < 0 || b >= 16) {
            throw new IllegalArgumentException("byte is too large [0..15].");
        }

        if (b >= 0 && b <= 9) {
            return (char) ('0' + b);
        }

        return (char)('A' + b - 10);
    }

}
