/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.common;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Token Utility.
 */
public class TokenUtil {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String VALID_CHARS = "75284396JKLMNPQRABCDEFGHSTUVWXYZ";
    private TokenUtil(){

    }

    public static TokenHash computeHash(String text){
        String hexValue = DigestUtils.sha256Hex(text);
        Long hashValue = Long.parseLong(hexValue.substring(0,14), 16);
        String hashRemaining = hexValue.substring(15);
        return new TokenHash(hashValue, hashRemaining);
    }

    public static String generateToken(int length){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++){
            int index = (int)(SECURE_RANDOM.nextDouble() * VALID_CHARS.length());
            sb.append(VALID_CHARS.charAt(index));
        }
        return sb.toString();
    }

    public static List<String> generateToken(int length, Long quantity){
        List<String> items = new ArrayList<String>();
        while(quantity > 0){
            items.add(generateToken(length));
            quantity--;
        }
        return items;
    }

    public static void main(String[] args){
        for(int i = 0; i < 1000; i++){
            TokenHash tokenHash = TokenUtil.computeHash(TokenUtil.generateToken(20));
            System.out.println(tokenHash.getHashValue());
            System.out.println(tokenHash.getHashRemaining());
        }
    }
}
