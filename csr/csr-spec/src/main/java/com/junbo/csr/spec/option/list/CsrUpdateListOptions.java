/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.option.list;

import javax.ws.rs.QueryParam;

/**
 * Created by haomin on 14-7-4.
 */
public class CsrUpdateListOptions extends PagingGetOptions {

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @QueryParam("status")
    private String status;
}
