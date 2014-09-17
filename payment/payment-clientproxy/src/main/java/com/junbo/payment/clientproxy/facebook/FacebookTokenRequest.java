/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import javax.ws.rs.QueryParam;

/**
 * Facebook Token Request.
 */
public class FacebookTokenRequest {
    @QueryParam("client_id")
    private String clientId;
    @QueryParam("client_secret")
    private String clientSecret;
    @QueryParam("grant_type")
    private final String grantType = "client_credentials";

    public String getGrantType() {
        return grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
