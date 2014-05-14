package com.junbo.crypto.core.service.impl

import com.junbo.crypto.core.service.KeyStoreService
import com.junbo.crypto.spec.error.AppErrors
import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.springframework.util.StringUtils

import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateException

/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
class KeyStoreServiceImpl implements KeyStoreService {
    private static final String DEFAULT_KEY_STORE_TYPE = 'jks'

    // todo:    To change this to use the reverse lookup properties
    // The aliases for keyStore to load
    // We will load all the largest version's alias to encrypt and decrypt the latest data;
    // We will try to decrypt the data from the largest version to the least version.
    // If no cert alias found, throw exception;
    // If cert expires, it won't be used any more.
    private Map<Integer, String> keyAliasMap

    // This is used to load keyStore
    private Map<Integer, String> keyPasswordMap

    private KeyStore keyStore

    KeyStoreServiceImpl(String keyStorePath, String keyStorePassword, Map<Integer, String> keyAliasMap,
                        Map<Integer, String> keyPasswordMap) {
        assert keyStorePath != null
        assert keyStorePassword != null
        assert keyAliasMap != null
        assert keyPasswordMap != null
        this.keyAliasMap = keyAliasMap
        this.keyPasswordMap = keyPasswordMap
        initKeyStore(keyStorePath, keyStorePassword)
    }

    @Override
    Map<Integer, PublicKey> getPublicKeys() {
        Map<Integer, PublicKey> publicKeys = new HashMap<>()
        Iterator iterator = this.keyAliasMap.iterator()
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> alias = (Map.Entry<Integer, String>)iterator.next()
            if (!StringUtils.isEmpty(alias.value)) {
                publicKeys.put(alias.key, getPublicKey(keyStore, alias.value))
            }
        }

        return publicKeys
    }

    @Override
    Map<Integer, Key> getPrivateKeys() {
        Map<Integer, Key> privateKeys = new HashMap<>()
        Iterator iterator = this.keyAliasMap.iterator()
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> alias = (Map.Entry<Integer, String>)iterator.next()
            String password = this.keyPasswordMap.get(alias.key)
            if (!StringUtils.isEmpty(alias.value) && !StringUtils.isEmpty(password)) {
                privateKeys.put(alias.key, getPrivateKey(keyStore, alias.value, password))
            }
        }
        return privateKeys
    }

    @Override
    PublicKey getPublicKeyByVersion(Integer version) {
        assert keyStore != null
        assert keyAliasMap != null

        String alias = keyAliasMap.get(version)
        return getPublicKey(keyStore, alias)
    }

    @Override
    Key getPrivateKeyByVersion(Integer version) {
        assert keyStore != null
        assert keyAliasMap != null
        assert keyPasswordMap != null

        String alias = keyAliasMap.get(version)
        String password = keyPasswordMap.get(version)
        return getPrivateKey(keyStore, alias, password)
    }

    private PublicKey getPublicKey(KeyStore keyStore, String alias) {
        assert keyStore != null
        assert alias != null
        try {
            Certificate cert = keyStore.getCertificate(alias)
            if (cert == null) {
                throw AppErrors.INSTANCE.certificateException('cert ' + alias + ' not found.').exception()
            }
            return cert.publicKey
        } catch (KeyStoreException ksex) {
            throw AppErrors.INSTANCE.keyStoreException(ksex.message).exception()
        }
    }

    private Key getPrivateKey(KeyStore keyStore, String alias, String password) {
        assert keyStore != null
        try {
            return keyStore.getKey(alias, password.toCharArray())
        } catch (KeyStoreException ksex) {
            throw AppErrors.INSTANCE.keyStoreException(ksex.message).exception()
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw AppErrors.INSTANCE.noSuchAlgorithmException(noSuchAlgorithmException.message).exception()
        } catch (UnrecoverableKeyException unrecoverableKeyEx) {
            throw AppErrors.INSTANCE.unrecoverableKeyException(unrecoverableKeyEx.message).exception()
        }
    }

    private void initKeyStore(String keyStorePath, String keyStorePassword) {
        assert keyStorePath != null
        assert keyStorePassword != null

        try {
            this.keyStore = KeyStore.getInstance(DEFAULT_KEY_STORE_TYPE)
            InputStream input = new FileInputStream(getAbsoluteKeyStorePath(keyStorePath))
            this.keyStore.load(input, keyStorePassword.toCharArray())
        } catch (KeyStoreException ksex) {
            throw AppErrors.INSTANCE.keyStoreException(ksex.message).exception()
        } catch (IOException ioEx) {
            throw AppErrors.INSTANCE.ioException(ioEx.message).exception()
        } catch (NoSuchAlgorithmException noSuchAlgoEx) {
            throw AppErrors.INSTANCE.noSuchAlgorithmException(noSuchAlgoEx.message).exception()
        } catch (CertificateException certEx) {
            throw AppErrors.INSTANCE.certificateException(certEx.message).exception()
        }
    }

    private String getAbsoluteKeyStorePath(String keyStorePath) {
        String javaHome = System.properties.getProperty('java.home')

        return FilenameUtils.normalize(javaHome + '/' + keyStorePath)
    }
}
