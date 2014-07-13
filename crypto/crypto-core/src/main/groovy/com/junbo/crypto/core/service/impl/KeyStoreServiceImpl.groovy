package com.junbo.crypto.core.service.impl

import com.junbo.configuration.ConfigServiceManager
import com.junbo.crypto.core.service.KeyStoreService
import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils

import javax.xml.bind.DatatypeConverter
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
    private static final Logger logger = LoggerFactory.getLogger(KeyStoreServiceImpl.class)
    private static final String DEFAULT_KEY_STORE_TYPE = 'jks'
    private static final String KEY_STORE_FROM_FILE = "file://"
    private static final String KEY_STORE_INCLUDED = "inline://"

    // The aliases for keyStore to load
    // We will load all the largest version's alias to encrypt and decrypt the latest data;
    // We will try to decrypt the data from the largest version to the least version.
    // If no cert alias found, throw exception;
    // If cert expires, it won't be used any more.
    private Map<Integer, String> keyAliasMap = new HashMap<>()

    // This is used to load keyStore
    private Map<Integer, String> keyPasswordMap = new HashMap<>()

    private KeyStore keyStore

    KeyStoreServiceImpl(String keyStore, String keyStorePassword, String keyAliases, String keyPasswords) {
        assert keyStore != null
        assert keyStorePassword != null
        assert keyAliases != null
        assert keyPasswords != null

        initKeyAliasesAndPassword(keyAliases, keyPasswords)
        initKeyStore(keyStore, keyStorePassword)
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

    private void initKeyStore(String keyStore, String keyStorePassword) {
        assert keyStore != null
        assert keyStorePassword != null

        this.keyStore = KeyStore.getInstance(DEFAULT_KEY_STORE_TYPE)

        InputStream input = null;
        if (keyStore.startsWith(KEY_STORE_FROM_FILE)) {
            String keyStorePath = keyStore.substring(KEY_STORE_FROM_FILE.length());
            input = new FileInputStream(getAbsoluteKeyStorePath(keyStorePath))
        } else if (keyStore.startsWith(KEY_STORE_INCLUDED)) {
            String keyStoreHex = keyStore.substring(KEY_STORE_INCLUDED.length());
            byte[] keyStoreBin = DatatypeConverter.parseHexBinary(keyStoreHex);
            input = new ByteArrayInputStream(keyStoreBin);
        }
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
        String configDir = ConfigServiceManager.instance().getConfigPath()
        return FilenameUtils.normalize(configDir + '/' + keyStorePath)
    }
}
