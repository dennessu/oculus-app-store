package com.junbo.crypto.core.service.impl
import com.junbo.crypto.core.service.KeyStoreService
import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.springframework.util.StringUtils

import java.security.Key
import java.security.KeyStore
import java.security.PublicKey
import java.security.cert.Certificate
/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
@SuppressWarnings('EmptyCatchBlock')
class KeyStoreServiceImpl implements KeyStoreService {
    private static final String DEFAULT_KEY_STORE_TYPE = 'jks'

    // The aliases for keyStore to load
    // We will load all the largest version's alias to encrypt and decrypt the latest data;
    // We will try to decrypt the data from the largest version to the least version.
    // If no cert alias found, throw exception;
    // If cert expires, it won't be used any more.
    private Map<Integer, String> keyAliasMap = new HashMap<>()

    // This is used to load keyStore
    private Map<Integer, String> keyPasswordMap = new HashMap<>()

    private KeyStore keyStore

    KeyStoreServiceImpl(String keyStorePath, String keyStorePassword, String keyAliases, String keyPasswords,
                        Boolean enableEncrypt) {
        assert keyStorePath != null
        assert keyStorePassword != null
        assert keyAliases != null
        assert keyPasswords != null
        assert enableEncrypt != null

        if (enableEncrypt != true) {
            return
        }
        initKeyAliasesAndPassword(keyAliases, keyPasswords)
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

        Certificate cert = keyStore.getCertificate(alias)
        if (cert == null) {
            throw new RuntimeException('cert ' + alias + ' not found.')
        }
        return cert.publicKey
    }

    private Key getPrivateKey(KeyStore keyStore, String alias, String password) {
        assert keyStore != null

        return keyStore.getKey(alias, password.toCharArray())
    }

    private void initKeyStore(String keyStorePath, String keyStorePassword) {
        assert keyStorePath != null
        assert keyStorePassword != null

        this.keyStore = KeyStore.getInstance(DEFAULT_KEY_STORE_TYPE)
        InputStream input = new FileInputStream(getAbsoluteKeyStorePath(keyStorePath))
        this.keyStore.load(input, keyStorePassword.toCharArray())
    }

    private void initKeyAliasesAndPassword(String keyAliases, String keyPasswords) {
        assert keyAliases != null
        assert keyPasswords != null

        String[] aliases = keyAliases.split(';')
        String[] passwords = keyPasswords.split(';')

        if (aliases.length != passwords.length) {
            throw new IllegalStateException('aliases and passwords number doesn\'t match')
        }

        int length = aliases.length
        for (int index = 0; index < length; index++) {
            keyAliasMap.put(index + 1, aliases[index])
            keyPasswordMap.put(index + 1, passwords[index])
        }
    }

    private String getAbsoluteKeyStorePath(String keyStorePath) {
        String javaHome = System.properties.getProperty('java.home')

        return FilenameUtils.normalize(javaHome + '/' + keyStorePath)
    }
}
