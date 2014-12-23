/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core;

import com.junbo.common.error.AppCommonErrors;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HashUtil.
 */
public class HashUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HashUtil.class);

    public static String hash(String value) {
        try {
            // MessageDigest is not thread safe, always create new instance per usage.
            // getInstance is not that expensive.
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            return Hex.encodeHexString(md.digest(value.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error happened while hashing the key", e);
            throw AppCommonErrors.INSTANCE.internalServerError(e).exception();
        }
    }
}
