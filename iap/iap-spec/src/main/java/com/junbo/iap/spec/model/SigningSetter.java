/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.iap.spec.model;

/**
 * The SigningSetter class.
 */
public interface SigningSetter {

    void setPayload(String payload);

    void setSignature(String signature);
}
