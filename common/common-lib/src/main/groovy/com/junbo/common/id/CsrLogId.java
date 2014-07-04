/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by haomin on 14-7-3.
 */
@IdResourcePath(value = "/csr-logs/{0}",
        resourceType = "csr-logs",
        regex = "/csr-logs/(?<id>[0-9A-Za-z]+)")
public class CsrLogId extends CloudantId {
    public CsrLogId(){
    }

    public CsrLogId(String value) {
        super(value);
    }
}
