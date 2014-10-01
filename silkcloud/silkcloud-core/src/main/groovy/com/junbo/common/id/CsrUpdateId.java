/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by haomin on 14-7-3.
 */
@IdResourcePath(value = "/csr-updates/{0}",
        resourceType = "csr-updates",
        regex = "/csr-updates/(?<id>[0-9A-Za-z]+)")
public class CsrUpdateId extends CloudantId {
    public CsrUpdateId(){
    }

    public CsrUpdateId(String value) {
        super(value);
    }
}
