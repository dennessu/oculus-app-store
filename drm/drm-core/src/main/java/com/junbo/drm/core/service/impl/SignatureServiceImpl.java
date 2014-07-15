/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.core.service.impl;

import com.junbo.configuration.ConfigServiceManager;
import com.junbo.drm.core.service.SignatureService;
import com.junbo.drm.spec.error.AppErrors;
import com.junbo.utils.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.*;

/**
 * SignatureServiceImpl.
 */
public class SignatureServiceImpl implements SignatureService, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureServiceImpl.class);
    private static final String DEFAULT_KEY_STORE_TYPE = "jks";
    private static final String KEY_STORE_FROM_FILE = "file://";
    private static final String KEY_STORE_INCLUDED = "inline://";

    private PrivateKey privateKey;
    private String keyStorePath;
    private String keyStorePassword;
    private String keyAlias;
    private String keyPassword;
    private String algorithm;

    @Required
    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    @Required
    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    @Required
    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    @Required
    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    @Required
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public byte[] sign(byte[] bytes) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey);
            signature.update(bytes);
            return signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            LOGGER.error("Exception happens during signing the content", e);
            throw AppErrors.INTSTANCE.signatureError().exception();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(DEFAULT_KEY_STORE_TYPE);

        InputStream input = null;
        if (keyStorePath.startsWith(KEY_STORE_FROM_FILE)) {
            String path = keyStorePath.substring(KEY_STORE_FROM_FILE.length());
            String keyStoreAbsolutePath = getAbsoluteKeyStorePath(path);
            FileUtils.checkPermission600(Paths.get(keyStoreAbsolutePath));
            input = new FileInputStream(keyStoreAbsolutePath);
        } else if (keyStorePath.startsWith(KEY_STORE_INCLUDED)) {
            String keyStoreHex = keyStorePath.substring(KEY_STORE_INCLUDED.length());
            byte[] keyStoreBin = DatatypeConverter.parseHexBinary(keyStoreHex);
            input = new ByteArrayInputStream(keyStoreBin);
        }

        keyStore.load(input, keyStorePassword.toCharArray());

        privateKey = (PrivateKey)keyStore.getKey(keyAlias, keyPassword.toCharArray());
    }

    private String getAbsoluteKeyStorePath(String keyStorePath) {
        String configDir = ConfigServiceManager.instance().getConfigPath();
        return FilenameUtils.normalize(configDir + '/' + keyStorePath);
    }
}
