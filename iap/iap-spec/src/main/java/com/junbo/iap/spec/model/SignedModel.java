/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.iap.spec.model;

/**
 * Interface indicates the model needs to be signed.
 */
public interface SignedModel {
    void  setSignatureTimestamp(Long timestamp);
}
