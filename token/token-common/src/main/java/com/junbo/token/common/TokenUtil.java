/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Token Utility.
 */
public class TokenUtil {
    private TokenUtil(){

    }
    //TODO: need random alg and profanity check
    public static String generateToken(String length){
        return generateToken(length, System.currentTimeMillis());
    }

    public static String generateToken(String length, Long seed){
        Random random = new Random(System.currentTimeMillis());
        return String.valueOf(random.nextLong());
    }

    public static List<String> generateToken(String length, Long seed, Long quantity){
        List<String> items = new ArrayList<String>();
        while(quantity > 0){
            items.add(generateToken(length, seed));
            quantity--;
        }
        return items;
    }
}
