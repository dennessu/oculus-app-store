/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.data.entity;

/**
 * Created by liangfu on 5/6/14.
 */
public class UserCryptoKeyEntity {
    private Long userId;

    private Integer cryptoVersion;

    // The user encrypted key
    private String cryptoValue;
}