package com.junbo.crypto.core.service

import groovy.transform.CompileStatic

import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey

/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
interface KeyStoreService {
    public Map<Integer, PublicKey> getPublicKeys()

    public Map<Integer, PrivateKey> getPrivateKeys()

    public PublicKey getPublicKeyByVersion(Integer version)

    public Key getPrivateKeyByVersion(Integer version)
}