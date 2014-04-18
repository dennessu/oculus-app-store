/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.util;

import com.junbo.identity.data.identifiable.UserPasswordStrength;
import com.junbo.identity.spec.error.AppErrors;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liangfu on 3/16/14.
 */
public class CipherHelper {
    private CipherHelper() {

    }

    private static final String MD5_ALGORITHM = "MD5";
    private static final String SHA_1_ALGORITHM = "SHA-1";
    private static final String SHA_256_ALGORITHM = "SHA-256";
    private static final String HASH_VERSION = "1";

    // Only support English password Only

    private static final Integer MIN_LENGTH = 4;
    private static final Integer MAX_LENGTH = 16;

    private static final Integer MIN_FAIR_STRONG_LENGTH = 4;

    // private final static String STAR = "*";
    private static final String ANY_STRING_PATTERN = ".*";
    private static final String PLUS = "+";
    private static final String NOT_ALLOWED_PATTEN = "[ ]";
    private static final String UPPER_ALPHA_PATTEN = "[A-Z]";
    private static final String LOWER_ALPHA_PATTEN = "[a-z]";
    // private final static String ALPHA_PATTEN = "[A-Za-z]";
    private static final String NUMBER_PATTEN = "[0-9]";
    private static final String SPECIAL_CHARACTER_PATTEN =  "[`~!@#$%^&*()+=|{}\\':;\\',//[//].<>/\" +\n?]";
    public static final String COLON = ":";

    /**
     * Generates hash string from plain-text password, with either the existing salt and pepper for verification
     * or creates randomly generated salt and pepper, if omitted.
     *
     * @param password
     * @param passwordSalt
     * @param passwordPepper
     * @return string - [version]:[salt]:[pepper]:[password hash]
     */
    // todo:    Need to ask Fan about the hash stored in oculus

    public static String generateCipherHashV1(String password, String passwordSalt, String passwordPepper) {
        if (passwordSalt == null || passwordSalt.length() != 20
         || passwordPepper == null || passwordPepper.length() != 20) {
            throw new RuntimeException();
        }
        passwordSalt = passwordSalt.replace(':', 'f');
        passwordPepper = passwordPepper.replace(':', 'f');

        String temp = hash(password, null, MD5_ALGORITHM);
        temp = shuffleString(temp);
        temp = temp + passwordSalt;
        temp = hash(temp, null, SHA_256_ALGORITHM);
        temp = passwordPepper + temp;
        temp = shuffleString(temp);
        temp = hash(temp, null, SHA_1_ALGORITHM);
        temp = shuffleString(temp);
        temp = hash(temp, null, SHA_256_ALGORITHM);
        temp = shuffleString(temp);

        return HASH_VERSION + COLON + passwordSalt + COLON + passwordPepper + COLON + temp;
    }

    public static String generateCipherRandomStr(Integer returnLength) {
        char[] chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < returnLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static String hash(String password, String passwordSalt, String algorithm) {
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
            throw AppErrors.INSTANCE.encryptPassword("NoSuchAlgorithm " + e.getMessage()).exception();
        }
        return passwordHash;
    }

    public static void validatePassword(String password) {
        if(StringUtils.isEmpty(password)) {
            throw AppErrors.INSTANCE.fieldInvalid("password").exception();
        }

        if(!isValidLength(password)) {
            throw AppErrors.INSTANCE.
                    invalidPassword("Password should be between " + MIN_LENGTH + " and " + MAX_LENGTH).exception();
        }

        if(isNotAllowedContain(password)) {
            throw AppErrors.INSTANCE.invalidPassword("Password should not contain " + NOT_ALLOWED_PATTEN).exception();
        }

        if(isInBlackList(password)) {
            throw AppErrors.INSTANCE.invalidPassword("Current password is in black model.").exception();
        }

        if(isSingleCharacterRepeated(password)) {
            throw AppErrors.INSTANCE.invalidPassword("Current password is single character repeated.").exception();
        }
    }

    public static String calcPwdStrength(String s) {
        if(s.length() < MIN_FAIR_STRONG_LENGTH || s.length() > MAX_LENGTH) {
            return UserPasswordStrength.WEAK.toString();
        }

        int score = getPasswordScore(s);

        if(score >= 6) {
            return UserPasswordStrength.STRONG.toString();
        }
        else if (score >= 3) {
            return UserPasswordStrength.FAIR.toString();
        }
        else {
            return UserPasswordStrength.WEAK.toString();
        }
    }

    private static int getPasswordScore(String s) {
        int score = 0;

        if(isNumberContain(s)) {
            score += 2;
        }
        if(isLowerAlphaContain(s)) {
            score += 2;
        }
        if(isUpperAlphaContain(s)) {
            score += 2;
        }
        if(isSpecialCharacterContain(s)) {
            score += 1;
        }

        return score;
    }

    private static boolean isValidLength(String s) {
        if(s.length() >= MIN_LENGTH && s.length() <= MAX_LENGTH) {
            return true;
        }
        return false;
    }

    private static boolean isNotAllowedContain(String s) {
        Pattern p = Pattern.compile(ANY_STRING_PATTERN + NOT_ALLOWED_PATTEN + PLUS + ANY_STRING_PATTERN);
        Matcher matcher = p.matcher(s);
        return matcher.matches();
    }

    private static boolean isSingleCharacterRepeated(String s) {
        Character appearChar = null;
        for(Character c : s.toCharArray()) {
            if(appearChar == null) {
                appearChar = c;
            }
            else {
                int result = appearChar.compareTo(c);
                if(result != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isUpperAlphaContain(String s) {
        Pattern p = Pattern.compile(ANY_STRING_PATTERN + UPPER_ALPHA_PATTEN + PLUS + ANY_STRING_PATTERN);
        Matcher matcher = p.matcher(s);
        return matcher.matches();
    }

    private static boolean isLowerAlphaContain(String s) {
        Pattern p = Pattern.compile(ANY_STRING_PATTERN + LOWER_ALPHA_PATTEN + PLUS + ANY_STRING_PATTERN);
        Matcher matcher = p.matcher(s);
        return matcher.matches();
    }

    private static boolean isSpecialCharacterContain(String s) {
        Pattern p = Pattern.compile(ANY_STRING_PATTERN + SPECIAL_CHARACTER_PATTEN + PLUS + ANY_STRING_PATTERN);
        Matcher matcher = p.matcher(s);
        return matcher.matches();
    }

    private static boolean isNumberContain(String s) {
        Pattern p = Pattern.compile(ANY_STRING_PATTERN + NUMBER_PATTEN + PLUS + ANY_STRING_PATTERN);
        Matcher matcher = p.matcher(s);
        return matcher.matches();
    }

    private static boolean isInBlackList(String s) {
        // Todo: fetch blackList from DB
        return false;
    }

    private static String shuffleString(String s) {
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        for (int i=0; i<s.length(); i++) {
            if (i%2 == 0) {
                sb1.append(s.charAt(i));
            } else {
                sb2.append(s.charAt(i));
            }
        }
        return sb1.toString() + sb2.toString();
    }
}
