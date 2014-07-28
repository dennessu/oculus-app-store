/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.crypto.apihelper;

/**
 * Created by weiyu_000 on 7/28/14.
 */
public interface CryptoService {

    String encryptCryptoMessage(String msg) throws Exception;

    String encryptCryptoMessage(String msg, int expectedResponseCode) throws Exception;

    String encryptCryptoMessage(String uid, String msg) throws Exception;

    String encryptCryptoMessage(String uid, String msg, int expectedResponseCode) throws Exception;

    String decryptCryptoMessage(String msg) throws Exception;

    String decryptCryptoMessage(String msg, int expectedResponseCode) throws Exception;

    String decryptCryptoMessage(String uid, String msg) throws Exception;

    String decryptoCryptoMessage(String uid, String msg, int expectedResponseCode) throws Exception;

}
