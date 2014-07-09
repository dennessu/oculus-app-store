/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.common;

import com.junbo.common.error.AppCommonErrors;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Token Utility.
 */
public class TokenUtil {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String ITEM_MAP = "75284396JKLMNPQRABCDEFGHSTUVWXYZ";
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
            int index = (int)(SECURE_RANDOM.nextDouble() * ITEM_MAP.length());
            sb.append(ITEM_MAP.charAt(index));
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

    public static <T extends Enum<T>> T getEnumValue(Class<T> enumType, String name){
        try{
            return Enum.valueOf(enumType, name.toUpperCase());
        }catch (Exception ex){
            throw AppCommonErrors.INSTANCE.fieldInvalid(enumType.toString()).exception();
        }
    }

    public static Long getUsage(String usage){
        try{
            return Long.valueOf(usage);
        }catch(Exception ex){
            throw AppCommonErrors.INSTANCE.fieldInvalid("usageLimit").exception();
        }
    }

    public static void main(String[] args){
        for(int i = 0; i < 1000; i++){
            TokenHash tokenHash = TokenUtil.computeHash(TokenUtil.generateToken(20));
            System.out.println(tokenHash.getHashValue());
            System.out.println(tokenHash.getHashRemaining());
        }
    }
}
