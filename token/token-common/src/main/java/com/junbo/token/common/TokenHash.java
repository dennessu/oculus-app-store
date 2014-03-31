/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.common;

/**
 * Token Hash result.
 */
public class TokenHash {
    private Long hashValue;
    private String hashRemaining;
    public TokenHash(Long hashValue, String hashRemaining){
        this.hashValue = hashValue;
        this.hashRemaining = hashRemaining;
    }

    public String getHashRemaining() {
        return hashRemaining;
    }

    public void setHashRemaining(String hashRemaining) {
        this.hashRemaining = hashRemaining;
    }

    public Long getHashValue() {
        return hashValue;
    }

    public void setHashValue(Long hashValue) {
        this.hashValue = hashValue;
    }

}
