/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.util;

import com.junbo.identity.spec.error.AppErrors;
import com.junbo.identity.data.identifiable.UserPasswordStrength;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Password DAO Util.
 * Password Strength level can be defined as follows:
 * 1) Invalid Password:
 *          a.  Contains space;
 *          b.  Empty or null string;
 *          c.  Single character repeated;
 *          d.  In black list;
 * 2) Fair Password: (It should be satisfied with one of those rules:)
 *          a.  It must be between 8 and 16;
 *          b.  digital number, lower case, upper case and other character set, it contains at least 2 of them;
 * 3) Strong Password: (It should satisfy all the following rules:)
 *          a.  It must be between 8 and 16;
 *          b.  It is at least 1 digital, 1 lower case and 1 upper case;
 * 4) Weak Password:
 *          a.  Not in the above 1), 2), 3)
 *
 */
public class PasswordDAOUtil {
    // Need to change this according to the passwordRule configuration.
    private PasswordDAOUtil() {

    }

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

    public static UserPasswordStrength calcPwdStrength(String password) {
        return getUserPasswordStrength(password);
    }

    public static String hashPassword(String password, String passwordSalt) {
        String passwordHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(passwordSalt.getBytes());
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
            throw AppErrors.INSTANCE.invalidPassword("Password is null or empty.").exception();
        }

        if(!isValidLength(password)) {
            throw AppErrors.INSTANCE.
                    invalidPassword("Password should be between " + MIN_LENGTH + " and " + MAX_LENGTH).exception();
        }

        if(isNotAllowedContain(password)) {
            throw AppErrors.INSTANCE.invalidPassword("Password should not contain " + NOT_ALLOWED_PATTEN).exception();
        }

        if(isInBlackList(password)) {
            throw AppErrors.INSTANCE.invalidPassword("Current password is in black list.").exception();
        }

        if(isSingleCharacterRepeated(password)) {
            throw AppErrors.INSTANCE.invalidPassword("Current password is single character repeated.").exception();
        }
    }

    private static UserPasswordStrength getUserPasswordStrength(String s) {
        if(s.length() < MIN_FAIR_STRONG_LENGTH || s.length() > MAX_LENGTH) {
            return UserPasswordStrength.WEAK;
        }

        int score = getPasswordScore(s);

        if(score >= 6) {
            return UserPasswordStrength.STRONG;
        }
        else if (score >= 3) {
            return UserPasswordStrength.FAIR;
        }
        else {
            return UserPasswordStrength.WEAK;
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
}
