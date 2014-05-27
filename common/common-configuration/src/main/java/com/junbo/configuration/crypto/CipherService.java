/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.crypto;

import java.security.Key;

/**
 * Created by liangfu on 5/7/14.
 */
public interface CipherService {
    String encrypt(String message, Key key);
    String decrypt(String encryptMessage, Key key);
    String getKeyAlgorithm();
}
