/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.common.util;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by liangfu on 5/14/14.
 */
public class HashHelper {
    private HashHelper() {

    }

    public static String shaHash(String password, String passwordSalt, String algorithm) {
        String passwordHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            if (!StringUtils.isEmpty(passwordSalt)) {
                md.update(passwordSalt.getBytes());
            }
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            passwordHash = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm " + algorithm);
        }
        return passwordHash;
    }

    public static String bcryptHash(String password, String passwordSalt) {
        try {
            return BCrypt.hashpw(password, passwordSalt);
        } catch (Exception e) {
            throw new IllegalStateException("BCrypt hash error");
        }
    }
}
