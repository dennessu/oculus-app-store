/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.common.id.TosId;

import java.util.Date;

/**
 * The AcceptTosResponse class.
 */
public class AcceptTosResponse {

    private TosId tos;

    private Date acceptedTime;

    public TosId getTos() {
        return tos;
    }

    public void setTos(TosId tos) {
        this.tos = tos;
    }

    public Date getAcceptedTime() {
        return acceptedTime;
    }

    public void setAcceptedTime(Date acceptedTime) {
        this.acceptedTime = acceptedTime;
    }
}
