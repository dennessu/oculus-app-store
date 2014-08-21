/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.TosId;

import javax.ws.rs.QueryParam;

/**
 * The GetTocRequest class.
 */
public class AcceptTosRequest {

    @JsonProperty("tos")
    private TosId tosId;

    public TosId getTosId() {
        return tosId;
    }

    public void setTosId(TosId tosId) {
        this.tosId = tosId;
    }
}
