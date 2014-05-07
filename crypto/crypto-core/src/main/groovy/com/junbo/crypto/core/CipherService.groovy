package com.junbo.crypto.core

import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/7/14.
 */
@CompileStatic
interface CipherService {

    public String encrypt(String message, String keyHexString)

    public String decrypt(String encryptMessage, String keyHexString)
}
