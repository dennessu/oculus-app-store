package com.junbo.crypto.core.service

import groovy.transform.CompileStatic

import java.security.Key

/**
 * Created by liangfu on 5/7/14.
 */
@CompileStatic
interface CipherService {

    public String encrypt(String message, Key key)

    public String decrypt(String encryptMessage, Key key)

    public Key stringToKey(String str)
}
