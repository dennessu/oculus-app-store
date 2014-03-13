/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.model;

import javax.ws.rs.QueryParam;

/**
 * token order search model.
 */
public class TokenOrderSearchParam {
    @QueryParam("type")
    private String type;
    @QueryParam("status")
    private String status;


    public TokenOrderSearchParam() {
    }

    public TokenOrderSearchParam(String type, String status) {
        this.type = type;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
